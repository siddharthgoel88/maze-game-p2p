package org.ds.p2p.impl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Set;

import org.ds.p2p.ClientHeartBeat;
import org.ds.p2p.FailureUpdate;
import org.ds.p2p.GameEndCheck;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.MovePlayers;
import org.ds.p2p.Player;
import org.ds.p2p.PrimaryStatus;

public class PrimaryStatusChecker implements Runnable{

	PrimaryStatus primaryStatus = null;
	static int noOfTries = 1;
	static int flag = 1;
	private String oldPrimaryUUID = null;
	
	@Override
	public void run() {
		while(true && (flag==1))
		{
			try {
				while(primaryStatus.isPrimaryAlive())
				{
					noOfTries = 1;
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				if(noOfTries < 3)
				{
					System.out.println("Cannot Reach server. Try No. " + noOfTries);
					try{
						Thread.sleep(500);
					}catch(Exception e1){
						
					}
					noOfTries++;
				}
				else
				{
					System.out.println("Primary cannot be reached");
					System.out.println("So backup becoming primary");
					becomePrimary();
					updateClients();
					MovePlayersImpl.nominateAltBackup();
					P2Player.initHeartBeat();
					flag = 0;
				}
			}
		}
	}

	private void updateClients() {
		HashMap<String, String> others = P2Player.getPeerProp().getOtherPlayerProps();
		Set<String> nextbkp =  others.keySet();
		FailureUpdate fu = null;
		Registry fuReg = null;
		String ip = null, port = null;
		HashMap<String, String> newPrimary = new HashMap<String, String>();
		
		newPrimary.put("ip", P2Player.getPeerProp().getMyIP());
		newPrimary.put("port",String.valueOf(P2Player.getPeerProp().getMyRMIport()));
		
		for(String otherPlayerHosts : nextbkp){
			ip = others.get(otherPlayerHosts).split(":")[0];
			port = others.get(otherPlayerHosts).split(":")[1];
			
			try
			{
				fuReg = LocateRegistry.getRegistry(ip, Integer.parseInt(port) );
				fu = (FailureUpdate) fuReg.lookup("failureUpdate");
				fu.updatePrimary(newPrimary);
			}
			catch(Exception e)
			{
				System.err.println("Could not connect to client" + others.get("uuid"));
			}			
		}
		
	}

	private void becomePrimary() { 
		System.out.println("Backup Game state. No of treasure:"+ BackupUpdatesImpl.backUpGameState.getTotalNumTreasures());
		GameStateFactory.setState(BackupUpdatesImpl.backUpGameState);
		P2Player.setPeerProp(BackupUpdatesImpl.backUpPeerProps);
		oldPrimaryUUID = (String) P2Player.getPeerProp().getPrimaryProperties().get("uuid");
		P2Player.getPeerProp().setPrimary(true);
		P2Player.getPeerProp().setBackup(false);
		P2Player.getPeerProp().getPrimaryProperties().put("uuid", P2Player.gamePlayer.getId());
		P2Player.getPeerProp().getPrimaryProperties().put("ip", P2Player.getPeerProp().getMyIP());
		P2Player.getPeerProp().getPrimaryProperties().put("port", String.valueOf(P2Player.getPeerProp().getMyRMIport()));
		initiateRegistries();
	}

	private void initiateRegistries() {
		Registry registry = RegistryManager.getRegistry();
		RegistryManager.setPrimaryRegistry(registry);
		
		ClientHeartBeatImpl heartBeatImpl = new ClientHeartBeatImpl();
		GameEndCheckImpl gameEndCheck = new GameEndCheckImpl();
		PrimaryStatusImpl primaryStatus = new PrimaryStatusImpl();
		MovePlayersImpl movePlayers = new MovePlayersImpl();
		GameState state = GameStateFactory.getGameState();
		
		for( Player p : state.getPlayers().values() ){
			if(!p.getId().equals(oldPrimaryUUID))
				p.setLastActiveTime(0L);
		}
		
		try
		{
			registry.bind("move", (MovePlayers) UnicastRemoteObject.exportObject( movePlayers , 0));
			registry.bind("heartBeat", (ClientHeartBeat) UnicastRemoteObject.exportObject( heartBeatImpl , 0));
			registry.bind("gameEnd", (GameEndCheck) UnicastRemoteObject.exportObject( gameEndCheck , 0));
			registry.bind("primaryStatus", (PrimaryStatus) UnicastRemoteObject.exportObject( primaryStatus , 0));
		}
		catch (Exception e)
		{
			System.err.println("Registry binding in new primary is some problem");
		}		
	}

	public PrimaryStatus getPrimaryStatus() {
		return primaryStatus;
	}

	public void setPrimaryStatus(PrimaryStatus primaryStatus) {
		this.primaryStatus = primaryStatus;
	}

}
