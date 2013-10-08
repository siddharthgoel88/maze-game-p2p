package org.ds.p2p;

import java.rmi.RemoteException;

import org.ds.p2p.impl.RegistryManager;

public class HeartBeatThread implements Runnable{
	
	ClientHeartBeat heartBeat;
	Player player;
	
	@Override
	public void run() {
		getRemoteObj();
		
		while(true){
			try {
				heartBeat.updateHeartBeat(player.getId());
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException re){
				try {
					System.out.println("Please wait for a moment.");
					Thread.sleep(5000);//TODO: remove this delay
					getRemoteObj();
				} catch (InterruptedException e) {
					System.err.println("Cannot sleep in hearth beat thread");
					e.printStackTrace();
				}
				System.out.println("Yeah, now you can continue");
				// TODO : Just keep a check here. 
			}
		}
	}

	private void getRemoteObj() {
		try {
			heartBeat = (ClientHeartBeat) RegistryManager.getPrimaryRegistry().lookup("heartBeat");
			System.out.println("New Primary bounds:" + RegistryManager.getPrimaryRegistry().list());
		} catch (Exception e1) {
			System.out.println("Issues in heartBeat registry lookup");
			//e1.printStackTrace();
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
