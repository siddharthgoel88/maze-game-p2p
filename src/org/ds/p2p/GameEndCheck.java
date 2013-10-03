package org.ds.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameEndCheck extends Remote{
	public GameState checker() throws RemoteException;
}
