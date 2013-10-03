package org.ds.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientHeartBeat extends Remote{
	public boolean updateHeartBeat(String id) throws RemoteException;
}
