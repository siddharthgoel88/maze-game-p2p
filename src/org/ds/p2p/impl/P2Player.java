package org.ds.p2p.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.UUID;

import org.ds.p2p.Bootstrapper;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.Nominator;
import org.ds.p2p.PeerProperties;

public class P2Player {
	PeerProperties peerProp = new PeerProperties();
	GameState state;
	
	public static void main(String[] args) {
		P2Player player = new P2Player();
		player.init(args);
		while(true){
			
		}
	}
	
	public void init(String[] args){
		if(args.length > 0){
			state = GameStateFactory.getGameState();
			if(!initRegistry(args[0])){
				System.err.println("Game could not be initialized! Please try again later!");
			}
		}
	}

	private boolean initRegistry(String primaryIP) {
		Enumeration<NetworkInterface> netInterfaces = null;
		boolean isPrimary = false;
		String playerUUID = UUID.randomUUID().toString();
		
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
	        while (netInterfaces.hasMoreElements()) {
	            NetworkInterface iface = netInterfaces.nextElement();    
	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                
	                if(primaryIP.equals(addr.getHostAddress())){
	                	Registry registry = null;
	                	try {
	                		registry = LocateRegistry.createRegistry(1099);
	                		BootstrapperImpl bootstrapper = new BootstrapperImpl();
	                		registry.bind("bootstrapper", (Bootstrapper)UnicastRemoteObject.exportObject( bootstrapper , 0));
	                		state.setNumPlayers(1);
	                		System.out.println("Primary is ready !");
	                		isPrimary = true;
	                	}catch(Exception createException){
	                		System.out.println("Primary server already exists on this machine.");
	                	}
	                }
	            }
	        }
	      } catch (Exception exception) {
	    	    exception.printStackTrace();
				return false;
	      }
		
		if(!isPrimary){
			NominatorImpl nominator = new NominatorImpl();
			try {
				Registry registry = LocateRegistry.getRegistry(primaryIP, 1099);
				registry.bind(playerUUID ,(Nominator) UnicastRemoteObject.exportObject( nominator, 0));
				Bootstrapper bootstrap = (Bootstrapper) registry.lookup("bootstrapper");
				bootstrap.bootstrap(playerUUID);
				
			} catch (Exception cannotContact) {
				cannotContact.printStackTrace();
				return false;
			}
			
		}
	  return true;
	}
}