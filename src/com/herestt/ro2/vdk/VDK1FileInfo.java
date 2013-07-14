package com.herestt.ro2.vdk;

import java.util.Map;


public class VDK1FileInfo extends VDKInnerDirectory{

	private String version;							// VDK file's version.
	private long unknown;							// Unknown.
	private long fileCount;							// Amount of file in the VDK file.
	private long folderCount;						// Amount of folder in the VDK file.
	private long size;								// VDK file's size.	
	private long fileListPartLength;				// Trailing file list's size.
	private Map<Long, String> filePathMap;			// Map holding the trailing file list. <File_Offset, File_Path>.
	private String sourcePath;						// Targeted VDK File's(unpackage)/Directory's(package) source path.
	
	public VDK1FileInfo(String version, long unknown, long fileCount, long folderCount, long size, 
			long fileListPartSize) {
		
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

	public long getUnknown() {
		return unknown;
	}

	public void setUnknown(long unknown) {
		this.unknown = unknown;
	}

	public long getFileCount() {
		return fileCount;
	}

	public void setFileCount(long fileCount) {
		this.fileCount = fileCount;
	}

	public long getFolderCount() {
		return folderCount;
	}

	public void setFolderCount(long folderCount) {
		this.folderCount = folderCount;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getFileListPartLength() {
		return fileListPartLength;
	}

	public void setFileListPartLength(long fileListPartLength) {
		this.fileListPartLength = fileListPartLength;
	}

	public Map<Long, String> getFilePathMap() {
		return filePathMap;
	}

	public void setFilePathMap(Map<Long, String> filePathMap) {
		this.filePathMap = filePathMap;
	}
	
	// Methods.
	
	public String getSourcePath() {
		return sourcePath;
	}


	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void unpack(String destination) {		
		unpack(getSourcePath(), destination);
	}
}
