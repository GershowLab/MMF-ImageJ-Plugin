package edu.nyu.physics.gershowlab.mmf;

import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.io.FileInfo;
import ij.process.ImageProcessor;

/**
 * A stack of background removed images and their associated background image.
 * 
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * @see ImageStackLocator
 * @see BackgroundRemovedImage
 *
 */
public class CommonBackgroundStack {

	/**
	 * The image stack header containing stack metadata
	 */
	private ImageStackLocator h;
	/**
	 * A list containing the background removes images
	 */
	ArrayList<BackgroundRemovedImage> bri;
	/**
	 * The common background image
	 */
	ImageProcessor backgroundIm;	
	/**
	 * Metadata for the background image
	 */
	private FileInfo fi;
	
	/**
	 * Creates a CommonBackgroundStack from the MMF file and the information contained in the ImageStackLocator
	 * 
	 * @param isl				The header containing data about the stack
	 * @param f					The MMF file
	 * @throws IOException
	 * @see ImageStackLocator
	 * @see MmfFile
	 */
	public CommonBackgroundStack(ImageStackLocator isl, MmfFile f) throws IOException{
		bri = new ArrayList<BackgroundRemovedImage>();
		try {
			h = (ImageStackLocator) isl.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		f.seek(isl.filePosition + isl.headerSize);
		IplImageHeader iph = new IplImageHeader(f);
		fi = iph.getFileInfo();
		backgroundIm = iph.getImageData(f);
		
		for (int j = 0; j < h.nframes; ++j) {
			bri.add(f.readBRI(backgroundIm, fi));
		}
		if (f.getFilePointer() - h.filePosition != h.stackSize) {
			IJ.showMessage("WARNING", "incorrect number of bytes read from stack frame " + h.getLastFrame() + " to " + h.getLastFrame() + " at " + h.filePosition);
		}
	}
	
	/**
	 * Returns the FileInfo
	 * @return The FileInfo
	 */
	public FileInfo getFi() {
		return fi;
	}
	/**
	 * Returns the index of the first frame in the stack
	 * @return The index of the first frame in the stack
	 */
	public int getStartFrame() {
		return h.getStartFrame();
	}

	/**
	 * Returns the index of the last frame in the stack
	 * @return The index of the last frame in the stack
	 */
	public int getLastFrame() {
		return h.getLastFrame();
	}

	/**
	 * Returns true if the stack contains the query frame
	 * 
	 * @param frameNumber	The index of the query frame
	 * @return true if the stack contains the query frame, otherwise false
	 */
	public boolean containsFrame (int frameNumber) {
		return (getStartFrame() <= frameNumber && getLastFrame() >= frameNumber);
	}
	
	/**
	 * Reconstructs and returns a background removed image. 
	 * 
	 * @param frameNumber
	 * @return The reconstructed movie frame (or null, if the query frame is not in the stack)
	 * @see BackgroundRemovedImage
	 */
	ImageProcessor getImage(int frameNumber) {		
		if (!containsFrame(frameNumber)){
			return null;
		}
		return bri.get(frameNumber-h.getStartFrame()).restoreImage();
	}

}
