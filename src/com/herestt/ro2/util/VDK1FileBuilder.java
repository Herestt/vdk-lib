package com.herestt.ro2.util;

import java.io.File;
import java.util.Map;

import com.herestt.ro2.vdk.AbstractVDKFileInfo;
import com.herestt.ro2.vdk.VDK1FileInfo;
import com.herestt.ro2.vdk.VDK1FilePattern;
import com.herestt.ro2.vdk.VDKFileVersion;
import com.herestt.ro2.vdk.VDKInnerDirectory;
import com.herestt.ro2.vdk.VDKInnerFile;

public class VDK1FileBuilder extends AbstractVDKFileBuilder {

	private final String dirPath;

	public VDK1FileBuilder(String dirPath) {
		
		this.dirPath = dirPath;
	}

	// Getters and Setters.
	
	public String getDirPath() {
		return dirPath;
	}

	// Methods.
	
	@Override
	public AbstractVDKFileInfo start() {
		
		VDK1FileInfo vdkFileInfo;
		
		vdkFileInfo = getAndFillVDK1FileInfo();
		build(vdkFileInfo);

		return vdkFileInfo;
	}	

	private VDK1FileInfo getAndFillVDK1FileInfo() {

		VDK1FileInfo vdkFileInfo = new VDK1FileInfo();
		File dir = new File(dirPath);
		
		vdkFileInfo.setName(dir.getName());
		vdkFileInfo.setVersion(VDKFileVersion.VDK1.getSTRFileVersion());
		vdkFileInfo.setFileCount(determineFileCount(dirPath));
		vdkFileInfo.setFolderCount(determineDirectoryCount(dirPath));
		vdkFileInfo.setFileListPartLength(determineFileListPartLength(vdkFileInfo.getFolderCount(), vdkFileInfo.getFileCount()));
		vdkFileInfo.setSourcePath(dirPath);
		vdkFileInfo.setRawSize(dir.getTotalSpace());
		vdkFileInfo.setDotDirectory(
									new VDKInnerDirectory(
															VDK1FilePattern.getDotDirectoryToken(),
															0,
															0,
															VDK1FilePattern.getHeaderLength(),
															VDK1FilePattern.getHeaderLength(),
															VDK1FilePattern.getHeaderLength() + VDK1FilePattern.getDirectoryHeaderLength()
									));
		
		return vdkFileInfo;
	}
	
	private long determineFileCount(String path) {
		
		long fileCount = 0;
		
		for(File f : new File(path).listFiles()) {
			
			if(f.isFile())
				fileCount++;
			if(f.isDirectory())
				fileCount += determineFileCount(f.getAbsolutePath());
		}
		
		return fileCount;
	}

	private long determineDirectoryCount(String path) {

		long directoryCount = 0;
		
		for(File f : new File(path).listFiles()) {
			
			if(f.isDirectory()) {
				
				directoryCount++;
				directoryCount += determineDirectoryCount(f.getAbsolutePath());
			}
		}

		return directoryCount;
	}

	private long determineFileListPartLength(long directoryCount, long fileCount) {		
		return ((fileCount * VDK1FilePattern.getPathNameBlockLength()) + directoryCount);
	}
	
	private void build(VDKInnerDirectory parentDir) {
		
		VDKInnerDirectory currentDir = null;		
		File dir = new File(parentDir.getSourcePath());
		
		for(File f : dir.listFiles()) {
			
			currentDir = toVDKInnerDirectory(f);
			currentDir.setSourcePath(f.getAbsolutePath());
			parentDir.addChild(currentDir);
			
			if(f.isDirectory())
				build(currentDir);
		}
		
	}

	private VDKInnerDirectory toVDKInnerDirectory(File f) {
		
		VDKInnerDirectory vdkInnerDirectory = null;
		VDKInnerDirectory vdkDotDirectory = null;
		VDKInnerDirectory vdkParInnerDirectory = null;
		
		if(f.isDirectory()) {
			
			vdkInnerDirectory = new VDKInnerDirectory(
														f.getName(), 
														0, 
														0,
														0,		// Is determinated during the packing phase. 
														0,		// Is determinated during the packing phase.
														0		// Is determinated during the packing phase.
														);
			
			
			
			vdkDotDirectory = new VDKInnerDirectory(
														VDK1FilePattern.getDotDirectoryToken(), 
														0, 
														0,
														0,		// Is determinated during the packing phase.
														0,		// Is determinated during the packing phase.
														0		// Is determinated during the packing phase.
														);
			
			vdkParInnerDirectory = new VDKInnerDirectory(
															VDK1FilePattern.getParentDirectoryToken(), 
															0, 
															0,
															0,		// Is determinated during the packing phase.
															0,		// Is determinated during the packing phase.
															0		// Is determinated during the packing phase.
															);
			
			vdkInnerDirectory.setDotDirectory(vdkDotDirectory);			
			vdkInnerDirectory.setParentAccessorDirectory(vdkParInnerDirectory);
		}
		
		if(f.isFile()) {
			
			vdkInnerDirectory = new VDKInnerFile(				
													f.getName(), 
													f.length(), 
													0,		// Is determinated during the packing phase.
													0,		// Is determinated during the packing phase.
													0,		// Is determinated during the packing phase.
													0		// Is determinated during the packing phase.
													);
		}
		
		return vdkInnerDirectory;
	}
}
