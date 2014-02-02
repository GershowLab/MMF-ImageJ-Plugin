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
