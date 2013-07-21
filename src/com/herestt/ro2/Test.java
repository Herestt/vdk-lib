package com.herestt.ro2;

import java.io.File;
import java.util.zip.Deflater;

import com.herestt.ro2.util.AbstractVDKFileBuilder;
import com.herestt.ro2.util.AbstractVDKFileParser;
import com.herestt.ro2.vdk.AbstractVDKFileInfo;
import com.herestt.ro2.vdk.VDK1FileInfo;
import com.herestt.ro2.vdk.VDKFileVersion;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AbstractVDKFileInfo fileInfo = null;
		
//		AbstractVDKFileParser fileParser = AbstractVDKFileParser.getParser("C:\\Users\\Herestt\\Desktop\\OBJECT_CITY.VDK");				
//		fileInfo = fileParser.start();		
//		fileInfo.unpack("C:\\Users\\Herestt\\Desktop");
		
		AbstractVDKFileBuilder fileBuiler = AbstractVDKFileBuilder.getBuilder("C:\\Users\\Herestt\\Desktop\\ASSET", VDKFileVersion.VDK1);
		fileInfo = fileBuiler.start();
		fileInfo.pack("C:\\Users\\Herestt\\Desktop");
	}
}
