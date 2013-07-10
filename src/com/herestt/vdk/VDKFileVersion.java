package com.herestt.vdk;

public enum VDKFileVersion {

	VDK0 ("VDISK1.0"),
	VDK1 ("VDISK1.1");
	
	private final String strFileVersion;
	
	private VDKFileVersion(String strFileVersion){
		
		this.strFileVersion = strFileVersion;
	}
	
	public String getSTRFileVersion(){
		return this.strFileVersion;
	}
	
	public static VDKFileVersion getEnumFromVersion(String strFileVersion){
		
		for(VDKFileVersion v : VDKFileVersion.values()){
			System.out.println(strFileVersion + ": " + v.getSTRFileVersion());
			if(v.getSTRFileVersion().equals(strFileVersion))
				return v;
		}
		
		return null;
	}
}
