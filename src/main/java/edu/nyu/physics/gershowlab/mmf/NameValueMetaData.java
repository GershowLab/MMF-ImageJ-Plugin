package edu.nyu.physics.gershowlab.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class NameValueMetaData extends ImageMetaData {

	@Override
	public HashMap<String, Object> getFieldNamesAndValues() {
		return namesAndValues;
	}

	
	public static final int idCode = 0xc15ac674;
	
	
	private HashMap<String,Object> namesAndValues;

	
	public NameValueMetaData(ByteBuffer b) {
		b.order(ByteOrder.LITTLE_ENDIAN);
		namesAndValues = new HashMap<String, Object>();
		int numElems = b.getInt();
	    for (int j = 0; j < numElems; ++j) {
	    	
	    	byte s[] = new byte[b.remaining()];
	        int index = 0;
	        byte c;
	        c = b.get();
	        while (c != '\0') {
	        	s[index++] = c;
	        	c = b.get();
	        }
	        double val = b.getDouble();
	        namesAndValues.put(new String(s, 0, index), val);
	        
	    }
		
	}
}
