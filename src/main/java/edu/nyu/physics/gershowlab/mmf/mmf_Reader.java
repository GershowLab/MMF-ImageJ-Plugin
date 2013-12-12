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
 *  -Added run() code
 *  
 *  ~Update 12/8/13
 *  -The ImageStack vStack is always Null; the MMF file system is used in is place
 *  -Added getProcessor() method
 *  -Currently implements the getProcessor and getSize methods of the Virtual Stack
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
		
		currentStack = raf.getStackForFrame(1);
		
		//Check that the file isn't null
		if (raf.getNumFrames()==0 || getProcessor(1)==null){
			IJ.showMessage("mmfReader","Error: Frames missing or empty");
			return;
		}
			
		//Make the ImagePlus and add the FileInfo
		imp = new ImagePlus(WindowManager.makeUniqueName(fileName), this);
		FileInfo fi = new FileInfo();
		fi.fileName = fileName;
		fi.directory = fileDir;
		imp.setFileInfo(fi);
		
		//Play the movie
		imp.show("Playing MMF: "+ fileName);
		
		
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
	//	Ensures that the frame is in the current mmfStack, and then gets the image through CommonBackgroundStack methods
	public ImageProcessor getProcessor (int frameNumber) {
		
		if(frameNumber<0 || frameNumber>raf.getNumFrames()){
			IJ.showMessage("mmfReader","Frame Index Error; mmf_Reader");
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
