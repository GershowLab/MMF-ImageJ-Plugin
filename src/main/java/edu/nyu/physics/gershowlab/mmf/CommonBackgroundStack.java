package edu.nyu.physics.gershowlab.mmf;

import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.process.ImageProcessor;

public class CommonBackgroundStack {

	
	private ImageStackLocator h;
	ArrayList<BackgroundRemovedImage> bri;
	ImageProcessor backgroundIm;
	public CommonBackgroundStack(ImageStackLocator isl, MmfFile f) throws IOException{
		try {
			h = (ImageStackLocator) isl.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		f.seek(isl.filePosition + isl.headerSize);
		backgroundIm = IplImageHeader.loadIplImage(f);
		for (int j = 0; j < h.nframes; ++j) {
			bri.add(f.readBRI(backgroundIm));
		}
		if (f.getFilePointer() - h.filePosition != h.stackSize) {
			IJ.showMessage("WARNING", "incorrect number of bytes read from stack frame " + h.getLastFrame() + " to " + h.getLastFrame() + " at " + h.filePosition);
		}
	}
	
	public int getStartFrame() {
		return h.getStartFrame();
	}

	public int getLastFrame() {
		return h.getLastFrame();
	}

	ImageProcessor getImage(int frameNumber) {		
		return bri.get(frameNumber-h.getLastFrame()).restoreImage();
	}

}
