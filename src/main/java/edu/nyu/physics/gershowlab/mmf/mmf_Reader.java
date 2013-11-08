/* mmf_Reader
 * reads mmf files into imagej
 * will eventually have a better header
 * 
 * (c) 2013 Natalie Bernat and Marc Gershow
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

public class mmf_Reader implements PlugIn {

	//Used to print debugging messages 
	//  Multiple numbers can be used for different kinds/levels of debug feedback
	private int debug = 1;
	
	
	
	
	
	private MmfFile raf;					//the mmf file
	
	
	

	
	
	/////////////////////////////
	// Plugin code begins here //
	/////////////////////////////
	
	public void run(String arg) {
		
		String path = getPath(arg);
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
		
		
		//make the reference table 
		
		//read each stack
		
		//if (debug>0){
	//		IJ.showMessage("mmfReader","Path Name:\n"+path);
	//	}
		
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
		return dir + od.getFileName();
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

        // open the Clown sample
       // ImagePlus image = IJ.openImage("http://imagej.net/images/clown.jpg");
       // image.show();

        // run the plugin
        IJ.runPlugIn(clazz.getName(), "");
}
}
