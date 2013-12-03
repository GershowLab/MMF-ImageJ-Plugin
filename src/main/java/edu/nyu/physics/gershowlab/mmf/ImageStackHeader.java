package edu.nyu.physics.gershowlab.mmf;
/*
 * Stack of common background images, beginning with this header:
512 byte zero-padded header, with the following fields (all 4 byte ints, except idcode):
4 byte unsigned long idcode = bb67ca20, header size in bytes, total size of stack on disk, nframes: number of images in stack
 */
public class ImageStackHeader implements Comparable<ImageStackHeader>, Cloneable{
	public final long idCode;
	public final int headerSize;
	public final int stackSize;
	public final int nframes;
	public final long filePosition;
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
