package org.ds.p2p.impl;

import java.rmi.RemoteException;

import org.ds.p2p.ClientHeartBeat;	
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;

public class ClientHeartBeatImpl implements ClientHeartBeat{
	
	GameState state = GameStateFactory.getGameState();
	
	public boolean updateHeartBeat(String id) throws RemoteException{
		synchronized (state.getPlayers().get(id)) {
			state.getPlayers().get(id).setLastActiveTime(System.currentTimeMillis());
		}
		return true;
	}
}
