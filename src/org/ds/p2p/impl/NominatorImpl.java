package org.ds.p2p.impl;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import org.ds.p2p.Bootstrapper;
import org.ds.p2p.PeerProperties;
import org.ds.p2p.BackupUpdates;

public class NominatorImpl  {
	// TODO : Refactor whole class 
	
	public void nominate(Map<String,Object> gameProps) {
		
		PeerProperties peerPros = P2Player.getPeerProp();
		if((Boolean) gameProps.get("isNominated")){
			System.out.println("I have been nominated as backup! Yahoo");
			peerPros.setBackup(true);
			peerPros.getSecondaryPeerIp().put("ip", peerPros.getMyIP());
			if((Boolean) gameProps.get("isSameMachine")){
				Registry registry = RegistryManager.getRegistry();
				BackupUpdatesImpl updateMoves = new BackupUpdatesImpl();
				try {
					registry.bind("updateBackup", (BackupUpdates) UnicastRemoteObject.exportObject( updateMoves , 0));
				} catch (Exception e) {
					e.printStackTrace();
				} 
				peerPros.getSecondaryPeerIp().put("port", peerPros.getMyRMIport());
			}else{
				System.out.println("Starting RMI on port 1099.");
				RegistryManager.initRegistry(1099);
				peerPros.getSecondaryPeerIp().put("port", 1099);
			}
		}else{
			System.out.println("I have been not been nominated as backup! Yahoo");
		}
	}	
}
