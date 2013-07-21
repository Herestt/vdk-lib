package com.herestt.ro2.vdk;

public enum VDK1FilePattern implements VDKFilePattern {

	
	// <SEQUENCE_NAME> (<VARIABLE_NAME>, <OFFSET>, <LENGTH>, IS_LITTLE_ENDIAN).
		// Header.
	VERSION ("version", 0x00, 0x08, false),
	UNKNOWN ("unknown", 0x08, 0x04, true),
	FILE_COUNT ("fileCount", 0x0C, 0x04, true),
	FOLDER_COUNT ("folderCount", 0x10, 0x04, true),
	SIZE ("size", 0x14, 0x04, true),
	FILE_LIST_PART_SIZE ("fileListPartSize", 0x18, 0x04, true),
	
		// Data.
	IS_DIRECTORY ("isDir", 0x00, 0x01, false),
	NAME ("name", 0x01, 0x80, false),
	RAW_SIZE ("rawSize", 0x81, 0x04, true),
	PACKED_SIZE("packedSize", 0x85, 0x04, true),
	PARENT_DIRECTORY ("parentDirOffset", 0x89, 0x04, true),
	NEXT_ADDR_OFFSET("nextAddrOffset", 0x8D, 0x04, true),
	FILE_CONTENT("fileContent", 0x91, 0xFFFF, true),
	
		// File Root.
	FILE_ROOT("fileRoot", 0x00, 0x91, true),
	
		// File List.
	FILE_LIST_HEADER("fileListHeader", 0x00, 0x04, true),
	FILE_PATH ("filePath", 0x04, 0x108, false),
	FILE_OFFSET ("fileOffset", 0x108, 0x04, true);	
	
	// File's structure variables.
	private static final int headerLength = 0x1C;				// Hexadecimal length.
	private static final int directoryHeaderLength = 0x91;		//
	private static final int rootLength = 0x91;					// File's root size.
	private static final int fileListHeaderLength = 0x04;		//
	private static final int pathNameBlockLength = 0x108;		//Size of each file's path name.

	private static final String dotDirectoryToken = ".";
	private static final String parentDirectoryToken = "..";
	private static final int finalDirectoryToken = 0;
	
	// 	File's sequences variables.
	private final String key;
	private final int offset;
	private final int length;
	private final boolean isLittleEndian;

	private VDK1FilePattern(String key, int offset, int length, boolean isBigEndian){
		this.key = key;
		this.offset = offset;
		this.length = length;
		this.isLittleEndian = isBigEndian;
	}

	public static int getHeaderLength() {
		return headerLength;
	}

	public static int getDirectoryHeaderLength() {
		return directoryHeaderLength;
	}
	
	public static int getRootLength() {
		return rootLength;
	}
	
	public static int getFileListHeaderLength() {
		return fileListHeaderLength;
	}

	public static int getPathNameBlockLength() {
		return pathNameBlockLength;
	}	
	
	public static String getDotDirectoryToken() {
		return dotDirectoryToken;
	}

	public static String getParentDirectoryToken() {
		return parentDirectoryToken;
	}

	public static int getFinalDirectoryToken() {
		return finalDirectoryToken;
	}

	public String getKey() {
		return key;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public boolean isLittleEndian() {
		return isLittleEndian;
	}
}
