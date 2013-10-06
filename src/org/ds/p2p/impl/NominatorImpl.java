package org.ds.p2p.impl;

import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import org.ds.p2p.BackupUpdates;
import org.ds.p2p.PeerProperties;

public class NominatorImpl  {	
	public void nominate(Map<String,Object> gameProps) {
		PeerProperties peerPros = P2Player.getPeerProp();
		
		if((Boolean)gameProps.get("isNominated")){
			System.out.println("I have been nominated as backup! Yahoo");
			peerPros.getSecondaryPeerIp().put("ip", peerPros.getMyIP());
			peerPros.getSecondaryPeerIp().put("port", peerPros.getMyRMIport());
			peerPros.setBackup(true);
		}else{
			System.out.println("I have not been nominated as backup! Yahoo");
			peerPros.setBackup(false);
		}
		
		Registry registry = RegistryManager.getRegistry();
		BackupUpdatesImpl updateMoves = new BackupUpdatesImpl();
		
		try {
			registry.bind("updateBackup", (BackupUpdates) UnicastRemoteObject.exportObject( updateMoves , 0));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}	
}
