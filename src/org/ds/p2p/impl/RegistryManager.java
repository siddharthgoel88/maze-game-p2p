package org.ds.p2p.impl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryManager {
	static Registry registry;
	static Registry primaryRegistry;
	
	public static Registry initRegistry(int portNumber){
		try {
			registry = LocateRegistry.createRegistry(portNumber);
		} catch (RemoteException e) {
			System.out.println("Could not initiate registry");
			return null ; //TODO : Not handled
		}
		return registry;
	}
	
	public static Registry getRegistry(){
		return registry;
	}
	
	public static void setRegistry(String ip , int port) throws RemoteException{
		RegistryManager.registry = LocateRegistry.getRegistry(ip, port);
	}

	public static Registry getPrimaryRegistry() {
		return primaryRegistry;
	}

	public static void setPrimaryRegistry(Registry primaryRegistry) {
		RegistryManager.primaryRegistry = primaryRegistry;
	}
}
