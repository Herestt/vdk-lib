package com.herestt.ro2.util;

import com.herestt.ro2.vdk.AbstractVDKFileInfo;
import com.herestt.ro2.vdk.VDKFileVersion;

public abstract class AbstractVDKFileBuilder {

	private static String dirPath;
	
	public static AbstractVDKFileBuilder getBuilder(String sourceDirPath, VDKFileVersion version) {
		
		dirPath = sourceDirPath;
		
		if (version == VDKFileVersion.VDK0)
			return new VDK0FileBuilder(dirPath);
		
		if(version == VDKFileVersion.VDK1)
			return new VDK1FileBuilder(dirPath);
		
		//TODO - Herestt: Throw exception if the target isn't a directory.
		return null;
	}
	
	public abstract AbstractVDKFileInfo start();
}
