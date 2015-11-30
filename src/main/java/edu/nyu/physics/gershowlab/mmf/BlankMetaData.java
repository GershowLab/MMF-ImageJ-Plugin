package edu.nyu.physics.gershowlab.mmf;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class BlankMetaData extends ImageMetaData {

	
	public BlankMetaData(ByteBuffer b) {
		
	}

	@Override
	public HashMap<String, Object> getFieldNamesAndValues() {
		return new HashMap<String,Object>();
	}

	public static final int idCode = 0x0ccd07bc;
	

}
