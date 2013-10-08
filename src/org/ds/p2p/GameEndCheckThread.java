package org.ds.p2p;

import org.ds.p2p.impl.P2Player;
import org.ds.p2p.impl.PlayerUtils;
import org.ds.p2p.impl.RegistryManager;

public class GameEndCheckThread implements Runnable{

	GameState state;
	GameEndCheck gameEndObj;
	
	@Override
	public void run()
	{
		try {
			gameEndObj = (GameEndCheck) RegistryManager.getPrimaryRegistry().lookup("gameEnd");
		} catch (Exception e) {
			System.out.println("Issues in lookup of gameEnd registry");
			e.printStackTrace();
		}
		
		while(true){
			
			try{
			state = gameEndObj.checker();
			if(state == null){
				Thread.sleep(1000);
			}else{
				System.out.println("\n\n\nGame is over!\n\n\nFinal state is:");
				PlayerUtils.printState(state);
				System.out.println("\n\n\nFollowing is/are the winner(s) :\n");
				
				for(Player p: state.getWinner()){
					System.out.println("Name: "+ p.getName() + "\t# of Treasures: " + p.getNumTreasures());
				}
				
				// Wait for other players to quit before turning of the system
				if( P2Player.getPeerProp().isPrimary ){
					state.setGameOn(false);
					Thread.sleep(2000);
				}	
				System.exit(3);
			  }		
			}
			catch (Exception e) {
				try {
					Thread.sleep(5000); //TODO: remove this delay
				} catch (InterruptedException e1) {
					System.err.println("Game end checker could not sleep");
					e1.printStackTrace();
				}
				System.err.println("Issues in game end checker");
			}
		}
	}
}
