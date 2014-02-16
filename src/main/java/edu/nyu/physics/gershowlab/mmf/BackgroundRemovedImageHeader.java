package edu.nyu.physics.gershowlab.mmf;
/*
 * 
 * Copyright 2013,2014 by Marc Gershow and Natalie Bernat
 * 
 * This file is part of MMF-ImageJ-Plugin.
 * 
 * MMF-ImageJ-Plugin is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, 
 * or (at your option) any later version.
 * 
 * MMF-ImageJ-Plugin is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MMF-ImageJ-Plugin.  If not, see http://www.gnu.org/licenses/.
 */

import ij.IJ;

import java.io.IOException;
import java.nio.ByteBuffer;

import ucar.unidata.io.RandomAccessFile;

/**
 * Metadata for a background removed image 
 * 
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * @see BackgroundRemovedImage
 *
 */
public class BackgroundRemovedImageHeader {
	
	/**
	 * The file pointer for the beginning of the header
	 */
	private long pos;
	/**
	 * The id code
	 */
	private long idCode;
	/**
	 * The size of the header, measured in bytes
	 */
	private int headerSizeInBytes;
	/**
	 * The number of subimages associated with the background removed image
	 */
	private int numims;
	/**
	 * The image depth
	 */
	private int cvImageDepth;
	/**
	 * The number of channels
	 */
	private int nChannels;
	/**
	 * The unprocessed portion of the header
	 */
	private byte[] restOfHeader;
	
	
	/**
	 * Creates a BackgroundRemovedImageHeader for the frame located at the current file pointer of the MMF file
	 * 
	 * @param raf			The MMF file
	 * @throws IOException
	 * @see MmfFile
	 */
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
	

	/**
	 * Returns the file pointer for the beginning of the header
	 * @return The file pointer for the beginning of the header
	 */
	public long getPos() {
		return pos;
	}
	
	/**
	 * Returns the id code
	 * @return The id code
	 */
	public long getIdCode() {
		return idCode;
	}
	
	/**
	 * Returns the size of the header, measured in bytes
	 * @return The size of the header
	 */
	public int getHeaderSizeInBytes() {
		return headerSizeInBytes;
	}
	
	/**
	 * Returns the number of subimages associated with the background removed image
	 * @return The number of subimages associated with the background removed image
	 */
	public int getNumims() {
		return numims;
	}
	
	/**
	 * Returns the image depth
	 * @return The image depth
	 */
	public int getCvImageDepth() {
		return cvImageDepth;
	}
	
	/**
	 * Return the number of channels
	 * @return The number of channels
	 */
	public int getnChannels() {
		return nChannels;
	}
	
	/**
	 * Returns an array of bytes containing the unprocessed portion of the header
	 * @return The unprocessed portion of the header
	 */
	public byte[] getRestOfHeader() {
		return restOfHeader;
	}


	public ImageMetaData getMetaData() {
		return ImageMetaData.loadMetaData(ByteBuffer.wrap(restOfHeader));
	}

}
