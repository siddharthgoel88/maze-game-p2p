package org.ds.p2p;

import java.rmi.RemoteException;

import org.ds.p2p.impl.RegistryManager;

public class HeartBeatThread implements Runnable{
	
	ClientHeartBeat heartBeat;
	Player player;
	
	@Override
	public void run() {
		try {
			heartBeat = (ClientHeartBeat) RegistryManager.getPrimaryRegistry().lookup("heartBeat");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		while(true){
			try {
				heartBeat.updateHeartBeat(player.getId());
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException re){
				// TODO : Just keep a check here. 
				re.printStackTrace();
			}
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
