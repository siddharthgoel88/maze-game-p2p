package org.ds.p2p.impl;

import java.rmi.RemoteException;

import org.ds.p2p.PrimaryStatus;

public class PrimaryStatusImpl implements PrimaryStatus {

	@Override
	public boolean isPrimaryAlive() throws RemoteException {
		return true;
	}

}
