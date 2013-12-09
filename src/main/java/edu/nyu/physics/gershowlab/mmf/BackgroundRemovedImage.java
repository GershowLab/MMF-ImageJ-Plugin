package edu.nyu.physics.gershowlab.mmf;

import java.util.ArrayList;

import ij.process.ImageProcessor;

public class BackgroundRemovedImage {

	private ArrayList<BRISubImage> subim;
	private ImageProcessor backgroundIm;
	private BackgroundRemovedImageHeader header;
	public BackgroundRemovedImage(BackgroundRemovedImageHeader header, ImageProcessor backgroundIm) {
		this.backgroundIm = backgroundIm;
		this.header = header;
		subim = new ArrayList<BRISubImage>();
		// TODO Auto-generated constructor stub
	}
	
	public void addSubImage (BRISubImage im) {
		subim.add(im);
	}
	
	public ImageProcessor restoreImage() {
		ImageProcessor ip = backgroundIm.duplicate();
		for (int j = 0; j < subim.size(); ++j) {
			subim.get(j).insertIntoImage(ip);
		}
		return ip;
	}
	

}
