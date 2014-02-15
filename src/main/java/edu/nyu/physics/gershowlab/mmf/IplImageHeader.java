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
import ij.io.FileInfo;
import ij.io.ImageReader;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.activation.UnsupportedDataTypeException;

import java.nio.ByteBuffer;

import ucar.unidata.io.RandomAccessFile;
/**
 * 
 * Contains metadata about an image from an MMF movie file, and provides methods to convert the MMF image into an ImageProcessor.
 * 
 * @author Marc Gershow
 * @author Natalie Bernat
 * @version 1.0
 * 
 */
@SuppressWarnings("unused")
public class IplImageHeader {
	private static final int IPL_DEPTH_SIGN =  0x80000000;
	private static final int IPL_DEPTH_1U = 1;
	private static final int IPL_DEPTH_8U = 8;
	private static final int IPL_DEPTH_16U =  16;
	private static final int IPL_DEPTH_32F = 32;
	private static final int IPL_DEPTH_64F = 64;
	private static final int IPL_DEPTH_8S = (IPL_DEPTH_SIGN| 8);
	private static final int IPL_DEPTH_16S = (IPL_DEPTH_SIGN|16);
	private static final int IPL_DEPTH_32S = (IPL_DEPTH_SIGN|32);

		/**
		 * sizeof(IplImage)
		 */
	    private int nSize;
	    /**
	     * version (=0)
	     */
	    private int ID; 
	    /**
	     * Most of OpenCV functions support 1,2,3 or 4 channels
	     */
	    private int nChannels; 
	    /**
	     * ignored by OpenCV
	     */
	    private int alphaChannel;
	    /**
	     * pixel depth in bits: IPL_DEPTH_8U, IPL_DEPTH_8S, IPL_DEPTH_16S, IPL_DEPTH_32S, IPL_DEPTH_32F and IPL_DEPTH_64F are supported
	     */
	    private int depth; 
	    /**
	     * ignored by OpenCV
	     */
	    private byte[] colorModel = new byte[4];
	    /**
	     * ignored by OpenCV
	     */
	    private byte[] channelSeq = new byte[4];
	    /**
	     * 0 - interleaved color channels, 1 - separate color channels. cvCreateImage can only create interleaved images
	     */
	    private int dataOrder; 
	    /**
	     * 0 - top-left origin,	1 - bottom-left origin (Windows bitmaps style)
	     */
	    private int origin; 
	    /**
	     * Alignment of image rows (4 or 8). OpenCV ignores it and uses widthStep instead
	     */

		private int align; 
	    /**
	     * image width in pixels 
	     */
	    private int width; 
	    /**
	     * image height in pixels 
	     */
	    private int height; 
	    /**
	     * image ROI. if NULL, the whole image is selected 
	     */
	    private long roiPTR; 
	    /**
	     * must be NULL 
	     */
	    private long maskROIPTR;
	    /**
	     * must be NULL 
	     */
	    private long imageIdPTR; 
	    /**
	     * must be NULL 
	     */
	    private long tileInfoPTR;
	    /**
	     * image data size in bytes (==image->height*image->widthStep in case of interleaved data)
	     */
	    private int imageSize; 
	    /**
	     * pointer to aligned image data 
	     */
	    private long imageDataPTR; 
	    /**
	     * size of aligned image row in bytes
	     */
	    private int widthStep; 
	    /**
	     * ignored by OpenCV
	     */
	    private int[] BorderMode = new int[4];
	    /**
	     * ignored by OpenCV
	     */
	    private int[] BorderConst = new int[4]; 
	    /**
	     * pointer to very origin of image data (not necessarily aligned) - needed for correct deallocation
	     */
	    private long imageDataOriginPTR;
	

	    /**
	     * Creates an IplImageHeader from the MMF file at the current file pointer location
	     * 
	     * @param raf	The MMF file
	     * @throws IOException
	     */
	    public IplImageHeader(RandomAccessFile raf) throws IOException{
	    	long pos = raf.getFilePointer();
	    	nSize = raf.readInt();
	    	ID = raf.readInt();
	    	nChannels = raf.readInt();
	    	alphaChannel = raf.readInt();
	    	depth = raf.readInt();
	    	for (int i = 0; i < colorModel.length; i++) {
				
				colorModel[i]= raf.readByte();
			}
	    	for (int i = 0; i < channelSeq.length; i++) {
				channelSeq[i] = raf.readByte();
			}
	    	dataOrder = raf.readInt(); 
	    	origin = raf.readInt(); 
	    	align = raf.readInt(); 
	    	width = raf.readInt(); 
	    	height = raf.readInt();
	    	roiPTR = readPointer(raf);
		    maskROIPTR = readPointer(raf); 
		    imageIdPTR = readPointer(raf); 
		    tileInfoPTR = readPointer(raf);
		    imageSize = raf.readInt();
		    imageDataPTR = readPointer(raf);
		    widthStep = raf.readInt(); 
		    for (int i = 0; i < BorderMode.length; ++i) {
		    	BorderMode[i] = raf.readInt();
		    }
		    for (int i = 0; i < BorderConst.length; ++i) {
		    	BorderConst[i] = raf.readInt();
		    }
		    imageDataOriginPTR = raf.readInt();
		    if (raf.getFilePointer() - pos != nSize) {
		    	throw new IOException("did not read correct number of bytes from IplImage Header");//should never happen
		    }
	    }
	    
