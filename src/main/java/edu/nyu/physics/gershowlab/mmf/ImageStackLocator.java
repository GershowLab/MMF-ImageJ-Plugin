package edu.nyu.physics.gershowlab.mmf;

public class ImageStackLocator extends ImageStackHeader {

	private int startFrame;
	private int lastFrame;
	

	
	
	public ImageStackLocator (ImageStackHeader h, int startFrame) {
		this(h.idCode, h.headerSize, h.stackSize, h.nframes, h.filePosition, startFrame);
	}
	
	public ImageStackLocator(long idCode, int headerSize, int stackSize,
			int nframes, long filePosition, int startFrame) {
		super(idCode, headerSize, stackSize, nframes, filePosition);
		this.startFrame = startFrame;
		this.lastFrame = startFrame + nframes - 1;
		
	}

	public boolean containsFrame(int frame) {
		return (startFrame <= frame && lastFrame >= frame);
	}

	public int getStartFrame() {
		return startFrame;
	}

	public int getLastFrame() {
		return lastFrame;
	}
	
}
