package com.herestt.ro2.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.sun.org.apache.bcel.internal.util.ByteSequence;

public class NativeRandomAccessFile {

	private File file;
	private RandomAccessFile raf;
	private int endianness;
	
	public static int BIG_ENDIAN = 0x00;
	public static int LITTLE_ENDIAN = 0x01;
	
	public NativeRandomAccessFile(String name, String mode) throws FileNotFoundException {
		new NativeRandomAccessFile(new File(name), mode);
	}
	
	public NativeRandomAccessFile(File file, String mode) throws FileNotFoundException {
		
		this.file = file;
		this.endianness = BIG_ENDIAN;
		raf = new RandomAccessFile(file, mode);	
	}	
	
	public void seek(long pos) throws IOException {		
		raf.seek(pos);
	}
	
	public void order(int endianness) {
		
		if((endianness == BIG_ENDIAN) || (endianness == LITTLE_ENDIAN))
			this.endianness = endianness;
	}
	
	public void close() throws IOException {
		raf.close();
	}
	public void read(byte[] buffer) throws IOException {
		
		byte[] tmpBuffer = buffer.clone();
		
		raf.read(tmpBuffer);
		
		if(endianness == LITTLE_ENDIAN) {
			for(int i = 0; i < tmpBuffer.length; i++)
				buffer[(tmpBuffer.length - 1) - i] = tmpBuffer[i];
		} else
			for(int i = 0; i < tmpBuffer.length; i++)
				buffer[i] = tmpBuffer[i];
		
	}
	
	public int readInt() throws IOException {
		
		byte[] buffer = new byte[4];
		
		read(buffer);
		return new ByteSequence(buffer).readInt();
	}
	
	public long readUnsignedInt() throws IOException {		
		return (readInt() & 0xFFFFFFFFL);
	}

	public int readUnsignedByte() throws IOException {		
		return raf.readUnsignedByte();
	}

	public short readShort() throws IOException {
		
		byte[] buffer = new byte[2];
		
		read(buffer);
		return new ByteSequence(buffer).readShort();
	}
	
	public int readUnsignedShort() throws IOException {
//		int i = raf.readUnsignedShort();
		return (readShort() & 0xFFFF);
	}
	
	public long readLong() throws IOException {
		
		return raf.readLong();
	}

	public double readUnsignedFloat() throws IOException {
		
		return raf.readFloat();
	}

	public String readUTF16() throws IOException {
		
		byte[] buffer = new byte[2];
		
		read(buffer);		
		return new String(buffer, "UTF-16");
	}

	public String readUTF16String(long charCount) throws IOException {
		
		long l = 0;
		StringBuilder sb = new StringBuilder();
		
		while(l < charCount) {
			
			sb.append(readUTF16());
			l++;
		}
		
		return sb.toString();
	}
}
