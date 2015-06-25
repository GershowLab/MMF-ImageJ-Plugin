package edu.nyu.physics.gershowlab.mmf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import ij.IJ;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

public class metadata_Collector implements PlugIn{

	MmfFile f;
	CommonBackgroundStack cbs;
	
	private HashMap<String, Vector<Entry>> data;
	
	private boolean showData = false;
	
	public void run(String arg0) {
		
		String path = getPath(arg0);
		if (null == path) {
			//This is used when the dialog is cancelled
			return;
		}
		
		init(path);
		
		fillMap();
		
		if (showData){
			showData();
		}
		
		
		saveData("");
	}
	
	public metadata_Collector(String path){
		
		showData = false;//Hides the data when this is called in code
		run(path);
		
	}
	
	
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
		return dir+od.getFileName();
	}
	
	private void init(String path){
		
		//Construct the file f
		try{
			f = new MmfFile(path, "r");
		} catch (Exception e){
			IJ.showMessage("Error opening file at path:\n"+path);
		}
		
		
	}
	
	private void fillMap(){
		//Iterate through the frames in the MmfFile and get the metadata
		int s = cbs.getStartFrame();
		int e = cbs.getLastFrame();
		int i=0; //ITERATE OVER I
		
			if ( i>e || i<s){
				f.getStackForFrame(i);
				s = cbs.getStartFrame();
				e = cbs.getLastFrame();
			}
		
			Map<String, Object> m = cbs.getImageMetaData(i);
			m.keySet().
			
	}

	
	
	private void showData(){
		
	}
	
	
	private void saveData(String savepath){
		
		if (savepath.equals("")){
			//Generate new name
		}
		
		
		//Save data to a csv 
		
	}
	
	
}
