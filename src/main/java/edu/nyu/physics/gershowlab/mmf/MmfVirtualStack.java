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


import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import ij.*;
import ij.process.*;


/**
 *	Allows the MMF file to be accessed through ImageJ's ImageStack methods without loading all of the data into memory. 
 * 
 *  @author Natalie Bernat
 *  @author Marc Gershow
 *  @version 1.0
 *  @see VirtualStack
 *  @see MmfFile
 *  @see CommonBackgroundStack
 */
public class MmfVirtualStack extends VirtualStack {


	private String			fileName;
	private String			fileDir;
	/**
	 * The MMF file associated with the stack
	 */
	private MmfFile 		raf;	
	/**
	 * The image depth
	 */
	private int 			depth;
	
	/**
	 * The segment of the movie which is currently loaded in memory
	 */
	private CommonBackgroundStack currentStack;
	
	private ImagePlus imp;
	/**
	 * Creates an MmfVirtualStack from the file specified in the arguments, initially loading the first segment of the movie into memory. 
	 * 
	 */
	public MmfVirtualStack(String path){
		File f = new File(path);
		this.fileName = f.getName();
		this.fileDir  = f.getParent();
		depth = -1;
		try{	
			raf = new MmfFile(path, "r");
			raf.parse();			
		} catch(Exception e){
			IJ.showMessage("MmfVirtualStack","Opening of: \n \n"+path+"\n \n was unsuccessful.\n\n Error: " +e);
			return;
		}
		

		imp = null;
		
		currentStack = raf.getStackForFrame(1);
		
	}
	
	/**
	 * Indicates whether or not the Mmf file is invalid
	 * 
	 * @return		true if the file is invalid, false otherwise 
	 */
	public boolean fileIsNull(){
		//Check that the file isn't null
		if (raf == null || raf.getNumFrames()==0 || getProcessor(1)==null){
			IJ.showMessage("MmfVirtualStack","Error: Frames missing or empty");
			return true;
		}
		return false;
	}
	public void saveMetaData(String savepath){	
		if (raf == null) {
			return;
		}
		new metadata_Collector(raf).saveData(savepath);
	}
	
	public void writeMetaData(Writer bw) throws IOException{
		if (raf == null) {
			return;
		}
		new metadata_Collector(raf).writeData(bw);
	}
	/**
	 * Does nothing
	 */
	public void addSlice(String name){
		return;
	}
	
	/**
	 * Does nothing
	 */
	public void deleteLastSlice(){
		return;
	}
	
	/**
	 * Does nothing
	 */
	public void deleteSlice(int n){
		return;
	}
	
	public int getBitDepth(){
		if (depth == -1){
			depth = currentStack.backgroundIm.getBitDepth();
		}
		return depth;
	}

	public String getDirectory(){
		return fileDir;
	}
	
	/**
	 * Returns the file name (not including the directory)
	 */
	public String getFileName(){
		return fileName;
	}

	//Returns the ImageProcessor for the specified frame number
	//	Overrides the method in ImageStack
	//	Ensures that the frame is in the current mmfStack, and then gets the image through CommonBackgroundStack methods

	public ImageProcessor getProcessor (int frameNumber) {
		
		frameNumber -= 1; //imageJ starts at frame 1, instead of at frame 0; mmf is 0-indexed
		
		if(frameNumber<0 || frameNumber>=raf.getNumFrames()){
			return null; 
		}
		//check if current stack has frame
		//if not update current stack from mmf file
		if (!currentStack.containsFrame(frameNumber)) {
			currentStack = raf.getStackForFrame(frameNumber);
		}

		//then get specific frame
		if (currentStack == null){
			return null;
		}
		setImagePlusMetadata(currentStack.getImageMetaData(frameNumber));
		return currentStack.getImage(frameNumber);
	}
	
	@SuppressWarnings("unused")
	private void logImagePlusMetadata(Map<String,Object> m) {
		if (m == null) {
			return;
		}
		for (Map.Entry<String, Object> entry : m.entrySet())
		{
			IJ.log(entry.getKey() + ": " + entry.getValue().toString());
//		    imp.setProperty(entry.getKey(), entry.getValue());
		}
	}
	
	private void setImagePlusMetadata(Map<String,Object> m) {
		if (m == null || imp == null) {
			return;
		}
		for (Map.Entry<String, Object> entry : m.entrySet())
		{
		    imp.setProperty(entry.getKey(), entry.getValue());
		}
	}
	
	public int getSize() {
		return raf.getNumFrames();
	}
	
	
	public String getSliceLabel(int n){
		String label = "MMF_Frame_"+n;
		return label;
	}
	
	/**
	 * Does nothing
	 */
	public void setPixels(){
		return;
	}

	public ImagePlus getImagePlus() {
		return imp;
	}

	public void setImagePlus(ImagePlus imp) {
		this.imp = imp;
	}
	
	
	
}
