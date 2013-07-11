package com.herestt.ro2.vdk;

import java.util.HashSet;
import java.util.Set;

public class VDKInnerDirectory {

	private String name;
	private int rawSize;
	private int packedSize;	
	private int offset;
	private int parentDirOffset;
	private int nextAddrOffset;
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

	public int getRawSize() {
		return rawSize;
	}

	public void setRawSize(int rawSize) {
		this.rawSize = rawSize;
	}

	public int getPackedSize() {
		return packedSize;
	}

	public void setPackedSize(int packedSize) {
		this.packedSize = packedSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getParentDirOffset() {
		return parentDirOffset;
	}

	public void setParentDirOffset(int parentDirOffset) {
		this.parentDirOffset = parentDirOffset;
	}

	public int getNextAddrOffset() {
		return nextAddrOffset;
	}

	public void setNextAddrOffset(int nextAddrOffset) {
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
}
