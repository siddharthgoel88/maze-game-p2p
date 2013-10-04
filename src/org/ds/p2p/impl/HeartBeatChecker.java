package org.ds.p2p.impl;

import java.util.Collection;

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
					if (p.getLastActiveTime() != 0) {
						if ((curTime - p.getLastActiveTime()) > 10000) {
							System.out.println("Player " + p.getPlayerDispId() + " has quit the game abruptly");
							state.cleanUpPlayer(p);
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
