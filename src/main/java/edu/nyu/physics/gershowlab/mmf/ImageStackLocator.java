package edu.nyu.physics.gershowlab.mmf;
/*
 * 
 * Copyright 2013,2014 by Marc Gershow and Natalie Bernat
 * 
 * This file is part of MMF-ImageJ-Plugin.
 * 
 * MMF-ImageJ-Plugin is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, 
 * or (at your option) any later version.
 * 
 * MMF-ImageJ-Plugin is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MMF-ImageJ-Plugin.  If not, see http://www.gnu.org/licenses/.
 */
import edu.nyu.physics.gershowlab.mmf.ImageStackHeader;

/**
 * An extension of the ImageStackHeader which provides quick methods to determine if a frame (given by index) is located in the stack
 * 
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * @see ImageStackHeader
 */

public class ImageStackLocator extends ImageStackHeader {

	/**
	 * The index of the first frame in the image stack 
	 */
	private int startFrame;
	/**
	 * The index of the last frame in the image stack
	 */
	private int lastFrame;
	

	
	/**
	 * Creates an ImageStackLocator out of the info contained in the header
	 * 
	 * @param h				The ImageStackHeader
	 * @param startFrame	The starting frame index
	 * @see ImageStackHeader
	 */
	public ImageStackLocator (ImageStackHeader h, int startFrame) {
		this(h.idCode, h.headerSize, h.stackSize, h.nframes, h.filePosition, startFrame);
	}

	/**
	 * Creates an ImageStackLocator from individual parameters
	 * 
	 * @param idCode
	 * @param headerSize
	 * @param stackSize
	 * @param nframes
	 * @param filePosition
	 * @param startFrame
	 * @see ImageStackHeader.ImageStackHeader()
	 */
	public ImageStackLocator(long idCode, int headerSize, int stackSize,
			int nframes, long filePosition, int startFrame) {
		super(idCode, headerSize, stackSize, nframes, filePosition);
		this.startFrame = startFrame;
		this.lastFrame = startFrame + nframes - 1;
		
	}

	/**
	 * Returns true when the stack contains the query frame
	 * @param frame	The query frame
	 * @return true if the stack contains the query frame, otherwise false
	 */
	public boolean containsFrame(int frame) {
		return (startFrame <= frame && lastFrame >= frame);
	}

	/**
	 * Returns the index of the first frame in the stack
	 * @return The index of the first frame in the stack
	 */
	public int getStartFrame() {
		return startFrame;
	}

	/**
	 * Returns the index of the last frame in the stack
	 * @return The index of the last frame in the stack
	 */
	public int getLastFrame() {
		return lastFrame;
	}
	
}
