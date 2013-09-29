package org.ds.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface Bootstrapper extends Remote{
	public Map<String,Object> bootstrap(Map<String,String> playerProperties) throws RemoteException;
}
