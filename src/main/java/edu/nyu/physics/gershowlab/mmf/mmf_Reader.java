
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

import java.io.*;


import ij.*;
import ij.io.*;
import ij.plugin.*;

/**
 * 
 *  An ImageJ plugin which converts an MMF file into an ImagePlus and plays the MMF movie. 
 *  ImageJ is opened upon running the reader, and a dialog is opened to choose the MMF file. The movie is stored as a Virtual stack.
 *  @author Natalie Bernat
 *  @author Marc Gershow
 *  @version 1.0
 *  @see ImagePlus
 *  @see MmfVirtualStack
 *  
 */
public class mmf_Reader /*extends VirtualStack*/ implements PlugIn {

	/**
	 * The virtual stack containing the MMF movie frames
	 */
	private MmfVirtualStack vStack = null;
	
	/**
	 * The full path of the MMF file
	 */
	private String			path;
	/**
	 * The name of the MMF file
	 */
	private String			fileName;
	/**
	 * The directory of the MMF file
	 */
	private String			fileDir;	
	/**
	 * The ImagePlus containing the MMF movie
	 */
	private	ImagePlus		imp;
	

	
	
	/////////////////////////////
	// Plugin code begins here //
	/////////////////////////////
	public mmf_Reader() {
		
	}
	
	public mmf_Reader (String arg) {
		path = getPath(arg);
		loadStack();
	}
	
	private void loadStack() {
		//path = arg;
		if (null == path) {
			//This is used when the dialog is cancelled
			return;
		}
		//Construct virtual stack
		openStack(path);
		//Check that the file isn't null
		if (vStack == null || vStack.fileIsNull()){
			System.out.println("MMF file null");
			return;
		}
	}
	
	public MmfVirtualStack getMmfStack(){
		return vStack;
	}

	private void openStack(String path) {
		try{
			vStack = new MmfVirtualStack(path);
			fileName = vStack.getFileName();
			fileDir = vStack.getDirectory();
		} catch(Exception e){
			System.out.println("Virtual stack construction was unsuccessful.\n\n Error: " +e);
			IJ.showMessage("mmfReader","Virtual stack construction was unsuccessful.\n\n Error: " +e);
			return;
		}
	}
	public void run(String arg) {
		
		path = getPath(arg);
		loadStack();
		//Check that the file isn't null
		if (null == vStack || vStack.fileIsNull()){
			return;
		}
	
		//Make the ImagePlus and add the FileInfo
		imp = new ImagePlus(WindowManager.makeUniqueName(fileName), vStack);
		vStack.setImagePlus(imp);
		imp.getCalibration().fps = 10;
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
	/**
	 * Opens a dialogue to obtain the MMF file path. Also sets parameters fileName, fileDir, and path.
	 * 
	 * @return 	A String containing the full path
	 */
	private String getPath(String arg) {
		if (null != arg) {
			File f = new File(arg);
			if (f.exists()){ 
				fileName=f.getName();
				fileDir=f.getParent();
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
		path = new File(fileDir, fileName).getPath();
		return path;
	}
	public void saveMetaData(String savepath){	
		if (vStack == null) {
			return;
		}
		vStack.saveMetaData(savepath);
	}
	
	public void writeMetaData(Writer bw) throws IOException{
		if (vStack == null) {
			return;
		}
		vStack.writeMetaData(bw);
	}
	

	/**
	 * Opens ImageJ and plays the MMF movie
	 */
	public static void main(String[] args) {
        // set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = mmf_Reader.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);

        // start ImageJ
        new ImageJ();


        // run the plugin
        IJ.runPlugIn(clazz.getName(), "");
}
}
