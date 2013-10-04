package org.ds.p2p.impl;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.ds.p2p.Bootstrapper;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.PeerProperties;
import org.ds.p2p.Player;

public class BootstrapperImpl implements Bootstrapper{
	GameState state;
	char playerDispId = 'B';
	
	@Override
	public synchronized Map<String, Object> bootstrap(Map<String,String> playerProperties) throws RemoteException{
		Map<String,Object> gameProps = new HashMap<String, Object>();
		state = GameStateFactory.getGameState();
		PeerProperties peerProp = P2Player.getPeerProp();
		String name = playerProperties.get("name");
		String uuid = playerProperties.get("uuid");
		String port = playerProperties.get("port");
		String ip = playerProperties.get("machineIP");
		String primaryIP = (String) peerProp.getPrimaryProperties().get("ip");
		
		if(state.getNumPlayers() == 1){
			System.out.println("Backup server has arrived");
			gameProps.put("isNominated", true);
			gameProps.put("isSameMachine", primaryIP.equals(playerProperties.get("machineIP"))?true:false);
			state.setNumPlayers(state.getNumPlayers()+1);
			peerProp.getSecondaryPeerIp().put("uuid", uuid);
			peerProp.getSecondaryPeerIp().put("ip", ip);
			peerProp.getSecondaryPeerIp().put("port", port); 
		}else{
			gameProps.put( "isNominated", false );
			gameProps.put("isSameMachine", primaryIP.equals(playerProperties.get("machineIP"))?true:false);
			peerProp.getOtherPlayerProps().put(uuid, ip + ":" + port);
			state.setNumPlayers( state.getNumPlayers() + 1 );
		}
		gameProps.put("waitTime", peerProp.getInitTime() - System.currentTimeMillis());
		Player player = new Player( name , uuid);
		player.setPlayerDispId( playerDispId );
		gameProps.put("playerDispId", playerDispId );
		playerDispId++;
		state.getPlayers().put( uuid, player );
		while(!state.initializePlayer(uuid));
		return gameProps;
	}
}
