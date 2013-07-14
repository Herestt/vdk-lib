package com.herestt.ro2.vdk;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
	
	public VDKInnerDirectory() {
		
		children = new HashSet<VDKInnerDirectory>();
	};	
	
	public VDKInnerDirectory(String name, int rawSize, int packedSize,
			int offset, int nextAddrOffset) {
		super();
		this.name = name;
		this.rawSize = rawSize;
		this.packedSize = packedSize;
		this.offset = offset;
		this.nextAddrOffset = nextAddrOffset;
		children = new HashSet<VDKInnerDirectory>();
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
	
	public void unpack(String source, String destination) {
		
		String dirDestination = destination + File.separator + name;
		new File(dirDestination).mkdirs();
		
		for(VDKInnerDirectory c : children) {
			
			c.unpack(source, dirDestination);
		}
	}
}