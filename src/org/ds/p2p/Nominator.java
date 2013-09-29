package org.ds.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Nominator extends Remote{
	public void nominate(Map<String,Object> gameProps) throws RemoteException;
}
