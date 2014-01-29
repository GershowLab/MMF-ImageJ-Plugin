/* 
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
package edu.nyu.physics.gershowlab.mmf;


import java.io.*;


import ij.*;
import ij.io.*;
import ij.plugin.*;

public class mmf_Reader extends VirtualStack implements PlugIn {

	/*
	 * The virtual stack containing the MMF movie frames
	 */
	private MmfVirtualStack vStack;
	
	/*
	 * The full path of the MMF file
	 */
	private String			path;
	/*
	 * The name of the MMF file
	 */
	private String			fileName;
	/*
	 * The directory of the MMF file
	 */
	private String			fileDir;	
	/*
	 * The ImagePlus containing the MMF movie
	 */
	private	ImagePlus		imp;
	

	
	
	/////////////////////////////
	// Plugin code begins here //
	/////////////////////////////
	
	

	public void run(String arg) {
		
		getPath(arg);
		if (null == path) {
			//This is used when the dialog is cancelled
			return;
		}
		
		
		//Construct virtual stack
		try{
			vStack = new MmfVirtualStack(path, fileName, fileDir);
		} catch(Exception e){
			IJ.showMessage("mmfReader","Virtual stack construction was unsuccessful.\n\n Error: " +e);
			return;
		}

		//Check that the file isn't null
		if (vStack.fileIsNull()){
			return;
		}

			
		//Make the ImagePlus and add the FileInfo
		imp = new ImagePlus(WindowManager.makeUniqueName(fileName), vStack);
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
	/*
	 * Opens a dialogue to obtain the MMF file path. Also sets parameters fileName, fileDir, and path.
	 * 
	 * @return 	A String containing the full path
	 */
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
	
	

	/*
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
