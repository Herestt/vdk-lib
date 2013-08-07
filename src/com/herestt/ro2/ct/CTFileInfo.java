package com.herestt.ro2.ct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import com.herestt.ro2.io.NativeRandomAccessFile;

public class CTFileInfo extends Observable {

	private File ctFile;
	private File csvFile;
	private NativeRandomAccessFile nraf;
	private FileWriter csvWriter;
	private Map<String, Object> args;
	
	private String header;
	private long columnCount;
	private List<CTDataType> columnTypes;
	private long entryCount;
	private long writtedEntryCount;
	
	private final int fileHeaderLength = 0x40;	
		
	/**  
	 * 
	 * @param filePath
	 */
	public CTFileInfo(String filePath) {
		new CTFileInfo(new File(filePath));
	}
	
	/**
	 * 
	 * @param ctFile
	 */
	public CTFileInfo(File ctFile) {
	
		this.ctFile = ctFile;		
		args = new HashMap<String, Object>();
	}
	
	/**
	 * Parse a .ct file so as to build a .csv file.
	 * 
	 * @param target .csv target file path. 
	 * @return .csv file.
	 * @throws Exception 
	 */
	public File toCSVFile(String target) throws Exception {

		try {
			
			nraf = new NativeRandomAccessFile(ctFile, "rw");			
			csvFile = new File(target);		
			csvFile.createNewFile();
			csvWriter = new FileWriter(csvFile);			
					
			nraf.seek(0);
			nraf.order(NativeRandomAccessFile.LITTLE_ENDIAN);
			
			readHeader();
			readTableHeader();			
			readDataTypes();			
			readContent();
			
			csvWriter.flush();
			csvWriter.close();
			nraf.close();
						
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return csvFile;
	}	

	/**
	 * Read the .ct file header and write it to the targeted .csv file.
	 * 
	 * @param csvFile
	 * @throws IOException 
	 */
	private void readHeader() throws IOException {		
				
		Set<String> row = new HashSet<String>();			

		header = nraf.readUTF16String(fileHeaderLength / 2);
		row.add(header);		
		writeCSVRow(row);
	}

	/**
	 * Read data type of each table column and write it to the targeted file.
	 * 
	 * @param csvFile
	 * @throws IOException 
	 */
	private void readTableHeader() throws IOException {
		
		long columnCount = nraf.readUnsignedInt();
		long l = 0;
		StringBuilder sb;
		Set<String> row = new LinkedHashSet<String>();

		while(l < columnCount) {
			
			sb = new StringBuilder();
			long charCount = nraf.readUnsignedInt();
			long m = 0;
			
			while(m < charCount) {
				
				sb.append(nraf.readUTF16());
				m++;
			}
			
			row.add(sb.toString());			
			l++;
		}
		
		// The column count is written twice. The first one at the beginning
		// of the table header and the second at the end(or at the beginning of the data type table). 
		if(columnCount != nraf.readUnsignedInt())
			throw new IOException("Table header mismatch with the column count.");
		this.columnCount = columnCount;
		writeCSVRow(row);
	}
	
	/**
	 * 	Read the data type of each column.
	 * 
	 * @throws Exception 
	 */
	private void readDataTypes() throws Exception {
		
		columnTypes = new LinkedList<CTDataType>();
		List<String> columnTypesName = new LinkedList<String>();
		long l = 0;
		
		while(l < columnCount) {
						
			int i = (int)nraf.readUnsignedInt();
			CTDataType dt = CTDataType.getCTDataType(i);
			if(dt != null) {
				
				columnTypes.add(dt);
				columnTypesName.add(dt.getName());
			}				
			else
				throw new Exception("The table contians an unkown data type");
			l++;
		}
		writeCSVRow(columnTypesName);
	}
	
	
	/**
	 * Read each content line so as to write it into the targeted .csv file.
	 * @throws IOException 
	 */
	private void readContent() throws IOException {
				
		List<String> row = null;
		String column = null;
		
		entryCount = nraf.readUnsignedInt();
		writtedEntryCount = 0;
		while(writtedEntryCount < entryCount) {
			
			row = new LinkedList<String>();
			for(CTDataType dt : columnTypes) {
				
				switch(dt) {
					
					case BYTE : 
						column = Integer.toString(nraf.readUnsignedByte());
						break;
					
					case SHORT: case WORD:
						column = Integer.toString(nraf.readUnsignedShort());
						break;
					
					case DWORD: case UNKOWN0x07: case INTEGER:
						column = Long.toString(nraf.readUnsignedInt());
						break;							
						
					case QWORD:
						column = Long.toString(nraf.readLong());						
						break;
						
					case FLOAT:						
						column = Double.toString(nraf.readUnsignedFloat());
						break;
						
					case STRING:
						long charCount = nraf.readUnsignedInt();
						column = nraf.readUTF16String(charCount);
						break;
					
					default : 
						break;
				}
				
				row.add(column);
			}
			
			writeCSVRow(row);
			writtedEntryCount++;
			
			// Notify the translation progress to observers.			
			args.put("progress", new Float(writtedEntryCount / (float)entryCount));
			setChanged();			
			notifyObservers(args);								
		}
				
	}	
	
	private void writeCSVRow(Collection<String> line) throws IOException {
		
		for(String s : line) {
			
			csvWriter.append(s);
			csvWriter.append(";");
		}	
		csvWriter.append("\n");
		csvWriter.flush();			
	}

	// Getters and Setters.
	public long getEntryCount() {
		return entryCount;
	}

	public long getWrittedEntryCount() {
		return writtedEntryCount;
	}
}
