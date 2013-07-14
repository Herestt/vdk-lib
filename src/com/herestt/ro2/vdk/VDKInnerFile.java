package com.herestt.ro2.vdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.InflaterInputStream;

import com.herestt.ro2.io.VDKRandomAccessFile;

public class VDKInnerFile extends VDKInnerDirectory {
	
	public VDKInnerFile() {
		super();
	}

	public VDKInnerFile(String name, int rawSize, int packedSize, int offset, int nextDirOffset) {
		super(name, rawSize, packedSize, offset, nextDirOffset);
	}
	
	// Getters and Setters.
	
	// Methods.
	
	public void unpack(String source, String destination) {
		
		File content = null;
		
		destination = new String(destination + File.separator + getName());
		content = fetchContent(source);
		decompress(content, destination);
	}

	private File fetchContent(String source) {
		
		File tmp = null;
		long fileContentOffset = getOffset() + VDK1FilePattern.getDirectoryHeaderLength();
		VDKRandomAccessFile raf;
		
		try {

			raf = new VDKRandomAccessFile(source, "r");			
			tmp = raf.writeContentToTmp(fileContentOffset, getPackedSize());			
			raf.close();			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return tmp;
	}

	private File decompress(File contentFile, String destination) {
		
		File decompressedFile = null;

		try {
			decompressedFile = new File(destination);
			InputStream in = new InflaterInputStream(new FileInputStream(contentFile));
			OutputStream out = new FileOutputStream(decompressedFile);
			
			shovelIntoOut(in, out);
			contentFile.delete();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return decompressedFile;
	}
	
	private void shovelIntoOut(InputStream in, OutputStream out) {
		
		byte[] buffer = new byte[1024];
		int len;
		
		try {
			while((len = in.read(buffer)) > 0)
			{
				out.write(buffer, 0, len);
			}			
			in.close();
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}
}
