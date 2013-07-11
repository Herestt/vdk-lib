package com.herestt.ro2.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.herestt.ro2.vdk.VDKFilePattern;
import com.sun.org.apache.bcel.internal.util.ByteSequence;

public class VDKRandomAccessFile extends RandomAccessFile{

	public VDKRandomAccessFile(String filePath, String mode)
			throws FileNotFoundException {
		super(filePath, mode);		
	}

	public byte[] read(int offset, VDKFilePattern pattern) {
		
		byte[] buffer = new byte[pattern.getLength()];
		byte[] resultBuffer = new byte[pattern.getLength()];
		int bytesRead, tmp;
		
		try {

			seek(offset + pattern.getOffset());
			bytesRead = read(buffer);
			
			if (bytesRead != pattern.getLength()) {
				close();
			    throw new IOException("Unexpected End of Stream");
			}
			
			if(pattern.isLittleEndian()){					
				tmp = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();					
				resultBuffer = ByteBuffer.allocate(pattern.getLength()).putInt(tmp).array();
			}					
			else ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN).get(resultBuffer);
			
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
	
	public int readInt(int offset, VDKFilePattern pattern) throws IOException {
		return new ByteSequence(read(offset, pattern)).readInt();
	}
	
	public double readUnsignedInt(int offset, VDKFilePattern pattern) {
		return 0;
	}
	
	public String readString(int offset, VDKFilePattern pattern) throws UnsupportedEncodingException {
		return new String(read(offset, pattern), "UTF-8").trim();
	}
	
	public boolean readBoolean(int offset, VDKFilePattern pattern) throws IOException {
		return new ByteSequence(read(offset, pattern)).readBoolean();
	}
}
