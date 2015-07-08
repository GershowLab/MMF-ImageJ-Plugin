package edu.nyu.physics.gershowlab.mmf;

public class WritableMdatPair {

	private int frameNum;
	
	private Object value;
	
	public WritableMdatPair(int fNum, Object val){
		frameNum = fNum;
		value = val;
	}
	
	public int getFrameNum(){
		return frameNum;
	}
	
	public Object getValue(){
		return value;
	}
	
}
