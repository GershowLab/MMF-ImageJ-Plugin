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

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.io.FileInfo;
import ij.io.ImageReader;
import ij.process.ImageProcessor;
import ucar.unidata.io.RandomAccessFile;

/**
 * An implementation of RandomAccessFile tailored to the MMF file format. Provides access to the information contained in the main file header
 * and the MMF image stack headers. 
 * <p>
 * (See https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/v4.0/javadocAll/ucar/unidata/io/RandomAccessFile.html).
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * @see RandomAccessFile
 * @see ImageStackLocator
 */
public class MmfFile extends RandomAccessFile {

	/**
	 * Main MMF header
	 */
	private MmfHeader header;
	/**
	 * The locations of MMF ImageStacks within the MMF file
	 */
	private ArrayList<ImageStackLocator> stackLocations;
	private boolean parsed = false;
	
	/**
	 * Returns the number of MMF ImageStacks
	 * 
	 * @return The number of MMF ImageStacks
	 */
	public int getNumStacks() {
		return stackLocations.size();
	}
	
	/**
	 * Returns the main MMF header
	 * 
	 * @return The main MMF header
	 */
	public MmfHeader getHeader() {
		if (!parsed) {
			try {
				parse();
			} catch (IOException e) {
				return null;
			}
		}
		return header;
	}
	
	/**
	 * Returns the total number of frames in the MMF movie
	 * 
	 * @return The total number of frames in the MMF movie
	 */
	public int getNumFrames(){
		if (!parsed) {
			try {
				parse();
			} catch (IOException e) {
				return -1;
			}
		}
		return stackLocations.get(stackLocations.size()-1).getLastFrame() - stackLocations.get(0).getStartFrame();
	}

	/**
	 * Returns a status report pertaining to the parsing of the file. Includes the size and number of frames and stacks for the entire MMF, and 
	 * the number of frames and location for each stack. Indicates parsing errors.
	 * 
	 * @return A String containing the status report
	 */
	public String getReport() {
		StringBuilder sb = new StringBuilder();
		if (!parsed) {
			return "Not Parsed";
		}
		sb.append(header.headerText + "\n\n");
		try {
			sb.append("total stacks = " + getNumStacks() + ";  total frames = " + getNumFrames() + ";  file size = " + length() + "\n");
		} catch (IOException e) {
			sb.append("file error: " + e);
			return sb.toString();
		}
		int j = 0;
		for (ImageStackLocator i : stackLocations) {
			sb.append(String.format("stack num: %d,  frames %d-%d, locInFile = %d\n", j++, i.getStartFrame(), i.getLastFrame(), i.filePosition));
		}
		return sb.toString();
	}
	
	/**
	 * Reads and stores the main MMF header, and builds the list of MMF ImageStack locations. 
	 * @throws IOException
	 * @see MMFHeader
	 * @see ImageStackLocator
	 */
	public void parse() throws IOException {
		stackLocations = new ArrayList<ImageStackLocator>();
		seek(0);
		header = readFileHeader();
		int frame = 0;
		while (!isAtEndOfFile()) {
			ImageStackHeader h = readImageStackHeader();
			stackLocations.add(new ImageStackLocator(h, frame));
			frame += h.nframes;
			long nextpos;
			if ((nextpos = h.filePosition + h.stackSize) < length()) {
				seek(nextpos);
			} else {
				break;
			}
		}
		parsed = true;
	}
	
	

	/**
	 * Creates an MMF file.
	 * 
	 * @param location		Location of the file
	 * @param mode			Access mode, either: r, rw, rws, or rwd
	 * @param bufferSize	Size of buffer
	 * @throws IOException
	 */
	public MmfFile(String location, String mode, int bufferSize)
			throws IOException {
		super(location, mode, bufferSize);
		order (LITTLE_ENDIAN);
	}

	/**
	 * Creates an MMF file.
	 * 
	 * @param bufferSize	Size of buffer
	 */
	public MmfFile(int bufferSize) {
		super(bufferSize);
		order (LITTLE_ENDIAN);
	}

	/**
	 * Creates an MMF file.
	 * 
	 * @param location		Location of the file
	 * @param mode			Access mode, either: r, rw, rws, or rwd
	 * @throws IOException
	 */
	public MmfFile(String location, String mode) throws IOException {
		super(location, mode);
		order (LITTLE_ENDIAN);
	}


