package com.herestt.ro2.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;

import com.herestt.vdk.VDK1FileInfo;
import com.herestt.vdk.VDKFileVersion;

public abstract class AbstractVDKFileParser {

	private static String vdkFilePath;
	
	public static AbstractVDKFileParser getParser(String vdkTargetFilePath){
		
		VDKFileVersion vdkFileVersion;
		
		vdkFilePath = vdkTargetFilePath;
		
		vdkFileVersion = readVersion(vdkFilePath);
		
		if(vdkFileVersion == VDKFileVersion.VDK0)
			return new VDK0FileParser(vdkFilePath);

		if(vdkFileVersion == VDKFileVersion.VDK1)					
			return new VDK1FileParser(vdkFilePath);

		//TODO - Herestt: Throw exception if the file isn't VDISK type;
		return null;
	}

	private static VDKFileVersion readVersion(String vdkFilePath) {
	 		
		FileInputStream in;
		int currentValue;	
		String strVDKFileVersion = null;	
		StringBuilder sb= new StringBuilder();
		
		try {
			
			in = new FileInputStream(vdkFilePath);
			
			while((currentValue = in.read()) != 0){
				
				sb.append((char) Integer.parseInt(Integer.toHexString(currentValue), 16));
			}
			
			in.close();
			strVDKFileVersion = sb.toString();			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
						
		return VDKFileVersion.getEnumFromVersion(strVDKFileVersion);
	}
	
	public abstract VDK1FileInfo start();
}
