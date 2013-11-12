package edu.nyu.physics.gershowlab.mmf;

import ij.process.ImageProcessor;

public class BackgroundRemovedImage {

	ArrayList<BRISubImage> subim;
	ImageProcessor backgroundIm;
	
	public BackgroundRemovedImage() {
		// TODO Auto-generated constructor stub
	}
	
	public ImageProcessor RestoreImage() {
		ImageProcessor ip = backgroundIm.clone();
		//iterate over subims and insertIntoImage
		return ip;
	}
	

}