	/**
	 * Processes (and returns) the main MMF header, positioning the file pointer at the beginning of the first ImageStack.
	 * 
	 * @return The main MMF header
	 * @throws IOException
	 */
	public MmfHeader readFileHeader() throws IOException{
		long pos = getFilePointer();
		StringBuilder headerText = new StringBuilder();
		char c;
		while ((c = (char) readUnsignedByte()) != 0) {
			headerText.append(c);
		}
		long idcode = readInt() & 0x00000000ffffffffL;
		int headerSizeInBytes = readInt();
		int keyFrameInterval= readInt();
		int threshAboveBackground = readInt();
		int threshBelowBackground = readInt();
		seek (pos + headerSizeInBytes);
		return new MmfHeader(headerText.toString(), idcode, headerSizeInBytes, keyFrameInterval, threshAboveBackground, threshBelowBackground);
	}
	
	
	/**
	 * Processes (and returns) the header for the MMF ImageStack located at the current file pointer, positioning the file 
	 * pointer at the beginning of the data in that ImageStack.
	 * @return The header for the MMF ImageStack located at the current file pointer
	 * @throws IOException
	 */
	public ImageStackHeader readImageStackHeader() throws IOException {
		long pos = getFilePointer();
		long idCode = readInt() & 0x00000000ffffffffL;
		int headerSize = readInt();
		int stackSize = readInt();
		int nframes = readInt();
		
		seek (pos + headerSize);	
		
		return new ImageStackHeader(idCode, headerSize, stackSize, nframes, pos);
	}
	
	/**
	 * Returns the MMF ImageStack containing the query frame
	 * 
	 * @param frameNumber	The query frame 
	 * @return The MMF ImageStack containing the query frame
	 * @see CommonBackgroundStack
	 */
	public CommonBackgroundStack getStackForFrame (int frameNumber) {

		if (frameNumber<0 || frameNumber>getNumFrames()){
			IJ.showMessage("mmfReader","Frame Index Error; MmfFile");
			return null; 
		}
		
		//find correct imageStackLocator
		ImageStackLocator isl = findStackLocForFrame(frameNumber);
		
		//read stack from file
		CommonBackgroundStack stack = null;
		try {
			stack = new CommonBackgroundStack(isl, this);
			
		} catch (IOException e) {
			IJ.showMessage("mmfReader","Getting Stack for Frame was unsuccessful in MmfFile.\n\n Error: " +e);
			return null; 
		}
		return stack;
	}
	
	/**
	 * Returns (when possible) the ImageStackLocator containing info about the Stack containing the query frame
	 * 
	 * @param frameNumber The query frame
	 * @return The ImageStackLocator containing info about the Stack containing the query frame
	 */
	private ImageStackLocator findStackLocForFrame(int frameNumber){
		if (frameNumber<0 || frameNumber>=getNumFrames()){
			return null; 
		}
		
		ImageStackLocator isl;
		for (int i=0; i<stackLocations.size(); i++){
			isl = stackLocations.get(i);
			if (isl.getStartFrame()<=frameNumber && isl.getLastFrame()>=frameNumber){
				return isl;
			}				
		}
		int[] startFrames = new int[stackLocations.size()];
		int[] endFrames = new int[stackLocations.size()];
		String msg = "frame: " + frameNumber + " not found in startFrames: " + startFrames + " - end frames - " + endFrames;
		IJ.showMessage("mmfReader",msg);
		return null;
		

	}
	
	/**
	 * Processes the data for a background removed image frame, converting it to a BackgroundRemovedImage object.
	 * 
	 * @param bak					The background image
	 * @param backgroundFileInfo	Metadata for the background image
	 * @return The background removed image 
	 * @throws IOException
	 * @see BackgroundRemovedImage
	 * @see BackgroundRemovedImageHeader
	 */
	BackgroundRemovedImage readBRI(ImageProcessor bak, FileInfo backgroundFileInfo) throws IOException {
		BackgroundRemovedImageHeader h = new BackgroundRemovedImageHeader(this);
		BackgroundRemovedImage bri = new BackgroundRemovedImage(h, bak);
		for (int j = 0; j < h.getNumims(); ++j) {
			bri.addSubImage(readBRISubIm(bak, backgroundFileInfo));
		}
		return bri;
		
	}
	
	 
	/**
	 * Processes (and returns) a subimage for a background removed image.
	 * 
	 * @param bak 					The background image
	 * @param backgroundFileInfo	Metadata for the background image
	 * @return The subimage
	 * @throws IOException
	 */
	private BRISubImage readBRISubIm(ImageProcessor bak, FileInfo backgroundFileInfo) throws IOException {
		Rectangle r = new Rectangle();
		
		r.x = readInt();
		r.y = readInt();
		r.width = readInt();
		r.height = readInt();
		ImageProcessor ip = bak.createProcessor(r.width, r.height);
		FileInfo fi = (FileInfo) backgroundFileInfo.clone();
		fi.width = r.width;
		fi.height = r.height;
    	byte buf[] = new byte[fi.width*fi.height*fi.getBytesPerPixel()];
    	read(buf);
    	ByteArrayInputStream bis = new ByteArrayInputStream(buf);
    	Object pixels = (new ImageReader(fi)).readPixels(bis);
		ip.setPixels(pixels);
		return new BRISubImage(ip,r);
	}
	
}
