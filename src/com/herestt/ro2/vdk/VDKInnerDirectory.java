package com.herestt.ro2.vdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.herestt.ro2.io.VDKRandomAccessFile;

public class VDKInnerDirectory {

	private String name;
	private long rawSize;
	private long packedSize;	
	private long offset;
	private long parentDirOffset;
	private long nextAddrOffset;
	private VDKInnerDirectory dotDirectory;
	private VDKInnerDirectory parentAccessorDirectory;
	private Set<VDKInnerDirectory> children;
	private String sourcePath;
	
	public VDKInnerDirectory() {
		
		children = new LinkedHashSet<VDKInnerDirectory>();
	};	
	
	public VDKInnerDirectory(String name, long rawSize, long packedSize,
			long offset, long parentDirOffset, long nextAddrOffset) {
		super();
		this.name = name;
		this.rawSize = rawSize;
		this.packedSize = packedSize;
		this.offset = offset;
		this.parentDirOffset = parentDirOffset;
		this.nextAddrOffset = nextAddrOffset;
		children = new LinkedHashSet<VDKInnerDirectory>();
	}
	
	// Getters and Setters.
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getRawSize() {
		return rawSize;
	}

	public void setRawSize(long rawSize) {
		this.rawSize = rawSize;
	}

	public long getPackedSize() {
		return packedSize;
	}

	public void setPackedSize(long packedSize) {
		this.packedSize = packedSize;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getParentDirOffset() {
		return parentDirOffset;
	}

	public void setParentDirOffset(long parentDirOffset) {
		this.parentDirOffset = parentDirOffset;
	}

	public long getNextAddrOffset() {
		return nextAddrOffset;
	}

	public void setNextAddrOffset(long nextAddrOffset) {
		this.nextAddrOffset = nextAddrOffset;
	}

	public VDKInnerDirectory getDotDirectory() {
		return dotDirectory;
	}

	public void setDotDirectory(VDKInnerDirectory dotDirectory) {
		this.dotDirectory = dotDirectory;
	}

	public VDKInnerDirectory getParentAccessorDirectory() {
		return parentAccessorDirectory;
	}

	public void setParentAccessorDirectory(VDKInnerDirectory parentAccessorDirectory) {
		this.parentAccessorDirectory = parentAccessorDirectory;
	}

	public Set<VDKInnerDirectory> getChildren() {
		return children;
	}

	public void setChildren(Set<VDKInnerDirectory> children) {
		this.children = children;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	// Methods

	public void addChild(VDKInnerDirectory component) {
		
		children.add(component);
	}

	public void removeChild(VDKInnerDirectory component) {
		
		children.remove(component);
	}
	
	public VDKInnerDirectory findChild(int offset) {
		
		for(VDKInnerDirectory d : children) {
			 
			if(!(d instanceof VDKInnerFile)) {
				
				if(d.getDotDirectory().getOffset() == offset)
					return d;
				return d.findChild(offset);
			}
		}
		
		//TODO - Herestt: Throw exception.
		return null;
	}
	
	protected long packChildren(String destination, long parentDirOffset, long nextAddrOffset) {
		
		long currentOffset = nextAddrOffset;
		Iterator<VDKInnerDirectory> it = getChildren().iterator();
		List<VDKInnerDirectory> directoryList = new ArrayList<VDKInnerDirectory>();

		while (it.hasNext()) {

			VDKInnerDirectory d = it.next() ;
			if(d instanceof VDKInnerFile) {

				if(!it.hasNext())
					d.setNextAddrOffset(-1);

				currentOffset = d.packChildren(destination, 0, currentOffset);
			}
			else directoryList.add(d);					
		}
		
		for(VDKInnerDirectory d : directoryList) {
			
			// Determines offsets and write the directory.
			d.setOffset(currentOffset);
			d.setParentDirOffset(currentOffset + VDK1FilePattern.getDirectoryHeaderLength());
			currentOffset += VDK1FilePattern.getDirectoryHeaderLength(); 				
			
			// Determines offsets and write the dot directory next to the directory.
			d.getDotDirectory().setOffset(currentOffset);
			d.getDotDirectory().setParentDirOffset(currentOffset);
			d.getDotDirectory().setNextAddrOffset(currentOffset + VDK1FilePattern.getDirectoryHeaderLength());
			currentOffset = writeDirectory(destination, d.getDotDirectory(), currentOffset);				
			
			// Determines offsets and write the parent accessor directory next to the dot directory.				
			d.getParentAccessorDirectory().setOffset(currentOffset);
			d.getParentAccessorDirectory().setParentDirOffset(parentDirOffset);
			d.getParentAccessorDirectory().setNextAddrOffset(d.getDotDirectory().getOffset() + VDK1FilePattern.getHeaderLength());
			currentOffset = writeDirectory(destination, d.getParentAccessorDirectory(), currentOffset);				
			
			currentOffset = d.packChildren(destination, d.getDotDirectory().getOffset(), currentOffset);

			// Write the directory's next address offset.
			if(!((directoryList.indexOf(d) + 1) == directoryList.size()))
				d.setNextAddrOffset(currentOffset);
			else d.setNextAddrOffset(VDK1FilePattern.getFinalDirectoryToken());
			writeDirectory(destination, d, d.getOffset());
		}				

		return currentOffset;
	}
	
	protected long writeDirectory(String filePath, VDKInnerDirectory dir, long offset) {
		
		VDKRandomAccessFile raf = null;
		
		try {
			raf = new VDKRandomAccessFile(filePath, "rw");
			
			raf.writeBoolean(true, offset, VDK1FilePattern.IS_DIRECTORY);
			raf.writeString(dir.getName(), offset, VDK1FilePattern.NAME);
			raf.writeUnsignedInt(dir.getRawSize(), offset, VDK1FilePattern.RAW_SIZE);
			raf.writeUnsignedInt(dir.getPackedSize(), offset, VDK1FilePattern.PACKED_SIZE);
			raf.writeUnsignedInt(dir.getParentDirOffset(), offset, VDK1FilePattern.PARENT_DIRECTORY);
			raf.writeUnsignedInt(dir.getNextAddrOffset(), offset, VDK1FilePattern.NEXT_ADDR_OFFSET);
			
			raf.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (dir.getOffset() + VDK1FilePattern.getDirectoryHeaderLength());
	}

	protected Map<String, Long> writeFileList(String destination, long offset) {
		
		Map<String, Long> filePathMap = new HashMap<String, Long>();
		String innerPath, rawPath;
		VDKRandomAccessFile raf = null;
		
		try {
			
			raf = new VDKRandomAccessFile(destination, "rw");
			
			for(VDKInnerDirectory d : getChildren()) {
								
				rawPath = destination.substring(0, destination.lastIndexOf("."));				
				innerPath = d.getSourcePath().substring(rawPath.length()+ 1);
				filePathMap.put(innerPath, d.getOffset());
				raf.writeString(innerPath, offset, VDK1FilePattern.FILE_PATH);
				raf.writeUnsignedInt(d.getOffset(), offset, VDK1FilePattern.FILE_OFFSET);				
				if(!(d instanceof VDKInnerFile))					
					filePathMap.putAll(d.writeFileList(destination, offset));
				offset += VDK1FilePattern.getPathNameBlockLength();				
			}
			
			raf.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return filePathMap;	
	}
	
	public void unpack(String source, String destination) {
		
		String dirDestination = destination + File.separator + name;
		new File(dirDestination).mkdirs();
		
		for(VDKInnerDirectory c : children) {
			
			c.unpack(source, dirDestination);
		}
	}
}