package com.herestt.ro2.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.herestt.ro2.io.VDKRandomAccessFile;
import com.herestt.ro2.vdk.VDK1FileInfo;
import com.herestt.ro2.vdk.VDK1FilePattern;
import com.herestt.ro2.vdk.VDKInnerDirectory;
import com.herestt.ro2.vdk.VDKInnerFile;

public class VDK1FileParser extends AbstractVDKFileParser {

	private final String vdkFilePath;
	
	public VDK1FileParser(String vdkFilePath) {
		
		this.vdkFilePath = vdkFilePath;
	}

	public String getVdkFilePath() {
		return vdkFilePath;
	}
	
	@Override
	public VDK1FileInfo start() {

		VDK1FileInfo vdkFileInfo = null;
				
		vdkFileInfo = readHeader();
		vdkFileInfo.setFilePathMap(retrieveFilePathMap(vdkFileInfo));			
		parse(vdkFileInfo);
				
		return vdkFileInfo;
	}

	private VDK1FileInfo readHeader() {
	
		VDK1FileInfo vdkFileInfo = null;				
		VDKRandomAccessFile raf = null;
		long offset = 0;
			
		try {
				
			raf = new VDKRandomAccessFile(vdkFilePath, "r");
			
			vdkFileInfo = new VDK1FileInfo( 
											raf.readString(offset, VDK1FilePattern.VERSION), 
											raf.readUnsignedInt(offset, VDK1FilePattern.UNKNOWN),
											raf.readUnsignedInt(offset, VDK1FilePattern.FILE_COUNT), 
											raf.readUnsignedInt(offset, VDK1FilePattern.FOLDER_COUNT), 
											raf.readUnsignedInt(offset, VDK1FilePattern.SIZE), 
											raf.readUnsignedInt(offset, VDK1FilePattern.FILE_LIST_PART_SIZE)											
											);			
			vdkFileInfo.setName(vdkFilePath);
			vdkFileInfo.setNextAddrOffset(VDK1FilePattern.getHeaderLength());
			vdkFileInfo.setDotDirectory(readDirectoryHeader(VDK1FilePattern.getHeaderLength()));
			vdkFileInfo.setOffset(VDK1FilePattern.getHeaderLength());
			
			raf.close();
				
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return vdkFileInfo;
	}

	private Map<Long, String> retrieveFilePathMap(VDK1FileInfo vdkFileInfo) {
		
		Map<Long, String> filePathMap = new HashMap<Long, String>();
		long currentOffset = vdkFileInfo.getSize() + VDK1FilePattern.getRootLength() + VDK1FilePattern.getFileListHeaderLength();
		long endOfFileList = vdkFileInfo.getSize() + VDK1FilePattern.getRootLength() + vdkFileInfo.getFileListPartLength();	
		
		try {			
			
			VDKRandomAccessFile raf = new VDKRandomAccessFile(vdkFilePath, "r");
			
			while(currentOffset < endOfFileList) {
				
				//System.out.println(raf.readUnsignedInt(currentOffset, VDK1FilePattern.FILE_OFFSET) + ": " + raf.readString(currentOffset, VDK1FilePattern.FILE_PATH));
				
				filePathMap.put(								 
								raf.readUnsignedInt(currentOffset, VDK1FilePattern.FILE_OFFSET),
								raf.readString(currentOffset, VDK1FilePattern.FILE_PATH)
								);
				currentOffset += VDK1FilePattern.getPathNameBlockLength();
			}
			
			raf.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return filePathMap;
	}	
	
	
	private VDKInnerDirectory readInnerSequence(long nextOffset) {
		
		VDKInnerDirectory vdkInnerDirectory = null;
		VDKRandomAccessFile raf = null;
		boolean isDirectory;
		
		try {
					
			raf = new VDKRandomAccessFile(vdkFilePath, "r");
			
			isDirectory = raf.readBoolean(nextOffset, VDK1FilePattern.IS_DIRECTORY);						
			vdkInnerDirectory = readDirectoryHeader(nextOffset);
			
			if(isDirectory) {

				if((vdkInnerDirectory.getName() != VDK1FilePattern.getDotDirectoryToken()) || (vdkInnerDirectory.getName() != VDK1FilePattern.getParentDirectoryToken())) {
														
					long dotDirectoryOffset = vdkInnerDirectory.getParentDirOffset();
					long parentDirectoryAccessorOffset = raf.readUnsignedInt(dotDirectoryOffset, VDK1FilePattern.NEXT_ADDR_OFFSET);
					
					vdkInnerDirectory.setDotDirectory(readDirectoryHeader(dotDirectoryOffset));
					vdkInnerDirectory.setParentAccessorDirectory(readDirectoryHeader(parentDirectoryAccessorOffset));
					
				} 
				//TODO - Herestt: throw an exception else.
				
				raf.close();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vdkInnerDirectory;
	}
	
	private VDKInnerDirectory readDirectoryHeader(long offset) {
		
		VDKInnerDirectory vdkInnerDirectory = null;
		VDKRandomAccessFile raf = null;
		boolean isDirectory; 
		
		try {
			raf = new VDKRandomAccessFile(vdkFilePath, "r");
			
			isDirectory = raf.readBoolean(offset, VDK1FilePattern.IS_DIRECTORY);
			
			if(isDirectory)
				vdkInnerDirectory = new VDKInnerDirectory();
			else vdkInnerDirectory = new VDKInnerFile();
			
			vdkInnerDirectory.setName(raf.readString(offset, VDK1FilePattern.NAME));
			vdkInnerDirectory.setRawSize(raf.readUnsignedInt(offset, VDK1FilePattern.RAW_SIZE));
			vdkInnerDirectory.setPackedSize(raf.readUnsignedInt(offset, VDK1FilePattern.PACKED_SIZE));
			vdkInnerDirectory.setOffset(offset);
			vdkInnerDirectory.setParentDirOffset(raf.readUnsignedInt(offset, VDK1FilePattern.PARENT_DIRECTORY));
			vdkInnerDirectory.setNextAddrOffset(raf.readUnsignedInt(offset, VDK1FilePattern.NEXT_ADDR_OFFSET));
			
			raf.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vdkInnerDirectory;
	}

	private void parse(VDKInnerDirectory parentDir) {
		
		VDKInnerDirectory dir;
		long nextOffset; 
		
		if(parentDir instanceof VDK1FileInfo)
			nextOffset = parentDir.getDotDirectory().getNextAddrOffset();
		// IF the parent directory is a directory instance.
		else nextOffset = parentDir.getParentAccessorDirectory().getNextAddrOffset();
		
		while(nextOffset != VDK1FilePattern.getFinalDirectoryToken()) {
			
			dir = readInnerSequence(nextOffset);
			
			if(dir instanceof VDKInnerFile) {
				
				dir.setChildren(null);				
				parentDir.addChild(dir);								
				nextOffset = dir.getNextAddrOffset();
			}
			// IF "dir" is a directory instance.
			else {
				
				parse(dir);
				parentDir.addChild(dir);
				nextOffset = dir.getNextAddrOffset();
			}
				
		}
	}
}
