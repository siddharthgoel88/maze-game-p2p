package org.ds.p2p.impl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ds.p2p.BackupUpdates;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.MoveConstants;
import org.ds.p2p.MovePlayers;
import org.ds.p2p.Player;
import org.ds.p2p.Square;

public class MovePlayersImpl implements MovePlayers{
	GameState state;
	static int maxTreasure=0;
	boolean isAnyBackupAvailable = true;
	
	public MovePlayersImpl(){
		state = GameStateFactory.getGameState();
	}
	
	public synchronized Map<String,Object> move(String id, String move) throws RemoteException {
		Player player = state.getPlayers().get(id);
		BackupUpdates updateBackup = null;
		
		try{
			Registry bkpRegistry = LocateRegistry.getRegistry((String)P2Player.getPeerProp().getSecondaryPeerIp().get("ip"), Integer.parseInt((String)P2Player.getPeerProp().getSecondaryPeerIp().get("port")));
			updateBackup = (BackupUpdates) bkpRegistry.lookup("updateBackup");
		}catch(Exception bkpConException){
			if(state.getNumPlayers() > 2){
				System.out.println("Cannot connect to backup. Nominating new backup.");
				updateBackup = nominateAltBackup();
				if(updateBackup == null){
					isAnyBackupAvailable = false;
				}
			}else{
				System.out.println("Cannot connect to backup. Cannot nominate new backup.");
				isAnyBackupAvailable = false; //TODO: should we stop the game here?
			}
		}
		if(move.equals(MoveConstants.KILL)){
			state.cleanUpPlayer(player);
			System.out.println("Player " + player.getName() + " quited voluntarily");
			updateBackup.updateMove(state);
			return null;
		}
		System.out.println("Player " + player.getPlayerDispId() + " is trying to move.");
		Map<String,Object> map = moveIfValid(player, move);
		
		if(/* !move.equals(MoveConstants.NOMOVE) &&*/ isAnyBackupAvailable )
			updateBackup.updateMove(state);
		return map;
	}
	
	public static BackupUpdates nominateAltBackup() {
		
		HashMap<String, String> playerProps = P2Player.getPeerProp().getOtherPlayerProps();
		Set<String> nextbkp =  playerProps.keySet();
		Registry bkpRegistry = null;
		BackupUpdates updates = null;
		String ip = null , port = null;
		boolean nominateSuccessful = false;
		String nominatedUUID = null;
		
		System.out.println("No of other players:" + P2Player.getPeerProp().getOtherPlayerProps().size());
		
		for( String otherPlayerHosts : nextbkp) {
			
			ip = playerProps.get(otherPlayerHosts).split(":")[0];
			port = playerProps.get(otherPlayerHosts).split(":")[1];
			
			try {
				bkpRegistry = LocateRegistry.getRegistry( ip , Integer.parseInt(port) );
				updates = (BackupUpdates) bkpRegistry.lookup("updateBackup");
				nominateSuccessful = true;
				nominatedUUID = otherPlayerHosts;
				break;
			}  catch (Exception e) {
				System.out.println("Player has quit but system not yet detected the exit. Nominating the next player as backup");
			}
		}
	
		P2Player.getPeerProp().getOtherPlayerProps().remove(nominatedUUID);
		// TODO: Send latest snapshots of state & props
		if(nominateSuccessful){
			P2Player.getPeerProp().getSecondaryPeerIp().put("ip", ip);
			P2Player.getPeerProp().getSecondaryPeerIp().put("port", port);
			System.out.println("Next secondary : " + ip +":" + port);
			
			try {
				updates.updateBckProps(ip,port);
				updates.updateMove(GameStateFactory.getGameState());
				updates.updatePeerProps(P2Player.getPeerProp());
			} catch (RemoteException e) {
				System.out.println("Backup props update failure");
				e.printStackTrace();
			}
		}else{
			System.out.println("No players available for backup! Thanks for your keen interest in the game. Please play again");
			System.exit(4);
		}
		
		return updates;
	}

	private Map<String,Object> moveIfValid(Player player, String move) {
		
		int resCol=player.getCurrentCol(),resRow = player.getCurrentRow();
		Map<String , Object> moveMap = new HashMap<String, Object>();
		Square[][] board = state.getGameBoard(); 
		
		if(move.equals(MoveConstants.NOMOVE)){
			moveMap.put("currentState", state);
			moveMap.put("isSuccessful", "true");
			return moveMap;
		}
		
		if(move.equals(MoveConstants.NORTH)){
			resRow -= 1;
		}else if(move.equals(MoveConstants.WEST)){
			resCol -= 1;
		}else if(move.equals(MoveConstants.EAST)){
			resCol += 1 ;
		}else if(move.equals(MoveConstants.SOUTH)){
			resRow += 1;
		}
		
		if( resRow < 0 || resRow >= state.getBoardSize() || resCol < 0 || resCol >= state.getBoardSize()){
			moveMap.put("errorMessage", "Invalid Move. Boundary crossed");
			moveMap.put("isSuccessful", "false");
		}else if(!board[resRow][resCol].isFree()){
			moveMap.put("errorMessage", "Invalid Move. Another Player present at desired location");
			moveMap.put("isSuccessful", "false");
		}else{
			moveMap.put("isSuccessful", "true");
			board[player.getCurrentRow()][player.getCurrentCol()].setUserId(null);
			board[player.getCurrentRow()][player.getCurrentCol()].setFree(true);
			player.setNumTreasures(player.getNumTreasures()+board[resRow][resCol].getNumTreasures());
			player.setCurrentRow(resRow);
			player.setCurrentCol(resCol);
			board[resRow][resCol].setUserId(player.getId());
			board[resRow][resCol].setFree(false);
			state.setTotalNumTreasures(state.getTotalNumTreasures() - board[resRow][resCol].getNumTreasures());
			board[resRow][resCol].setNumTreasures(0);
		}
		
		if(player.getNumTreasures() >= maxTreasure)
		{
			if(player.getNumTreasures() == maxTreasure){
				if(!state.getWinner().contains(player))
					state.getWinner().add(player);
			}else{
				maxTreasure = player.getNumTreasures();
				state.getWinner().clear();
				state.getWinner().add(player);
			}
		}
		moveMap.put("currentState",state);
		return moveMap;
	}
}