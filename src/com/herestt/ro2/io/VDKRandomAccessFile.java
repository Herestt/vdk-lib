package com.herestt.ro2.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import com.herestt.ro2.vdk.VDKFilePattern;
import com.sun.org.apache.bcel.internal.util.ByteSequence;

public class VDKRandomAccessFile extends RandomAccessFile{

	public VDKRandomAccessFile(String filePath, String mode)
			throws FileNotFoundException {
		super(filePath, mode);		
	}

	public byte[] read(long offset, VDKFilePattern pattern) {
		
		byte[] buffer = new byte[pattern.getLength()];
		byte[] resultBuffer = new byte[pattern.getLength()];
		int bytesRead;
		
		try {

			seek(offset + pattern.getOffset());
			bytesRead = read(buffer);
			
			if (bytesRead != pattern.getLength()) {
				close();
			    throw new IOException("Unexpected End of Stream");
			}
			
			if(pattern.isLittleEndian()) {
				for(int i = 0; i < buffer.length; i++)
					resultBuffer[(buffer.length - 1) - i] = buffer[i];
			}			
			// Data are naturally red as big endian bit sequence.
			else resultBuffer = buffer;			
			
		} catch (IOException e) {
			
			try {
				close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return resultBuffer;
	}
		
	public int readInt(long offset, VDKFilePattern pattern) throws IOException {
		return new ByteSequence(read(offset, pattern)).readInt();
	}
	
	public long readUnsignedInt(long dotDirectoryOffset, VDKFilePattern pattern) throws IOException {
		return readInt(dotDirectoryOffset, pattern) & 0xFFFFFFFFL;
	}
	
	public String readString(long currentOffset, VDKFilePattern pattern) throws UnsupportedEncodingException {
		return new String(read(currentOffset, pattern), "UTF-8").trim();
	}
	
	public boolean readBoolean(long offset, VDKFilePattern pattern) throws IOException {
		return new ByteSequence(read(offset, pattern)).readBoolean();
	}
	
	public File writeContentToTmp(long offset, long length) {
		
		File tmp = null;		
		BufferedOutputStream bos = null;
		long l = 0;
		int c;
		
		try {
			
			tmp = File.createTempFile("vdkFileContent", ".tmp");
			bos = new BufferedOutputStream(new FileOutputStream(tmp.getAbsoluteFile()));
					
			seek(offset);
			while(l < length){
				
				c = read();
				
				bos.write(c);
				l++;
			}
				
			bos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tmp;
	}
}
