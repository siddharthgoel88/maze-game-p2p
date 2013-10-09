package org.ds.p2p.impl;

import java.rmi.RemoteException;

import org.ds.p2p.GameState;
import org.ds.p2p.BackupUpdates;
import org.ds.p2p.PeerProperties;

public class BackupUpdatesImpl implements BackupUpdates{
	PeerProperties peerProps;
	static GameState backUpGameState;
	static PeerProperties backUpPeerProps;
	
	@Override
	public boolean updateMove(GameState currentState) throws RemoteException {
		backUpGameState = currentState;
		return true;
	}

	public GameState getBackUpGameState() {
		return backUpGameState;
	}

	@Override
	public boolean updatePeerProps(PeerProperties peerProperties)throws RemoteException {
		backUpPeerProps = peerProperties;
		return true;
	}

	public PeerProperties getBackUpPeerProps() {
		return backUpPeerProps;
	}

	@Override
	public boolean updateBckProps(String ip, String port) throws RemoteException {
		P2Player.initPrimaryPoll();
		peerProps = P2Player.getPeerProp();
		peerProps.setBackup(true);
		return true;
	}
}
