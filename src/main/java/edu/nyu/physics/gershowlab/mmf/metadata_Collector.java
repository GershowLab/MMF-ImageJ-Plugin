package edu.nyu.physics.gershowlab.mmf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Vector;

import ij.IJ;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;

public class metadata_Collector implements PlugIn{

	String path;
	MmfFile f;
	CommonBackgroundStack cbs;
	String dstDir;
	
	/**
	 * A mapping from a data type (String name) to a list of {int framenum, Object value} pairs 
	 */
	private HashMap<String, Vector<WritableMdatPair>> data;
	private int lastFrame = -1;
	
	
	private boolean showData = false;
	
	public static void main(String[] args){
		
//		String path = "C:\\Users\\Natalie\\Documents\\TestJavaMat\\data\\phototaxis\\berlin@berlin\\LIGHT_RANDOM_WALK_S1_112Hz\\201402121840\\berlin@berlin_LIGHT_RANDOM_WALK_S1_112Hz_201402121840.mmf";
		
		metadata_Collector mdc = new metadata_Collector(); 
		
		if (args!=null && args.length>=1){
			
			if (args.length>=2){
				mdc.dstDir = args[1];
			}
			
			mdc.run(args[0]);
		}

		
		
		
	}
	
	public void run(String arg0) {
		
		path = getPath(arg0);
		if (null == path) {
			//This is used when the dialog is cancelled
			return;
		}
		
		init(path);
		
		fillMap();
		
		if (showData){
			showData();
		}
		
		saveData(getSavePath());
	}
	
	public metadata_Collector(){
		
//		run(path);
		
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
		lastFrame = f.getNumFrames()-1;
		data = new HashMap<String, Vector<WritableMdatPair>>();
	}
	
	private void fillMap(){
		
		//Initialize commonBackgroudStack & relevant data
		cbs = f.getStackForFrame(0);
		if (cbs==null){
			System.out.println("FillMap Error: First stack wasn't loaded.");
			return;
		}
		int end = cbs.getLastFrame();

		//Iterate through the frames in the MmfFile and get the metadata
		for (int fnum=0; cbs!=null; fnum++){
			
			//Load the metadata from the stack
			HashMap<String, Object> m = cbs.getImageMetaData(fnum);
			for (String key: m.keySet()){
				if (data.get(key)==null){
					data.put(key, new Vector<WritableMdatPair>());
				}
				data.get(key).add(new WritableMdatPair(fnum, m.get(key)));
			}
			
			
			//Load the next cbs when current one is exhausted
			if (fnum==end){
				if (fnum==lastFrame){
					cbs = null;
				}else{
					//System.out.println("Loading stack for frame "+(fnum+1));
					try{
						cbs = f.getStackForFrame(fnum+1);
					} catch(Exception ex){
						System.out.println("Error getting stack for frame "+(fnum+1));
						cbs = null;
					}
					if (cbs!=null) end = cbs.getLastFrame();
				}
			}
		}
	}

	
	
	private void showData(){
		System.out.println("Showing data...");
		try{
//			writeData(new PrintWriter(System.out));
			writeData(new PrintWriter("C:\\Users\\Natalie\\Documents\\TestExProc\\metadataTest.dat"));
		} catch(Exception e){
			System.out.println("Error showing data...");
		}
		
		System.out.println("Done showing data");
	}
	
	private String getSavePath(){
		
		
		if (dstDir==null || dstDir.equals("")){
			//Generate new name "*.mdat"
			return path.replace(".mmf", ".mdat");
		} else{

//			Path p = Paths.get(path.replace(".mmf", ".mdat"));
//			return new File(dstDir, p.getFileName().toString()).getAbsolutePath();
			String name = path.substring(path.lastIndexOf(System.getProperty("file.separator")), path.length());
			return new File(dstDir, name).getAbsolutePath();
		}
		
	}
	
	
	private void saveData(String savepath){
		
		
		try{
			
			FileWriter fw = new FileWriter(savepath);
			BufferedWriter bw = new BufferedWriter(fw);
			
			writeData(bw);
			
			fw.close();
			bw.close();
			
		} catch (Exception e){
			return;
		}
	}
	
	private void writeData(Writer bw) throws IOException{
		
		String DELIMITER = ",";
		String NEWLINE = System.getProperty("line.separator");//.lineSeparator();
		
		//	row 1   = keys (1st key = 'framenum')
		bw.append("frameNum");
		for (String key: data.keySet()){
			bw.append(DELIMITER+key);
		}
		bw.append(NEWLINE);
		
		//  row 2-N = values (or null values) (1st value = frame#)
		String[] keySet = new String[data.keySet().size()];
		keySet = data.keySet().toArray(keySet);
		int[] index = new int[data.keySet().size()];//values initialized to 0
		for (int fnum=0; fnum<lastFrame; fnum++){
			bw.append(""+fnum);
			for (int k=0; k<keySet.length; k++){
				bw.append(DELIMITER);
				
				String key = keySet[k];
				WritableMdatPair pair=null;
				if (index[k]<data.get(key).size()) pair=data.get(key).get(index[k]);//Get the 1st unwritten pair from this list
				if (pair!=null && pair.getFrameNum()==fnum){//If the value belongs on this frame's row, write it
					bw.append(pair.getValue().toString());
					index[k]++;//Mark this val as written
				} else {
					bw.append("NaN");
				}
			}
			bw.append(NEWLINE);
		}
		
		
	}
	
	
}






