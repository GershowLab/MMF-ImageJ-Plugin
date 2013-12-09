package edu.nyu.physics.gershowlab.mmf;

import java.awt.Rectangle;

import ij.process.Blitter;
import ij.process.ImageProcessor;

class BRISubImage {

	private ImageProcessor ip;
	private Rectangle loc;
	
	public BRISubImage(ImageProcessor ip, Rectangle loc) {
		this.ip = ip;
		this.loc = loc;
	}
	
	public void insertIntoImage (ImageProcessor im) {
		im.copyBits(ip, loc.x, loc.y, Blitter.COPY);
	}

}
