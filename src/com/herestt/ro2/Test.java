package com.herestt.ro2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.herestt.ro2.ct.CTFileInfo;
import com.herestt.ro2.util.AbstractVDKFileBuilder;
import com.herestt.ro2.util.AbstractVDKFileParser;
import com.herestt.ro2.vdk.AbstractVDKFileInfo;
import com.herestt.ro2.vdk.VDKFileVersion;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		AbstractVDKFileInfo fileInfo = null;
		CTFileInfo ctFileInfo;
		
//		AbstractVDKFileParser fileParser = AbstractVDKFileParser.getParser("C:\\Users\\Herestt\\Desktop\\OBJECT_CITY.VDK");				
//		fileInfo = fileParser.start();		
//		fileInfo.unpack("C:\\Users\\Herestt\\Desktop");
		
//		AbstractVDKFileBuilder fileBuiler = AbstractVDKFileBuilder.getBuilder("C:\\Users\\Herestt\\Desktop\\ASSET", VDKFileVersion.VDK1);
//		fileInfo = fileBuiler.start();
//		fileInfo.pack("C:\\Users\\Herestt\\Desktop");
		
		ctFileInfo = new CTFileInfo(new File("C:\\Users\\Herestt\\Desktop\\classinfo.ct"));
		ctFileInfo.addObserver(new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				
				if(arg instanceof Map<?, ?>){
					Map<String, Object> args = (HashMap<String, Object>)arg;
					System.out.println( (int)(100 * (float)args.get("progress")) + "%");
				} 				
			}
		});
		ctFileInfo.toCSVFile("C:\\Users\\Herestt\\Desktop\\classinfo.csv");
	}
}