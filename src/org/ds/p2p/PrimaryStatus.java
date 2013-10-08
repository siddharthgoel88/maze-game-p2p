package org.ds.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrimaryStatus extends Remote {
	public boolean isPrimaryAlive() throws RemoteException;
}
