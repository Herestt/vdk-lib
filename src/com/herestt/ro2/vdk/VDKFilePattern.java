package com.herestt.ro2.vdk;

public interface VDKFilePattern {

	public String getKey();
	public int getOffset();
	public int getLength();
	public boolean isLittleEndian();
}
