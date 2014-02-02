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
import edu.nyu.physics.gershowlab.mmf.MmfFile;

/**
 * The main header for an MMF movie file
 * <p>
 * Format:
 * <p>
 * 10240 byte zero padded header beginning with a textual description of the file, followed by \0 then the following fields (all ints, except idcode)
 * 4 byte unsigned long idcode = a3d2d45d, header size in bytes, key frame interval, threshold below background, threshold above background
 * 
 * @author Marc Gershow
 * @version 1.0
 * @see MmfFile
 */
class MmfHeader {
	public String headerText;
	public long idCode;
	public int headerSizeInBytes;
	public int keyFrameInterval;
	public int threshAboveBackground;
	public int threshBelowBackground;
	
	/**
	 * Creates an MmfHeader with the specified parameters
	 */
	public MmfHeader(String headerText, long idCode, int headerSizeInBytes,
			int keyFrameInterval, int threshAboveBackground,
			int threshBelowBackground) {
		super();
		this.headerText = headerText;
		this.idCode = idCode;
		this.headerSizeInBytes = headerSizeInBytes;
		this.keyFrameInterval = keyFrameInterval;
		this.threshAboveBackground = threshAboveBackground;
		this.threshBelowBackground = threshBelowBackground;
	}
	
	
}
