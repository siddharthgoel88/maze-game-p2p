package org.ds.p2p;

import java.io.Serializable;

public class Player implements Serializable{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = -2397673921295129209L;
	String name;
	String id;
	int numTreasures;
	int currentRow;
	int currentCol;
	char playerDispId;
	Long lastActiveTime = 0L;
	
	public Player(String name) {
		this.setName(name);
	}

	public Player(String name , String id){
		this.setId(id);
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumTreasures() {
		return numTreasures;
	}

	public void setNumTreasures(int numTreasures) {
		this.numTreasures = numTreasures;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}

	public int getCurrentCol() {
		return currentCol;
	}

	public void setCurrentCol(int currentCol) {
		this.currentCol = currentCol;
	}

	public char getPlayerDispId() {
		return playerDispId;
	}

	public void setPlayerDispId(char playerDispId) {
		this.playerDispId = playerDispId;
	}

	public Long getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(Long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

}	
