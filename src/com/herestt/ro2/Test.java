package com.herestt.ro2;

import java.io.File;

import com.herestt.ro2.util.AbstractVDKFileParser;
import com.herestt.ro2.vdk.VDK1FileInfo;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		VDK1FileInfo fileInfo = null;
		
		AbstractVDKFileParser fileParser = AbstractVDKFileParser.getParser("C:\\Users\\Herestt\\Desktop\\OBJECT_CITY.VDK");				
		fileInfo = fileParser.start();
		fileInfo.unpack("C:\\Users\\Herestt\\Desktop");
	}

}
