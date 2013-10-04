package org.ds.p2p.impl;

import java.rmi.RemoteException;

import org.ds.p2p.GameState;
import org.ds.p2p.BackupUpdates;
import org.ds.p2p.PeerProperties;

public class BackupUpdatesImpl implements BackupUpdates{
	GameState backUpGameState;
	PeerProperties backUpPeerProps;
	
	@Override
	public boolean updateMove(GameState currentState) throws RemoteException {
		this.backUpGameState = currentState;
		return true;
	}

	public GameState getBackUpGameState() {
		return backUpGameState;
	}

	@Override
	public boolean updatePeerProps(PeerProperties peerProperties)throws RemoteException {
		System.out.println(peerProperties.getOtherPlayerProps());
		this.backUpPeerProps = peerProperties;
		return true;
	}

	public PeerProperties getBackUpPeerProps() {
		return backUpPeerProps;
	}

	public void setBackUpPeerProps(PeerProperties backUpPeerProps) {
		this.backUpPeerProps = backUpPeerProps;
	}
}
