package org.ds.p2p.impl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;

import org.ds.p2p.BackupUpdates;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.Player;

public class HeartBeatChecker implements Runnable {
	GameState state;

	@Override
	public void run() {
		while (true) {
			
			state = GameStateFactory.getGameState();
			if(state.isGameOn()){
				
				Collection<Player> players = state.getPlayers().values();
				long curTime = System.currentTimeMillis();
				
				for (Player p : players) {
					if(!p.getId().equals(P2Player.getPeerProp().getPrimaryProperties().get("uuid"))){
						if (p.getLastActiveTime() != 0) {
							if ((curTime - p.getLastActiveTime()) > 10000) {
								System.out.println("Player " + p.getPlayerDispId() + " has quit the game abruptly");
								state.cleanUpPlayer(p);
								state.setNumPlayers(state.getNumPlayers()-1);
								P2Player.getPeerProp().getOtherPlayerProps().remove(p.getId());
								
								BackupUpdates updateBackup = null;
								
								try{
									Registry bkpRegistry = LocateRegistry.getRegistry((String)P2Player.getPeerProp().getSecondaryPeerIp().get("ip"), Integer.parseInt((String)P2Player.getPeerProp().getSecondaryPeerIp().get("port")));
									updateBackup = (BackupUpdates) bkpRegistry.lookup("updateBackup");
								}catch(Exception bkpConException){
									if(state.getNumPlayers() >= 2){
										System.out.println("Cannot connect to backup. Nominating new backup.");
										updateBackup = MovePlayersImpl.nominateAltBackup();
									}else{
										System.out.println("Cannot connect to backup. Cannot nominate new backup.");
										System.out.println("Thanks for your interest in our game. Please play again with your friends later.");
										System.exit(4);
									}
								}
								
								try{
									updateBackup.updatePeerProps(P2Player.getPeerProp());
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
					}
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ie) {
					
				}
		  }else{
			  System.out.println("Game end checker disabled");
		  }
		}
	}
}
