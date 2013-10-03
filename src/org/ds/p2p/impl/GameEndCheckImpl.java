package org.ds.p2p.impl;


import java.rmi.RemoteException;

import org.ds.p2p.GameEndCheck;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;

public class GameEndCheckImpl implements GameEndCheck{

	GameState state = GameStateFactory.getGameState();
	
	@Override
	public GameState checker() throws RemoteException{
		if(state.getTotalNumTreasures() == 0){
			return state;
		}
		return null;
	}
}
