package com.herestt.ro2.vdk;

import java.util.Map;


public class VDK1FileInfo extends VDKInnerDirectory{

	private String version;							// VDK file's version.
	private int unknown;							// Unknown.
	private int fileCount;							// Amount of file in the VDK file.
	private int folderCount;						// Amount of folder in the VDK file.
	private int size;								// VDK file's size.	
	private int fileListPartLength;					// Trailing file list's size.
	private Map<Integer, String> filePathMap;		// Map holding the trailing file list. <File_Offset, File_Path>.
	
	public VDK1FileInfo(String version, int unknown, int fileCount,	int folderCount, int size, 
			int fileListPartSize) {
		
		super();
		this.version = version;
		this.unknown = unknown;
		this.fileCount = fileCount;
		this.folderCount = folderCount;
		this.size = size;
		this.fileListPartLength = fileListPartSize;				
	}

	
	// Getters and Setters.	

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public int getFolderCount() {
		return folderCount;
	}

	public void setFolderCount(int folderCount) {
		this.folderCount = folderCount;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getFileListPartLength() {
		return fileListPartLength;
	}

	public void setFileListPartLength(int fileListPartLength) {
		this.fileListPartLength = fileListPartLength;
	}

	public Map<Integer, String> getFilePathMap() {
		return filePathMap;
	}

	public void setFilePathMap(Map<Integer, String> filePathMap) {
		this.filePathMap = filePathMap;
	}
	
	// Methods.

}
