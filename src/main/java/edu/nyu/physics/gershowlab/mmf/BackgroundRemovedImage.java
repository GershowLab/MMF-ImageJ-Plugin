package edu.nyu.physics.gershowlab.mmf;

import java.util.ArrayList;

import ij.process.ImageProcessor;

/**
 * Contains the background image and the subimages necessary to reconstruct a video frame
 * 
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * @see BackgroundRemovedImageHeader
 * @see BRISubImage
 */


public class BackgroundRemovedImage {

	/**
	 * A list of all the subimages needed to reconstruct this frame
	 */
	private ArrayList<BRISubImage> subim;
	/**
	 * The background image used to reconstruct this frame
	 */
	private ImageProcessor backgroundIm;
	/**
	 * The header containing metadata for this frame
	 */
	private BackgroundRemovedImageHeader header;
	
	/**
	 * Creates a BackgroundRemovedImage
	 * 
	 * @param header		The header
	 * @param backgroundIm	The background Image
	 * @see BackgroundRemovedImageHeader
	 * @see BRISubImage
	 */
	public BackgroundRemovedImage(BackgroundRemovedImageHeader header, ImageProcessor backgroundIm) {
		this.backgroundIm = backgroundIm;
		this.header = header;
		subim = new ArrayList<BRISubImage>();
	}
	
	/**
	 * Adds an image to the list of subimages
	 * 
	 * @param im The subimage to add
	 * @see BRISubImage
	 */
	public void addSubImage (BRISubImage im) {
		subim.add(im);
	}
	
	/**
	 * Reconstructs the movie frame from the background image and the subimages 
	 * 
	 * @return The reconstructed frame
	 */
	public ImageProcessor restoreImage() {
		ImageProcessor ip = backgroundIm.duplicate();
		for (int j = 0; j < subim.size(); ++j) {
			subim.get(j).insertIntoImage(ip);
		}
		return ip;
	}
	

}
