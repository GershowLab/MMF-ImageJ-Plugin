package edu.nyu.physics.gershowlab.mmf;

import ij.process.ImageProcessor;

public class CommonBackgroundStack {

	private int firstFrame;
	private int lastFrame;
	ArrayList<BackgroundRemovedImage> bri;
	ImageProcessor backgroundIm;
	public CommonBackgroundStack() {
		// TODO Auto-generated constructor stub
	}
	
	public int getFirstFrame() {
		return firstFrame;
	}

	public int getLastFrame() {
		return lastFrame;
	}

	ImageProcessor getImage(int frameNumber) {
		ImageProcessor ip =backgroundIm.clone();
		//TODO restore differences;
		//bri[frameNumber-firstFrame].restore(ip);
		return ip;
	}

}
