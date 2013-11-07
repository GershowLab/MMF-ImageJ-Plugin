/* mmf_Reader
 * reads mmf files into imagej
 * will eventually have a better header
 * 
 * (c) 2013 Natalie Bernat and Marc Gershow
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see {http://www.gnu.org/licenses/}.
 * 
 * 
 */

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
	
	//Constants:
	private final int HEADERSIZE=10240;        		//mmf header size
	private final int SHEADERSIZE=512;         		//stack header size
	private final int FHEADERSIZE=10240;       		//frame header size
	
	
	
	private RandomAccessFile raf;					//the mmf file
	private long FILELENGTH;						//file length, computed/read when the file is opened
	
	
	//Reference tables
	//  Index refers to stack number
	private ArrayList<Integer> frameStart;				//# of the first frame in a stack
	private ArrayList<Long> framePos;				//location (in bytes) of the first frame in  stack
	
	
	/*
	private int sLen(int stackIndex){				//length (in bytes) of image stack 
		//if(()||()){
		//	
		//}
		//else 
		if (stackIndex == (sRefTable.length-1) ){
			return FILELENGTH;
		}
		else{
			return (sRefTable[stackIndex+1]-sRefTable[stackIndex]);
		}
		
	}
	*/

	
	
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
			if (!parse(path)) {
				//If the file was not read properly for any reason, inform the user
				IJ.showMessage("mmfReader","Opening of: \n \n"+path+"\n \n was unsuccessful.");
				return;
			}
		} catch(Exception e){
			IJ.showMessage("Error: ", ""+e);
			return;
		}
		
		
		//make the reference table 
		
		//read each stack
		
		if (debug>0){
			IJ.showMessage("mmfReader","Path Name:\n"+path);
		}
		
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
	private boolean parse(String path) throws IOException {
		
		IJ.showStatus("Reading mmf Images...");
		
		//Open the file as a RandomAccessFile here, take out file opening in processHeader()
		raf = new RandomAccessFile(path,"r");
		FILELENGTH = raf.length(); 
		
		//Process the header
		if(!processHeader(path)){
			return false;
		}
		
		if (makeFrameTables()<0){
			return false;
		}
		
		
		raf.close();
		return true;
	}
	
	
	//Makes a FileInputStream out of the pathname
	//  Helper method for processHeader()
	//  Opens URLs as well.
	private InputStream open(String path) throws Exception {
		if (0 == path.indexOf("http://"))
			return new java.net.URL(path).openStream();
		return new FileInputStream(path);
	}
	
	//Reads a LE integer, returns an int
	//  Helper method throughout code
	private final int readIntLittleEndian(byte[] buf, int start) {
		return (buf[start]) + (buf[start+1]<<8) + (buf[start+2]<<16) + (buf[start+3]<<24);
	}
	
	//Reads a LE integer from the RAF starting at the currest file pointer, returns an int
	//  Helper method throughout code
	//UNSIGNED INT
	private final int readUnsignedIntLittleEndian() throws IOException {
		
		byte[] buf = new byte[4];
		raf.read(buf); //reads 4 bytes into buf
		
		return (buf[0]) + (buf[1]<<8) + (buf[2]<<16) + (buf[3]<<24);
	}
	private final int convertBytesToLittleEndian(byte[] buf, StringBuilder strb) {
		int ans;
		byte msb = buf[(buf.length)-1];
		int sb = (msb&(1<<7)) == 0 ? 1:-1;
		ans = msb & (byte) ~(1<<7);
		//StringBuilder strb = new StringBuilder();
		strb.append(String.format("%02X ", msb));
		strb.append(ans);
		for (int j = buf.length - 2; j >= 0; --j){
			strb.append(String.format("  ans<<8 = %02X = %d\n", ans<<8, ans<<8));
			ans = (ans << 8) + (buf[j]&0xFF);
			strb.append(String.format("%02X ", buf[j]));
			strb.append(ans);
			strb.append("\n");
		}
	//	if (disp) {
		//	IJ.showMessage("mmfReader", strb.toString());
		//}
		return ans*sb;
	}
	
	private final int convertBytesToLittleEndian(byte[] buf) {
		int ans;
		byte msb = buf[(buf.length)-1];
		int sb = (msb&(1<<7)) == 0 ? 1:-1;
		ans = msb & (byte) ~(1<<7);
		for (int j = buf.length - 2; j >= 0; --j){
			ans = (ans << 8) + buf[j];
		}
		return ans*sb;
	}
	private final int readIntLittleEndian() throws IOException {
		
		byte[] buf = new byte[4];
		raf.read(buf); //reads 4 bytes into buf
		int msb = buf[3]&((byte)(1<<7));
	//	if(buf[1]==0){
		//	buf[1]+=(byte)1;
	//	}
		//buf[1] &= ~((byte)(1<<7));
		return ( (buf[0]) + (buf[1]<<8) + (buf[2]<<16) + (buf[3]<<24));
		//return ((msb == 0) ? 1 : -1) * ((buf[0]) + (buf[1]<<8) + (buf[2]<<16) + (buf[3]<<24));
	}
	
	
	//Pulls the header of the file into a buffer, then extracts info and displays the header text
	//  Helper method for parse()
	private boolean processHeader(String path){
		//Pull in the header

		byte[] buf = new byte[HEADERSIZE];
		try {
			raf.read(buf, 0, HEADERSIZE);
		} catch (Exception e) {
			e.printStackTrace();
			if(debug>0){
				IJ.showMessage("mmfReader Debugging","Error reading header data into buffer.");
			}
			return false;
		}

		
		//Count the number of bytes of text
		int headerTextLen = 0;
		while ( (byte)buf[headerTextLen] != (byte)0 ){ 		
			//count the byte
			headerTextLen++;
		}
			
		//Convert the subarray of bytes containing text into a String
		String headerText = new String(buf, 0, headerTextLen);
		
		//Read the id code and header size from the header
		int idcode = readIntLittleEndian(buf, headerTextLen+1);
		int readHeaderSize = readIntLittleEndian(buf, headerTextLen+5);
		
		IJ.showMessage("mmfReader","id code : "+idcode+"\n \n header size: "+readHeaderSize+"\n \nheader text : \n"+headerText);
		
		return true;
	}
	private int readOneStackSize(int startFrameNumber) throws IOException {
		long fp = raf.getFilePointer();
		
		int stackSize=-1;
		int NFRAMES = -1;
		int headerSize = -1;
		
		try {
			byte[] headerbytes = new byte[16];
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<=15; i++){
				headerbytes[i]=raf.readByte();
				sb.append(String.format("%02X ", headerbytes[i]));
			}
			sb.append("\n");
			for (int i=0; i<=3; i++){//for each int in the header
				byte[] headerint = new byte[4];
				for (int j=0; j<=3; j++){
					headerint[j]=headerbytes[4*i+j];//fill a temporary byte buffer with the 4 bytes
				}
				
				int le = convertBytesToLittleEndian(headerint, sb);		
				//sb.append("0x31 << 8 = "); sb.append(0x31 << 8); sb.append("\n");
				sb.append(String.format("%02X %02X %02X %02X: ", headerint[0], headerint[1], headerint[2], headerint[3]));
				sb.append(String.format("%d \n", le));
			}
			if (debug>0){
				IJ.showMessage("mmfReader",sb.toString());
				/*
				IJ.showMessage("mmfReader","ID Code: "+idCode+"\n \n Header Size: "+headerSize+"\n \nStack Size: "+stackSize+"\n \nNFRAMES: "+NFRAMES
						+"\n \n Start Pos: "+fp);
				*/
			}
			
			/*
			int idCode = raf.readInt(); 							//Skip the id code (for now)
			headerSize = readIntLittleEndian();	//Header size (for debugging)
			stackSize = readIntLittleEndian();	//Size of the stack (in bytes)
			NFRAMES = readIntLittleEndian();	//Number of frames in this stack
			if (debug>0){
				IJ.showMessage("mmfReader","ID Code: "+idCode+"\n \n Header Size: "+headerSize+"\n \nStack Size: "+stackSize+"\n \nNFRAMES: "+NFRAMES
						+"\n \n Start Pos: "+fp);
			}
			*/
		} catch (Exception e){
			IJ.showMessage ("mmfReader", e.getMessage());
			return -1;
		}
		
		if (/*everything is ok*/ true) {
			frameStart.add(startFrameNumber);
			framePos.add(raf.getFilePointer());
			try {
				raf.seek(fp + stackSize);
			} catch (Exception e) {
				return -1;
			}
		}
		if (raf.getFilePointer() >= FILELENGTH) {
			return -1;
		}
		return startFrameNumber + NFRAMES;
		
		
	}
	
	private int makeFrameTables() throws IOException {
		
		int nStacks = 0;
		int firstFrame = 0;
		
		frameStart = new ArrayList<Integer>();		//REASONABLE STARTING SIZE?? ~512
		framePos = new ArrayList<Long>();
		
		raf.seek(HEADERSIZE);
		
		while (firstFrame >= 0) {
			firstFrame = readOneStackSize(firstFrame);
			nStacks++;
		}
		/*
		//for (nStacks=0; raf.getFilePointer()<FILELENGTH; nStacks++){
		//for (nStacks=0; nStacks<2; ++nStacks){
			//Read in the stack size and NFRAMES
			raf.readInt(); 							//Skip the id code (for now)
			int headerSize = readIntLittleEndian();	//Header size (for debugging)
			int stackSize = readIntLittleEndian();	//Size of the stack (in bytes)
			int NFRAMES = readIntLittleEndian();	//Number of frames in this stack
			
			if (debug>0){
				IJ.showMessage("mmfReader","Header Size: "+headerSize+"\n \nStack Size: "+stackSize+"\n \nNFRAMES: "+NFRAMES
						+"\n \nCurrent Pos: "+raf.getFilePointer());
			}
			
			
			frameStart.add(firstFrame);				
			raf.skipBytes((SHEADERSIZE-16));			//16 bytes havve been read; "skip" over the header
			framePos.add(raf.getFilePointer()); 
			
			if (debug>0){
				IJ.showMessage("mmfReader","First Frame: "+frameStart.get(nStacks)+"\n \nFirst Frame Position: "
						+framePos.get(nStacks)+"\n \nCurrent Pos: "+raf.getFilePointer());
			}
			
			//Set the file pointer to the next stack
			raf.seek(framePos.get(nStacks)+stackSize);
			//raf.skipBytes(stackSize-headerSize);
			firstFrame += NFRAMES;
			
		//}
		*/
		
		
		return nStacks;
	}
	
	
	
	/*
	//Pulls stack into a buffer,  
	
	private boolean readStack(String path, int stackLen){
		
		//determine where to start the 
		int sStart = 
		
		//Pull in the header
		byte[] buf = new byte[SHEADERSIZE];
		try {
			InputStream stream = open(path);
			stream.read(buf, sStart, sHeaderSize);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
			if(debug>0){
				IJ.showMessage("mmfReader Debugging","Error reading stack header input stream into buffer.");
			}
			return false;
		}
		
		
		//int sidcode = readIntLittleEndian(buf, headerTextLen+1);
		
		
		return true;
				
	}
	*/
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