	    /**
	     * Returns a number of the appropriate data type (based on the value of nSize) from the MMF file 
	     * 
	     * @param raf	The MMF file
	     * @return A number
	     * @throws IOException
	     */
	    private long readPointer (RandomAccessFile raf) throws IOException {
	    	switch (nSize) {
		    	case 112:
		    		return raf.readInt();
		    	case 136:
		    		return raf.readLong();			
	    	}
	    	throw new UnsupportedDataTypeException("Expected IplImage Header to be 112 or 136 bytes");
	    	
	    }
	    
	    /**
	     * Returns metadata for the image.
	     *  
	     * @return Metadata for the image
	     * @throws IOException
	     */
	    public FileInfo getFileInfo() throws IOException {
	    	int bytesPerPixel;
	    	FileInfo fi = new FileInfo();
	    	fi.fileFormat = FileInfo.RAW;
	    	fi.intelByteOrder = true;
	    	fi.whiteIsZero = false;
	    	fi.height = height;
	    	fi.nImages = 1;
	    	fi.directory = null;
	    	fi.url = null;
	    	fi.fileName = null;
	    	fi.longOffset = fi.offset = 0;
	    	fi.width = width;
	    	switch (depth) {
	    	case IPL_DEPTH_8U:	    		
	    	case IPL_DEPTH_8S:
	    		bytesPerPixel = 1;
	    		fi.fileType = FileInfo.GRAY8;
	    		break;
	    	case IPL_DEPTH_16S: 
	    		fi.fileType = FileInfo.GRAY16_SIGNED;
	    		bytesPerPixel = 2;
	    		break;
	    	case IPL_DEPTH_16U:
	    		fi.fileType = FileInfo.GRAY16_UNSIGNED;
	    		bytesPerPixel = 2;
	    		break;
	    	case IPL_DEPTH_32F: 
	    		fi.fileType = FileInfo.GRAY32_FLOAT;
	    		bytesPerPixel = 4;
	    		break;
	    		case IPL_DEPTH_32S:
	    		fi.fileType = FileInfo.GRAY32_INT;
	    		bytesPerPixel = 4;
	    		break;
	    	case IPL_DEPTH_64F:
	    		fi.fileType = FileInfo.GRAY64_FLOAT;
	    		bytesPerPixel = 8;
	    		break;
    		default:
    			throw new IOException("unrecognized bit depth = " + depth);
	    	}
	    	if (this.nChannels != 1) {
	    		if (nChannels == 3 && bytesPerPixel == 1) {
	    			fi.fileType = FileInfo.BGR;
	    		} else {
	    			throw new IOException("unsupported bit depth and channel combination; bit depth = " + depth + "; nchannels = " + nChannels);
	    		}
	    	}
	    	
	    	return fi;
	    	
	    }
	    
	    /**
	     * Reads the image data from the MMF file and returns it as an ImageProcessor
	     * 
	     * @param raf the MMF file
	     * @return An ImageProcessor
	     * @throws IOException
	     */
	    public ImageProcessor getImageData(RandomAccessFile raf) throws IOException {
	    	
	    	FileInfo fi = getFileInfo();
	    	ImageReader ir = new ImageReader(fi);
	    	int nbytes = fi.width*fi.height*fi.getBytesPerPixel();
	    	byte buf[] = new byte[nbytes];
	    	if (fi.width*fi.getBytesPerPixel() == widthStep) { 
		    	raf.read(buf);
	    	} else {
	    		byte btemp[] = new byte[widthStep];
	    		ByteBuffer bb = ByteBuffer.wrap(buf);
	    		for (int j = 0; j < height; ++j) {
	    			raf.read(btemp);
	    			bb.put(btemp, 0, fi.width*fi.getBytesPerPixel());	    			
	    		}
	    	}
	    	ByteArrayInputStream bis = new ByteArrayInputStream(buf);
	    	Object pixels = ir.readPixels(bis);
	    	
	    	switch (fi.fileType) {
	    	case FileInfo.GRAY8:
	    		return new ByteProcessor(fi.width, fi.height, (byte[]) pixels);
			case FileInfo.GRAY16_SIGNED: case FileInfo.GRAY16_UNSIGNED:
	    		return new ShortProcessor(fi.width, fi.height, (short[]) pixels, null);
	    	case FileInfo.GRAY32_FLOAT:
	    		return new FloatProcessor(fi.width, fi.height, (float []) pixels);
	    	case FileInfo.RGB: case FileInfo.BGR:
	    		return new ColorProcessor(fi.width, fi.height, (int[]) pixels); 
	    	default:
	    		throw new IOException("unhandled image type: " + fi.toString());
	    	}
	    	
	    }
	    
	    /**
	     * Reads and returns an ImageProcessor of the image at the location of the current file pointer in the MMF
	     * 
	     * @param raf The MMF file
	     * @return An ImageProcessor of the image at the location of the current file pointer in the MMF
	     * @throws IOException
	     */
	    public static ImageProcessor loadIplImage (RandomAccessFile raf) throws IOException {
	    	IplImageHeader im = new IplImageHeader(raf);
	    	return im.getImageData(raf);
	    	
	    }
	    
}

