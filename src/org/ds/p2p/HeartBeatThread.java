package org.ds.p2p;

import java.rmi.RemoteException;
import org.ds.p2p.ClientHeartBeat;
import org.ds.p2p.Player;

public class HeartBeatThread implements Runnable{
	
	ClientHeartBeat heartBeat;
	Player player;
	
	@Override
	public void run() {
		heartBeat = RMIClientManagerFactory.getRMIClientManager().getHeartBeatStub();
		
		while(true){
			
			try {
				
				heartBeat.updateHeartBeat(player.getId());
				Thread.sleep(10000);
				
			} catch (InterruptedException e) { 
			} catch (RemoteException re){
			}
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
