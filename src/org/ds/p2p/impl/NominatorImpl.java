package org.ds.p2p.impl;

import java.rmi.RemoteException;
import java.util.Map;

import org.ds.p2p.Nominator;

public class NominatorImpl implements Nominator {

	@Override
	public void nominate(Map<String,Object> gameProps) throws RemoteException{
		if((Boolean)gameProps.get("isNominated"))
			System.out.println("I have been nominated as backup! Yahoo");
		else
			System.out.println("I have been not been nominated as backup! Yahoo");
	}
	
}
