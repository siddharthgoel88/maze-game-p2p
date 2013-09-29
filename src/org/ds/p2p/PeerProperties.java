package org.ds.p2p;

import java.util.HashMap;


public class PeerProperties {
	
	boolean isPrimary;
	boolean isBackup;
	HashMap<String,Object> primaryProperties = new HashMap<String, Object>();
	HashMap<String,Object> secondaryProperties = new HashMap<String, Object>();
	
	public boolean isPrimary() {
		return isPrimary;
	}
	
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	
	public boolean isBackup() {
		return isBackup;
	}
	
	public void setBackup(boolean isBackup) {
		this.isBackup = isBackup;
	}
	public HashMap<String, Object> getPrimaryProperties() {
		return primaryProperties;
	}
	public void setPrimaryProperties(HashMap<String, Object> primaryProperties) {
		this.primaryProperties = primaryProperties;
	}
	public HashMap<String, Object> getSecondaryPeerIp() {
		return secondaryProperties;
	}
	public void setSecondaryPeerIp(HashMap<String, Object> secondaryProperties) {
		this.secondaryProperties = secondaryProperties;
	}
}
