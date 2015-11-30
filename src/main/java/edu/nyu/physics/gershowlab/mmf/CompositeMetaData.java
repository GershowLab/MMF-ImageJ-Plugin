package edu.nyu.physics.gershowlab.mmf;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Vector;

public class CompositeMetaData extends ImageMetaData {

	public static final int idCode = 0x9844e951;
	
	private Vector<ImageMetaData> imd;

	@Override
	public HashMap<String, Object> getFieldNamesAndValues() {
		HashMap<String, Object>hm = new HashMap<String, Object>();
		for (int j = 0; j < imd.size(); ++j) {
			hm.putAll(imd.get(j).getFieldNamesAndValues());
		}
		return hm;
	}
	

	
	public CompositeMetaData(ByteBuffer b) {
		imd = new Vector<ImageMetaData>();

		int len = b.getInt();
	    for (int j = 0; j < len; ++j) {
	    	imd.add(ImageMetaData.loadMetaData(b));
	    }
	}

}
