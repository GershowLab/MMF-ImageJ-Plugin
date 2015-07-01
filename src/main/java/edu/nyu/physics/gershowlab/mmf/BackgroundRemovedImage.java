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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

@SuppressWarnings("unused")
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
		/*
		Map<String,Object> m = header.getMetaData().getFieldNamesAndValues();
		for (Map.Entry<String, Object> entry : m.entrySet())
		{
		    ip.set -- hah imageProcessor doesn't have metadata -- screw up!
		}
		 */
		return ip;
	}
	
	public HashMap<String,Object> getMetaData() {
		return header.getMetaData().getFieldNamesAndValues();
	}

}
