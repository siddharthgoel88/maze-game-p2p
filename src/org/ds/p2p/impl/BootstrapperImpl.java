package org.ds.p2p.impl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.ds.p2p.Bootstrapper;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.Nominator;

public class BootstrapperImpl implements Bootstrapper{
	GameState state;
	
	@Override
	public synchronized Map<String, Object> bootstrap(String uuid) throws RemoteException{
		Registry registry = LocateRegistry.getRegistry(1099);
		Map<String,Object> gameProps = new HashMap<String, Object>();
		state = GameStateFactory.getGameState();
		try {
			Nominator nominator = (Nominator) registry.lookup(uuid);
			if(state.getNumPlayers() == 1){
				System.out.println("Backup server has arrived");
				gameProps.put("isNominated", true);
				nominator.nominate(gameProps);
				state.setNumPlayers(state.getNumPlayers()+1);
			}else{
				gameProps.put("isNominated", false);
				nominator.nominate(gameProps);
			}
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return gameProps;
	}

}
