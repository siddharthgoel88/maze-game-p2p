package org.ds.p2p;

import java.io.Serializable;

public class Square implements Serializable{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = -4158912313912328787L;
	int numTreasures = 0;
	boolean isFree = true;
	String userId = null;

	public int getNumTreasures() {
		return numTreasures;
	}
	public void setNumTreasures(int numTreasures) {
		this.numTreasures = numTreasures;
	}
	public boolean isFree() {
		return isFree;
	}
	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
