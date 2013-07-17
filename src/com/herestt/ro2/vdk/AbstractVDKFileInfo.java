package com.herestt.ro2.vdk;


public abstract class AbstractVDKFileInfo extends VDKInnerDirectory {
	 
	// Getters.
	public abstract String getVersion();
	public abstract long getUnknown();	 
	public abstract long getFileCount();	 
	public abstract long getFolderCount();	 
	public abstract long getSize();
	
	// Abstract Methods.
	public abstract void pack(String destination);
	public abstract void unpack(String destination);	 
}
