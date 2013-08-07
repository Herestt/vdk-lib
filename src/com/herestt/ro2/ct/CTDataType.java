package com.herestt.ro2.ct;

public enum CTDataType {

		// DATA_TYPE(<NAME>, <DATA_TYPE_ID>, <LENGTH = byte>)
	 	BYTE("BYTE", 0x02, 1),
		SHORT("SHORT", 0x03, 2),
		WORD("WORD", 0x04, 2),
		INTEGER("INTEGER", 0x05, 4),
		DWORD("DWORD", 0x06, 4),
		UNKOWN0x07("UNKOWN0x07", 0x07, 4),
		STRING("STRING", 0x08, Integer.MAX_VALUE), //Length is given by a DWORD value before each string.
		FLOAT("FLOAT", 0x09, 4),
		QWORD("QWORD", 0x0B, 8/* ? */);
		
		private final String name;
		private final int dataTypeID;
		private final int length;	
		
		private CTDataType(String name, int dataTypeID, int length) {
			
			this.name = name;
			this.dataTypeID = dataTypeID;
			this.length = length;			
		}
		
		public String getName() {
			return name;
		}

		public int getDataTypeID() {
			return dataTypeID;
		}

		public int getLength() {
			return length;
		}
		
		public static CTDataType getCTDataType(int dataTypeID) {			
			for(CTDataType dt : values())
				if(dt.getDataTypeID() == dataTypeID)
					return dt;
			return null;
		}
}
