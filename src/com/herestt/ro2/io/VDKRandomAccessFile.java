package com.herestt.ro2.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.herestt.ro2.vdk.VDK1FilePattern;
import com.herestt.ro2.vdk.VDKFilePattern;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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
	
	public long readUnsignedInt(long offset, VDKFilePattern pattern) throws IOException {
		return readInt(offset, pattern) & 0xFFFFFFFFL;
	}
	
	public String readString(long offset, VDKFilePattern pattern) throws UnsupportedEncodingException {
		return new String(read(offset, pattern), "UTF-8").trim();
	}
	
	public boolean readBoolean(long offset, VDKFilePattern pattern) throws IOException {
		return new ByteSequence(read(offset, pattern)).readBoolean();
	}
	
	public File putIntoTemp(long offset, long length) {
		
		File tmp = null;		
		BufferedOutputStream bos = null;
		long l = 0;
		
		try {
			
			tmp = File.createTempFile("vdkFileContent", ".tmp");
			bos = new BufferedOutputStream(new FileOutputStream(tmp.getAbsoluteFile()));
					
			seek(offset);
			while(l < length){
					
				bos.write(read());
				l++;
			}
				
			bos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tmp;
	}

	public void write(byte[] data, long offset, VDK1FilePattern pattern) {
	
		byte[] resultBuffer = new byte[pattern.getLength()];
		
		try {
			
			seek(offset + pattern.getOffset());
			
			if(pattern.isLittleEndian()) {
				for(int i = 0; i < data.length; i++)
					resultBuffer[(data.length - 1) - i] = data[i];
			}			
			// Data are naturally red as big endian bit sequence.
			else {										
				for(int i = 0; i < data.length; i++)
					resultBuffer[i] = data[i];
			}
			
			write(resultBuffer);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeString(String data, long offset, VDK1FilePattern pattern) {		
		write(data.getBytes(), offset, pattern);
	}

	public void writeUnsignedInt(long data, long offset, VDK1FilePattern pattern) {
				
		byte[] dataByte = ByteBuffer.allocate(Long.SIZE / 8).putLong(data).array();
		byte[] result = new byte[Integer.SIZE / 8];
		int shift = 4;
		
		for(int i = 0; i < result.length; i++) 			
			result[i] = dataByte[i + shift];
		
		write(result, offset, pattern);		
	}
}
