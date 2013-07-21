package com.herestt.ro2.vdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.herestt.ro2.io.VDKRandomAccessFile;


public class VDK1FileInfo extends AbstractVDKFileInfo {

	private String version;							// VDK file's version.
	private long unknown;							// Unknown.
	private long fileCount;							// Amount of file in the VDK file.
	private long folderCount;						// Amount of folder in the VDK file.
	private long size;								// VDK file's size.	
	private long fileListPartLength;				// Trailing file list's size.
	private Map<String, Long> filePathMap;			// Map holding the trailing file list. <File_Path, File_Offset>.
	private String sourcePath;						// Targeted VDK File's(unpackage)/Directory's(package) source path.
	
	public VDK1FileInfo() {
		super();
	}

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

	public Map<String, Long> getFilePathMap() {
		return filePathMap;
	}

	public void setFilePathMap(Map<String, Long> filePathMap) {
		this.filePathMap = filePathMap;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	// Methods.
	
	@Override
	public void pack(String destination) {
		
		File f = new File(destination + File.separator + getName() + ".VDK");
		
		try {
			
			f.createNewFile();
			destination = f.getAbsolutePath();
			writeDirectory(f.getAbsolutePath(), this.getDotDirectory(), VDK1FilePattern.getHeaderLength());
			size = packChildren(destination, this.getDotDirectory().getOffset(), this.getDotDirectory().getNextAddrOffset());					
			writeHeader(destination, 0);
			writeRoot(destination, size);
			writeFileListerHeader(destination, (size + VDK1FilePattern.getRootLength()));
			writeFileList(destination, (size + VDK1FilePattern.getRootLength()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void writeHeader(String destination, long offset) {
		
		VDKRandomAccessFile raf = null;
		
		try {
			
			raf = new VDKRandomAccessFile(destination, "rw");
			
			raf.writeString(version, offset, VDK1FilePattern.VERSION);
			raf.writeUnsignedInt(unknown, offset, VDK1FilePattern.UNKNOWN);
			raf.writeUnsignedInt(fileCount, offset, VDK1FilePattern.FILE_COUNT);
			raf.writeUnsignedInt(folderCount, offset, VDK1FilePattern.FOLDER_COUNT);
			raf.writeUnsignedInt(size, offset, VDK1FilePattern.SIZE);
			raf.writeUnsignedInt(fileListPartLength, offset, VDK1FilePattern.FILE_LIST_PART_SIZE);
			
			raf.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeRoot(String destination, long offset) {
		// TODO - Herestt: write file's root.
		
	}	
	
	private void writeFileListerHeader(String destination, long offset) {
		
		VDKRandomAccessFile raf = null;
		
		try {
			
			raf = new VDKRandomAccessFile(destination, "rw");			
			raf.writeUnsignedInt(fileListPartLength, offset, VDK1FilePattern.FILE_LIST_HEADER);			
			raf.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void unpack(String destination) {		
		unpack(sourcePath, destination);
	}
}
