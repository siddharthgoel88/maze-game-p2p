package org.ds.p2p.impl;

import java.util.Map;

import org.ds.p2p.Nominator;
import org.ds.p2p.PeerProperties;

public class NominatorImpl implements Nominator {

	@Override
	public void nominate(Map<String,Object> gameProps) {
		
		PeerProperties peerPros = P2Player.getPeerProp();
		if((Boolean) gameProps.get("isNominated")){
			System.out.println("I have been nominated as backup! Yahoo");
			peerPros.setBackup(true);
			peerPros.getSecondaryPeerIp().put("ip", peerPros.getMyIP());
			if((Boolean) gameProps.get("isSameMachine")){
				System.out.println("Starting RMI on port 1100.");
				RegistryManager.initRegistry(1100);
				peerPros.getSecondaryPeerIp().put("port", 1100);
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
