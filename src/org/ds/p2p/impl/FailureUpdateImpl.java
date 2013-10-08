package org.ds.p2p.impl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import org.ds.p2p.FailureUpdate;

public class FailureUpdateImpl implements FailureUpdate {

	@Override
	public boolean updatePrimary(Map<String, String> primaryProp) throws RemoteException {
		String ip = primaryProp.get("ip");
		int port = Integer.parseInt(primaryProp.get("port"));
		System.out.println("IP:" + ip + "Port:"+port);
		Registry reg = LocateRegistry.getRegistry(ip,port);
		RegistryManager.setPrimaryRegistry(reg);
		
		return true;
	}

}
