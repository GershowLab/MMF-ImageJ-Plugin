package edu.nyu.physics.gershowlab.mmf;

import java.awt.Rectangle;

import ij.process.Blitter;
import ij.process.ImageProcessor;

/**
 * An subimage with an associated location which indicated where the subimage belongs on the main (background) image
 * 
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * @see BackgroundRemovedImage
 *
 */


class BRISubImage {

	/**
	 * The subimage
	 */
	private ImageProcessor ip;
	/**
	 * The location of the subimage on the background image 
	 */
	private Rectangle loc;
	
	/**
	 * Creates a BRI subimage
	 * 
	 * @param ip	The subimage
	 * @param loc	The location of the subimage on the background image
	 */
	public BRISubImage(ImageProcessor ip, Rectangle loc) {
		this.ip = ip;
		this.loc = loc;
	}
	
	/**
	 * Inserts the subimage onto the background image at the appropriate location
	 * 
	 * @param im The background image
	 * @see BackgroundRemovedImage
	 */
	public void insertIntoImage (ImageProcessor im) {
		im.copyBits(ip, loc.x, loc.y, Blitter.COPY);
	}

}
