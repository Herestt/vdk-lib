package com.herestt.ro2;

import com.herestt.ro2.util.AbstractVDKFileParser;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AbstractVDKFileParser fileParser = AbstractVDKFileParser.getParser("C:\\Users\\Herestt\\Desktop\\OBJECT_CITY.vdk");
		
		fileParser.start();	
		
		
		
	}

}
