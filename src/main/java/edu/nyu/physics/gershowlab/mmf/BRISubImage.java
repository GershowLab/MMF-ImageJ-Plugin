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
