/* mmf_Reader
 * 
 * (c) 2013 Natalie Bernat and Marc Gershow

 *  File reader for the MMF movie format through ImageJ.
 *  
 *  
 *   Opens a dialog to choose an MMF. Opens the specified file as an MmfFile, 
 *   and reads the image frames into a VirtualStack. The virtual stack is used to 
 *   create an ImagePlus, which is opened by ImageJ. 
 *   
 *  
 *  ~Update 12/6/13~
 *  -Wrote a descriptive header
 *  -Reader now extends VirtualStack
 *  -Added run() code to convert file to Virtual Stack
 *  -Added unimplemented makeVirtStackFromMMF() and getProcessor() methods
 *  
 *  
 *  
 *  
 */
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

public class mmf_Reader extends VirtualStack implements PlugIn {

	//Used to print debugging messages 
	//  Multiple numbers can be used for different kinds/levels of debug feedback
	private int debug = 1;
	
	private String			path;
	private String			fileName;
	private String			fileDir;
	private MmfFile 		raf;					//the mmf file
	private	ImageStack		vStack;
	private	ImagePlus		imp;
	
		
	private CommonBackgroundStack currentStack;
	
	
	/////////////////////////////
	// Plugin code begins here //
	/////////////////////////////
	
	public void run(String arg) {
		
		getPath(arg);
		if (null == path) {
			//This is used when the dialog is cancelled
			return;
		}
		try{	
			raf = new MmfFile(path, "r");
			raf.parse();
			IJ.showMessage("mmfReader", raf.getReport());
		} catch(Exception e){
			IJ.showMessage("mmfReader","Opening of: \n \n"+path+"\n \n was unsuccessful.\n\n Error: " +e);
			return;
		}
		
		//Create the Virtual Stack
		try{	
			//TODO put correct arguments when this is written; also correct the variables two blocks down
			vStack = makeStackFromMMF(path, firstFrame, lastFrame, isVirtual, convertToGray, flipVertical);
		} catch(Exception e){
			IJ.showMessage("mmfReader","Error on creation of Virtual Stack.\n\n Error: " +e);
			return;
		}
		
		//Check that the vStack was made
		if (vStack==null || (vStack.isVirtual()&&vStack.getProcessor(1)==null))
			return;
		
		//If the file is empty, provide some informative notes:
		if (vStack.getSize() == 0) {
			String rangeText = "";
			if (firstFrame>1 || lastFrame!=0)
				rangeText = "\nin Range "+firstFrame+
					(lastFrame>0 ? " - "+lastFrame : " - end");
			error("Error: No Frames Found"+rangeText);
			return;
		}
		
		//Make the ImagePlus from the stack, then add the FileInfo
		imp = new ImagePlus(WindowManager.makeUniqueName(fileName), vStack);
		/*
		if (imp.getBitDepth()==16)
			imp.getProcessor().resetMinAndMax();
		setFramesPerSecond(imp);
		*/
		FileInfo fi = new FileInfo();
		fi.fileName = fileName;
		fi.directory = fileDir;
		imp.setFileInfo(fi);
		
		
		//Play the movie
		imp.show("Playing MMF: "+ fileName);
		
		
		//make the reference table (done in MmfFile: parse() ) 
		//read each stack
		
		
		//Take stack and convert it to imageProcessor
		
		
		//if (debug>0){
	//		IJ.showMessage("mmfReader","Path Name:\n"+path);
	//	}
		
	}
	
	//Creates a Virtual Stack using the MmfFile
	private ImageStack makeVirtStackFromMMF(path, firstFrame, lastFrame, isVirtual, convertToGray, flipVertical){
		//TODO
		//See AVI_Reader: makeStack & readAVI
	}
	
	
	
	
	//Checks that the file exists, or opens a dialog for the user to choose a file. 
	//  Returns the path name
	//  Helper method for run()
	private String getPath(String arg) {
		if (null != arg) {
			if (0 == arg.indexOf("http://") || new File(arg).exists()){ 
				return arg;
			}
		}
		
		// else, ask:
		OpenDialog od = new OpenDialog("Choose a .mmf file", null);
		String dir = od.getDirectory();
		if (null == dir) return null; // dialog was canceled
		dir = dir.replace('\\', '/'); // Windows safe
		if (!dir.endsWith("/")) dir += "/";
		fileName = od.getFileName();
		fileDir = dir;
		path = fileDir + fileName;
		return path;
	}
	
	//Returns the ImageProcessor for the specified frame number
	//	Overrides the method in ImageStack
	//	Ensures that the frame is in the current mmfStack, and then 
	public ImageProcessor getProcessor (int frameNumber) {
		//TODO
		//check if current stack has frame
		//if not update current stack from mmf file
		if(frameNumber<currentStack.getFirstFrame() || frameNumber>currentStack.getLastFrame()){
			currentStack = raf.getStackForFrame(frameNumber);
		}
		//then get specific frame
		return currentStack.getImage(frameNumber);
		
		
	}
	
	
	
	//Main file reader:
	//		~Processes header
	//		~Makes the frame tables
	
	public static void main(String[] args) {
        // set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = mmf_Reader.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);

        // start ImageJ
        new ImageJ();

       // open the (incredibly creepy) Clown sample
       // ImagePlus image = IJ.openImage("http://imagej.net/images/clown.jpg");
       // image.show();

        // run the plugin
        IJ.runPlugIn(clazz.getName(), "");
}
}
