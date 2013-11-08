package edu.nyu.physics.gershowlab.mmf;

/*
 * 10240 byte zero padded header beginning with a textual description of the file, followed by \0 then the following fields (all ints, except idcode)
 * 4 byte unsigned long idcode = a3d2d45d, header size in bytes, key frame interval, threshold below background, threshold above background
 * Header is followed by a set of common background image stacks, with the following format:
 * 
 */
class MmfHeader {
	public String headerText;
	public long idCode;
	public int headerSizeInBytes;
	public int keyFrameInterval;
	public int threshAboveBackground;
	public int threshBelowBackground;
	
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
