package edu.nyu.physics.gershowlab.mmf;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import ij.io.FileInfo;
import ij.io.ImageReader;
import ij.process.ImageProcessor;
import ucar.unidata.io.RandomAccessFile;


public class MmfFile extends RandomAccessFile {

	private MmfHeader header;
	private ArrayList<ImageStackLocator> stackLocations;
	private boolean parsed = false;
	private FileInfo backgroundFileInfo = null;
	
	public int getNumStacks() {
		return stackLocations.size();
	}
	
	public MmfHeader getHeader() {
		if (!parsed) {
			try {
				parse();
			} catch (IOException e) {
				return null;
			}
		}
		return header;
	}
	
	public int getNumFrames(){
		if (!parsed) {
			try {
				parse();
			} catch (IOException e) {
				return -1;
			}
		}
		return stackLocations.get(stackLocations.size()-1).getLastFrame() - stackLocations.get(0).getStartFrame() + 1;
	}
	
	public String getReport() {
		StringBuilder sb = new StringBuilder();
		if (!parsed) {
			return "Not Parsed";
		}
		sb.append(header.headerText + "\n\n");
		try {
			sb.append("total stacks = " + getNumStacks() + ";  total frames = " + getNumFrames() + ";  file size = " + length() + "\n");
		} catch (IOException e) {
			sb.append("file error: " + e);
			return sb.toString();
		}
		int j = 0;
		for (ImageStackLocator i : stackLocations) {
			sb.append(String.format("stack num: %d,  frames %d-%d, locInFile = %d\n", j++, i.getStartFrame(), i.getLastFrame(), i.filePosition));
		}
		return sb.toString();
	}
	
	public void parse() throws IOException {
		stackLocations = new ArrayList<ImageStackLocator>();
		seek(0);
		header = readFileHeader();
		int frame = 0;
		while (!isAtEndOfFile()) {
			ImageStackHeader h = readImageStackHeader();
			stackLocations.add(new ImageStackLocator(h, frame));
			frame += h.nframes;
			long nextpos;
			if ((nextpos = h.filePosition + h.stackSize) < length()) {
				seek(nextpos);
			} else {
				break;
			}
		}
		parsed = true;
	}
	
	
	
	public MmfFile(String location, String mode, int bufferSize)
			throws IOException {
		super(location, mode, bufferSize);
		order (LITTLE_ENDIAN);
		// TODO Auto-generated constructor stub
	}

	public MmfFile(int bufferSize) {
		super(bufferSize);
		order (LITTLE_ENDIAN);
		// TODO Auto-generated constructor stub
	}

	public MmfFile(String location, String mode) throws IOException {
		super(location, mode);
		order (LITTLE_ENDIAN);
		// TODO Auto-generated constructor stub
	}

	/* public MmfHeader readFileHeader() throws IOException
	 * reads the file header (usually at the beginning of the file) 
	 * at end of function, file pointer should be positioned at first byte after file header
	 */
	public MmfHeader readFileHeader() throws IOException{
		long pos = getFilePointer();
		StringBuilder headerText = new StringBuilder();
		char c;
		while ((c = (char) readUnsignedByte()) != 0) {
			headerText.append(c);
		}
		long idcode = readInt() & 0x00000000ffffffffL;
		int headerSizeInBytes = readInt();
		int keyFrameInterval= readInt();
		int threshAboveBackground = readInt();
		int threshBelowBackground = readInt();
		seek (pos + headerSizeInBytes);
		return new MmfHeader(headerText.toString(), idcode, headerSizeInBytes, keyFrameInterval, threshAboveBackground, threshBelowBackground);
	}
	
	
	
	public ImageStackHeader readImageStackHeader() throws IOException {
		long pos = getFilePointer();
		long idCode = readInt() & 0x00000000ffffffffL;
		int headerSize = readInt();
		int stackSize = readInt();
		int nframes = readInt();
		
		seek (pos + headerSize);	
		
		return new ImageStackHeader(idCode, headerSize, stackSize, nframes, pos);
	}
	
	public CommonBackgroundStack getStackForFrame (int frameNumber) {
		//find correct file location
		//go to file location
		//read stack from file
		//return stack
	}
	
	private CommonBackgroundStack readStack() {
		//TODO
	}
	
	BackgroundRemovedImage readBRI(ImageProcessor bak) throws IOException {
		BackgroundRemovedImageHeader h = new BackgroundRemovedImageHeader(this);
		BackgroundRemovedImage bri = new BackgroundRemovedImage(h, bak);
		for (int j = 0; j < h.getNumims(); ++j) {
			bri.addSubImage(readBRISubIm(bak));
		}
		return bri;
		
	}
	
	 
	
	private BRISubImage readBRISubIm(ImageProcessor bak) throws IOException {
		Rectangle r = new Rectangle();
		
		r.x = readInt();
		r.y = readInt();
		r.width = readInt();
		r.height = readInt();
		ImageProcessor ip = bak.createProcessor(r.width, r.height);
		FileInfo fi = (FileInfo) backgroundFileInfo.clone();
		fi.width = r.width;
		fi.height = r.height;
    	byte buf[] = new byte[fi.width*fi.height*fi.getBytesPerPixel()];
    	read(buf);
    	ByteArrayInputStream bis = new ByteArrayInputStream(buf);
    	Object pixels = (new ImageReader(fi)).readPixels(bis);
		ip.setPixels(pixels);
		return new BRISubImage(ip,r);
	}
	
}
