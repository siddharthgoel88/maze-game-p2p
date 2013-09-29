package org.ds.p2p.impl;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.MoveConstants;
import org.ds.p2p.MovePlayers;
import org.ds.p2p.Player;
import org.ds.p2p.Square;

public class MovePlayersImpl implements MovePlayers{
	GameState state;
	static int maxTreasure=0;
	
	public MovePlayersImpl(){
		state = GameStateFactory.getGameState();
	}
	
	public synchronized Map<String,Object> move(String id, String move) throws RemoteException {
		Player player = state.getPlayers().get(id);
		
		if(move.equals(MoveConstants.KILL)){
			state.cleanUpPlayer(player);
			System.out.println("Player " + player.getName() + " quited voluntarily");
			return null;
		}
		
		Map<String,Object> map = moveIfValid(player, move);
		return map;
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