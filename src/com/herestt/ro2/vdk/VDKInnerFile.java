package com.herestt.ro2.vdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.herestt.ro2.io.VDKRandomAccessFile;

public class VDKInnerFile extends VDKInnerDirectory {
	
	public VDKInnerFile() {
		super();
	}

	public VDKInnerFile(String name, long rawSize, long packedSize,
			long offset, long parentDirOffset, long nextAddrOffset) {
		super(name, rawSize, packedSize, offset, parentDirOffset, nextAddrOffset);
	}
	
	// Getters and Setters.
	
	// Methods.	
	@Override
	protected long packChildren(String destination, long parentDirOffset, long nextAddrOffset) {
		
		long currentOffset = nextAddrOffset;
		File compressedFile = null;
		
		this.setOffset(currentOffset);
		compressedFile = compress(new File(this.getSourcePath()));
		this.setPackedSize(compressedFile.length());
		if(getNextAddrOffset() == -1)
			this.setNextAddrOffset(VDK1FilePattern.getFinalDirectoryToken());
		else
			this.setNextAddrOffset(currentOffset + VDK1FilePattern.getDirectoryHeaderLength() + compressedFile.length());
		currentOffset = writeFile(destination, compressedFile, nextAddrOffset);
		
		return currentOffset;
	}	

	private long writeFile(String filePath, File compressedFile, long offset) {
		
		VDKRandomAccessFile raf = null;
		
		try {
			raf = new VDKRandomAccessFile(filePath, "rw");
			
			raf.writeBoolean(false, offset, VDK1FilePattern.IS_DIRECTORY);
			raf.writeString(getName(), offset, VDK1FilePattern.NAME);
			raf.writeUnsignedInt(getRawSize(), offset, VDK1FilePattern.RAW_SIZE);
			raf.writeUnsignedInt(getPackedSize(), offset, VDK1FilePattern.PACKED_SIZE);
			raf.writeUnsignedInt(getParentDirOffset(), offset, VDK1FilePattern.PARENT_DIRECTORY);
			raf.writeUnsignedInt(getNextAddrOffset(), offset, VDK1FilePattern.NEXT_ADDR_OFFSET);
			raf.writeFileContent(compressedFile, offset, VDK1FilePattern.FILE_CONTENT);
			
			raf.close();
			compressedFile.delete();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return (offset + VDK1FilePattern.getDirectoryHeaderLength() + compressedFile.length());
	}

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
			tmp = raf.putIntoTemp(fileContentOffset, getPackedSize());			
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

	private File compress(File rawFile) {
		
		File compressedFile = null;
		InputStream in = null;
		OutputStream out = null;
		
		try {	
			
			compressedFile = File.createTempFile("vdkFileContent", ".tmp");
			in = new FileInputStream(rawFile);
			out = new DeflaterOutputStream(new FileOutputStream(compressedFile));
			
			shovelIntoOut(in, out);
			in.close();
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return compressedFile;
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
