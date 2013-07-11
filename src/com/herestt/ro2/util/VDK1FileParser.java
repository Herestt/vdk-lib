package com.herestt.ro2.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import com.herestt.ro2.vdk.VDK1FileInfo;
import com.herestt.ro2.vdk.VDK1FilePattern;
import com.herestt.ro2.vdk.VDKInnerDirectory;
import com.herestt.ro2.vdk.VDKInnerFile;
import com.sun.org.apache.bcel.internal.util.ByteSequence;

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
		
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		
		try {						
			
			for(VDK1FilePattern p : VDK1FilePattern.values()) {				
																										
				map.put(p.getKey(), read(0, p));
				
				if((p.getOffset() + p.getLength()) == VDK1FilePattern.getHeaderLength()) {													
					break;
				}					
			}
		}
		finally{
		
			try {				
				vdkFileInfo = new VDK1FileInfo( 
												new String(map.get("version"), "UTF-8").trim(), 
												new ByteSequence(map.get("unknown")).readInt(),
												new ByteSequence(map.get("fileCount")).readInt(), 
												new ByteSequence(map.get("folderCount")).readInt(), 
												new ByteSequence(map.get("size")).readInt(), 
												new ByteSequence(map.get("fileListPartSize")).readInt()											
												);				
				vdkFileInfo.setNextDirOffset(new ByteSequence(read(VDK1FilePattern.getHeaderLength(), VDK1FilePattern.NEXT_ADDR_OFFSET)).readInt());
				vdkFileInfo.setOffset(VDK1FilePattern.getHeaderLength());
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return vdkFileInfo;
	}

	private Map<Integer, String> retrieveFilePathMap(VDK1FileInfo vdkFileInfo) {
		
		Map<Integer, String> filePathMap = new HashMap<Integer, String>();
		int currentOffset = vdkFileInfo.getSize() + VDK1FilePattern.getRootLength() + VDK1FilePattern.getFileListHeaderLength();
		int endOfFileList = vdkFileInfo.getSize() + VDK1FilePattern.getRootLength() + vdkFileInfo.getFileListPartLength();	
		byte[] fileOffsetBuffer = null;
		byte[] filePathBuffer = null;
		
		while(currentOffset < endOfFileList) {

			try {
				
				filePathBuffer = read(currentOffset, VDK1FilePattern.FILE_PATH);
				fileOffsetBuffer = read(currentOffset, VDK1FilePattern.FILE_OFFSET);							
				currentOffset += VDK1FilePattern.getPathNameBlockLength();							

				filePathMap.put(
								new ByteSequence(fileOffsetBuffer).readInt(), 
								new String(filePathBuffer, "UTF-8").trim()
								);
				
				//System.out.println(new ByteSequence(fileOffsetBuffer).readInt() + ": " + new String(filePathBuffer, "UTF-8").trim());
				
			} catch (IOException e) {				
			
				e.printStackTrace();
			}					
		}
		
		return filePathMap;
	}	
	
	private void parse(VDKInnerDirectory parentDir) {
		
		VDKInnerDirectory dir;
		int nextOffset; 
		
		if(parentDir instanceof VDK1FileInfo)
			nextOffset = parentDir.getNextDirOffset();
		// IF the parent directory is a directory instance.
		else nextOffset = parentDir.getParentAccessorDirectory().getNextDirOffset();
		
		while(nextOffset != VDK1FilePattern.getFinalDirectoryToken()) {
			
			dir = readInnerSequence(nextOffset);
			
			if(dir instanceof VDKInnerFile) {
				
				parentDir.addChild(dir);
				nextOffset = dir.getNextDirOffset();
			}
			// IF "dir" is a directory instance.
			else {
				
				parse(dir);
				parentDir.addChild(dir);
				nextOffset = dir.getNextDirOffset();
			}
				
		}
	}
		
	private VDKInnerDirectory readInnerSequence(int currentOffset) {
		
		VDKInnerDirectory vdkInnerDirectory = null;	
		boolean isDirectory;
		
		try {
						
			isDirectory = new ByteSequence(read(currentOffset, VDK1FilePattern.IS_DIRECTORY)).readBoolean();						
			vdkInnerDirectory = readDirectoryHeader(currentOffset);
			
			if(isDirectory) {

				if((vdkInnerDirectory.getName() != VDK1FilePattern.getBeginningDirectoryToken()) || (vdkInnerDirectory.getName() != VDK1FilePattern.getParentDirectoryToken())) {
														
					int dotDirectoryOffset = vdkInnerDirectory.getParentDirOffset();
					int parentDirectoryAccessorOffset =	new ByteSequence(read(dotDirectoryOffset, VDK1FilePattern.NEXT_ADDR_OFFSET)).readInt();
					
					vdkInnerDirectory.setDotDirectory(readDirectoryHeader(dotDirectoryOffset));
					vdkInnerDirectory.setParentAccessorDirectory(readDirectoryHeader(parentDirectoryAccessorOffset));
					
				} 
				//TODO - Herestt: throw an exception else.

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vdkInnerDirectory;
	}
	
	private VDKInnerDirectory readDirectoryHeader(int offset) {
		
		VDKInnerDirectory vdkInnerDirectory = null;
		boolean isDirectory; 
		
		try {
			
			isDirectory = new ByteSequence(read(offset, VDK1FilePattern.IS_DIRECTORY)).readBoolean();
			
			if(isDirectory)
				vdkInnerDirectory = new VDKInnerDirectory();
			else vdkInnerDirectory = new VDKInnerFile();
			
			vdkInnerDirectory.setName(new String(read(offset, VDK1FilePattern.NAME), "UTF-8").trim());
			vdkInnerDirectory.setRawSize(new ByteSequence(read(offset, VDK1FilePattern.RAW_SIZE)).readInt());
			vdkInnerDirectory.setPackedSize(new ByteSequence(read(offset, VDK1FilePattern.PACKED_SIZE)).readInt());
			vdkInnerDirectory.setOffset(offset);
			vdkInnerDirectory.setParentDirOffset(new ByteSequence(read(offset, VDK1FilePattern.PARENT_DIRECTORY)).readInt());
			vdkInnerDirectory.setNextDirOffset(new ByteSequence(read(offset, VDK1FilePattern.NEXT_ADDR_OFFSET)).readInt());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return vdkInnerDirectory;
	}

	private byte[] read(int offset, VDK1FilePattern pattern) {
		
		byte[] buffer = new byte[pattern.getLength()];
		byte[] resultBuffer = new byte[pattern.getLength()];
		int bytesRead;
		int tmp;	
		
		RandomAccessFile raf = null;		
		
		try {					
			
			raf = new RandomAccessFile(new File(vdkFilePath), "r");
			
			raf.seek(offset + pattern.getOffset());
			bytesRead = raf.read(buffer);
			
			if (bytesRead != pattern.getLength()) {
				raf.close();
			    throw new IOException("Unexpected End of Stream");
			}

			if(pattern.isLittleEndian()){					
				tmp = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();					
				resultBuffer = ByteBuffer.allocate(pattern.getLength()).putInt(tmp).array();
			}					
			else ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN).get(resultBuffer);
			
			raf.close();
			
		} catch (IOException e) {
			
			try {
				raf.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return resultBuffer;
	}
}
