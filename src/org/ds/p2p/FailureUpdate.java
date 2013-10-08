package org.ds.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface FailureUpdate extends Remote{
	public boolean updatePrimary(Map<String, String> primaryProp) throws RemoteException;
}