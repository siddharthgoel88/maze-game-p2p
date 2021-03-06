package org.ds.p2p;

import org.ds.p2p.impl.P2Player;
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
			e.printStackTrace();
		}
		while(true){
			try{
			state = gameEndObj.checker();
			if(state == null){
				Thread.sleep(1000);
			}else{
				System.out.println("\n\n\nGame is over!\n\n\nFinal state is:");
				P2Player.printState(state);
				System.out.println("\n\n\nFollowing is/are the winner(s) :\n");
				
				for(Player p: state.getWinner()){
					System.out.println("Name: "+ p.getName() + "\t# of Treasures: " + p.getNumTreasures());
				}
				System.exit(3);
			}
						
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
