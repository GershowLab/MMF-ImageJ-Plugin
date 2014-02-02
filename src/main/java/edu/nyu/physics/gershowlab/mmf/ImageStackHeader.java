package edu.nyu.physics.gershowlab.mmf;

import edu.nyu.physics.gershowlab.mmf.ImageStackHeader;
import edu.nyu.physics.gershowlab.mmf.ImageStackLocator;

/**
 * The header containing metadata for a stack of background removed images which share a common background image.
 *
 * 
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * @see ImageStackLocator
 *
 */
public class ImageStackHeader implements Comparable<ImageStackHeader>, Cloneable{
	/**
	 * The idcode = bb67ca20
	 */
	public final long idCode;
	/**
	 * The size of the header in bytes
	 */
	public final int headerSize;
	/**
	 * The size of the stack on disk
	 */
	public final int stackSize;
	/**
	 * The number of images in the stack
	 */
	public final int nframes;
	/**
	 * The location of the stack in the file
	 */
	public final long filePosition;
	
	/**
	 * Creates an ImageStackHeader out of the parameters given
	 * 
	 * @param idCode		idcode = bb67ca20
	 * @param headerSize	The size of the header in bytes
	 * @param stackSize		The size of the stack on disk
	 * @param nframes		The number of images in the stack
	 * @param filePosition	The location of the stack in the file
	 */
	public ImageStackHeader(long idCode, int headerSize, int stackSize,
			int nframes, long filePosition) {
		super();
		this.idCode = idCode;
		this.headerSize = headerSize;
		this.stackSize = stackSize;
		this.nframes = nframes;
		this.filePosition = filePosition;
	}
	
	
	@Override
	public int compareTo(ImageStackHeader o) {
		return (int) (this.filePosition - o.filePosition);
	}
	
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
