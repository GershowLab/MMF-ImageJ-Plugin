package edu.nyu.physics.gershowlab.mmf;

import ij.IJ;

import java.io.IOException;

import ucar.unidata.io.RandomAccessFile;

public class BackgroundRemovedImageHeader {
	
	private long pos;
	private long idCode;
	private int headerSizeInBytes;
	private int numims;
	private int cvImageDepth;
	private int nChannels;
	private byte[] restOfHeader;
	
	public BackgroundRemovedImageHeader(RandomAccessFile raf) throws IOException {
		pos = raf.getFilePointer();
		idCode = raf.readInt() & 0xFFFFFFFFL;
		if (idCode == 0xf80921afL){
			headerSizeInBytes = raf.readInt();
			cvImageDepth = raf.readInt();
			nChannels = raf.readInt();
			numims = raf.readInt();
			int bytesRead = (int) (raf.getFilePointer() - pos);
			restOfHeader = new byte[headerSizeInBytes - bytesRead];
			raf.read(restOfHeader);
			if (raf.getFilePointer() != pos + headerSizeInBytes) {
				IJ.showMessage("WARNING - BackgroundRemovedImage", "incorrect number of bytes read from header");
				raf.seek(pos + headerSizeInBytes);
			}
			
			return;
		}
		throw new IOException("unrecognized BRI idCode: " + Long.toHexString(idCode));
	}
	

	public long getPos() {
		return pos;
	}
	public long getIdCode() {
		return idCode;
	}
	public int getHeaderSizeInBytes() {
		return headerSizeInBytes;
	}
	public int getNumims() {
		return numims;
	}
	public int getCvImageDepth() {
		return cvImageDepth;
	}
	public int getnChannels() {
		return nChannels;
	}
	public byte[] getRestOfHeader() {
		return restOfHeader;
	}
}
