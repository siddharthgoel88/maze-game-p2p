package org.ds.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BackupUpdates extends Remote {
	public boolean updateMove(GameState currentState) throws RemoteException;
	public boolean updatePeerProps(PeerProperties peerProperties) throws RemoteException;
	public boolean updateBckProps(String ip, String port) throws RemoteException;
}
