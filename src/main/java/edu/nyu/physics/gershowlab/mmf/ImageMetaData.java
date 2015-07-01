package edu.nyu.physics.gershowlab.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

public abstract class ImageMetaData {

	public abstract HashMap<String, Object> getFieldNamesAndValues ();
	public static final int idCode = 0;

	
	/*
	 * case BlankMetaData::IdCode:
                return BlankMetaData::fromFile(is);
                break;
            case MightexMetaData::IdCode:
                return MightexMetaData::fromFile(is);
                break; 
            case CompositeImageMetaData::IdCode:
                return CompositeImageMetaData::fromFile(is);
                break;
            case NameValueMetaData::IdCode:
                return NameValueMetaData::fromFile(is);
                break;
	 * 
	 */
	public static ImageMetaData loadMetaData(ByteBuffer b) {
		b.order(ByteOrder.LITTLE_ENDIAN);
		int idcode = b.getInt();
		switch (idcode) {
			case NameValueMetaData.idCode:
				return new NameValueMetaData(b);
			case BlankMetaData.idCode:
				return new BlankMetaData(b);
			case CompositeMetaData.idCode:
				return new CompositeMetaData(b);
			default:
				return null;
		}
	}
			
				
			
		
	
	
}
