package org.ds.p2p.impl;

import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.PrimaryStatus;

public class PrimaryStatusChecker implements Runnable{

	PrimaryStatus primaryStatus = null;
	static int noOfTries = 1;
	
	@Override
	public void run() {
		while(true)
		{
			try {
				while(primaryStatus.isPrimaryAlive())
				{
					noOfTries = 1;
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				if(noOfTries != 3)
				{
					System.out.println("Cannot Reach server. Try No. " + noOfTries);
					noOfTries++;
				}
				else
				{
					System.out.println("Primary cannot be reached");
					System.out.println("So backup becoming primary");
					becomePrimary();
					break;
				}
			}
		}
	}

	private void becomePrimary() {
		BackupUpdatesImpl bkp = new BackupUpdatesImpl();
		GameStateFactory.setState(bkp.getBackUpGameState());
		P2Player.setPeerProp(bkp.getBackUpPeerProps());
		P2Player.getPeerProp().setPrimary(true);
		P2Player.getPeerProp().setBackup(false);
	}

	public PrimaryStatus getPrimaryStatus() {
		return primaryStatus;
	}

	public void setPrimaryStatus(PrimaryStatus primaryStatus) {
		this.primaryStatus = primaryStatus;
	}

}
