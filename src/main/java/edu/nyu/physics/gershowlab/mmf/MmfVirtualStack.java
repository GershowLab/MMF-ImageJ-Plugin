package edu.nyu.physics.gershowlab.mmf;

import java.awt.*;
import java.io.*;
import java.util.*;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class MmfVirtualStack extends VirtualStack {


	private String			fileName;
	private String			fileDir;
	private MmfFile 		raf;					//the mmf file
	private int 			depth;
	
	private CommonBackgroundStack currentStack;
	
	
	//TODO Constructors
	//initialize depth=-1
	public MmfVirtualStack(String path, String fileName, String fileDir){
		this.fileName = fileName;
		this.fileDir  = fileDir;
		depth = -1;
		try{	
			raf = new MmfFile(path, "r");
			raf.parse();			
			IJ.showMessage("MmfVirtualStack", raf.getReport());
		} catch(Exception e){
			IJ.showMessage("MmfVirtualStack","Opening of: \n \n"+path+"\n \n was unsuccessful.\n\n Error: " +e);
			return;
		}
		
		currentStack = raf.getStackForFrame(1);
		
	}
	
	public boolean fileIsNull(){
		//Check that the file isn't null
		if (raf == null || raf.getNumFrames()==0 || getProcessor(1)==null){
			IJ.showMessage("MmfVirtualStack","Error: Frames missing or empty");
			return true;
		}
		return false;
	}
	
	public void addSlice(String name){
		return;
	}
	
	public void deleteLastSlice(){
		return;
	}
	
	public void deleteSlice(int n){
		return;
	}
	
	public int getBitDepth(){
		if (depth == -1){
			depth = currentStack.backgroundIm.getBitDepth();
		}
		return depth;
	}
	
	public String getDriectory(){
		return fileDir;
	}
	
	public String getFileName(){
		return fileName;
	}

	//Returns the ImageProcessor for the specified frame number
	//	Overrides the method in ImageStack
	//	Ensures that the frame is in the current mmfStack, and then gets the image through CommonBackgroundStack methods
	public ImageProcessor getProcessor (int frameNumber) {
		
		if(frameNumber<0 || frameNumber>raf.getNumFrames()){
			//IJ.showMessage("MmfVirtualStack","Frame Index Error; mmf_Reader");
			return null; 
		}
		//check if current stack has frame
		//if not update current stack from mmf file
		if (!currentStack.containsFrame(frameNumber)) {
			currentStack = raf.getStackForFrame(frameNumber);
		}
		//if(frameNumber<currentStack.getStartFrame() || frameNumber>currentStack.getLastFrame()){
			//currentStack = raf.getStackForFrame(frameNumber);
		//}
		//then get specific frame
		if (currentStack == null){
			return null;
		}
		return currentStack.getImage(frameNumber);
	}
			
	public int getSize() {
		return raf.getNumFrames();
	}
	
	
	public String getSliceLabel(int n){
		String label = "MMF_Frame_"+n;
		return label;
	}
	
	public void setPixels(){
		return;
	}
	
	
	
}
