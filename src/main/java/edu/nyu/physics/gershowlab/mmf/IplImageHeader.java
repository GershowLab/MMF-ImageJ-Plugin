package edu.nyu.physics.gershowlab.mmf;

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

	//TODO suppress warnings
	    private int nSize; /* sizeof(IplImage) */
	    private int ID; /* version (=0)*/
	    private int nChannels; /* Most of OpenCV functions support 1,2,3 or 4 channels */
	    private int alphaChannel; /* ignored by OpenCV */
	    private int depth; /* pixel depth in bits: IPL_DEPTH_8U, IPL_DEPTH_8S, IPL_DEPTH_16S,
	IPL_DEPTH_32S, IPL_DEPTH_32F and IPL_DEPTH_64F are supported */
	    private byte[] colorModel = new byte[4]; /* ignored by OpenCV */
	    private byte[] channelSeq = new byte[4]; /* ditto */
	    private int dataOrder; /* 0 - interleaved color channels, 1 - separate color channels.
	cvCreateImage can only create interleaved images */
	    private int origin; /* 0 - top-left origin,
	1 - bottom-left origin (Windows bitmaps style) */
	    private int align; /* Alignment of image rows (4 or 8).
	OpenCV ignores it and uses widthStep instead */
	    private int width; /* image width in pixels */
	    private int height; /* image height in pixels */
	    private long roiPTR;/* image ROI. if NULL, the whole image is selected */
	    private long maskROIPTR; /* must be NULL */
	    private long imageIdPTR; /* ditto */
	    private long tileInfoPTR; /* ditto */
	    private int imageSize; /* image data size in bytes
	(==image->height*image->widthStep
	in case of interleaved data)*/
	    private long imageDataPTR; /* pointer to aligned image data */
	    private int widthStep; /* size of aligned image row in bytes */
	    private int[] BorderMode = new int[4]; /* ignored by OpenCV */
	    private int[] BorderConst = new int[4]; /* ditto */
	    private long imageDataOriginPTR; /* pointer to very origin of image data
	(not necessarily aligned) -
	needed for correct deallocation */
	
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
	    
	    private long readPointer (RandomAccessFile raf) throws IOException {
	    	switch (nSize) {
		    	case 112:
		    		return raf.readInt();
		    	case 136:
		    		return raf.readLong();			
	    	}
	    	throw new UnsupportedDataTypeException("Expected IplImage Header to be 112 or 136 bytes");
	    	
	    }
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
	    	
	    	/*
	    	fi.width = widthStep/(bytesPerPixel*nChannels);
	    	if (fi.width < width) {
	    		throw new IOException("widthStep is less than width of pixel row data");
	    	}
	    	*/
	    	return fi;
	    	
	    }
	    private ImageProcessor getImageData(RandomAccessFile raf) throws IOException {
	    	
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
	    public static ImageProcessor loadIplImage (RandomAccessFile raf) throws IOException {
	    	IplImageHeader im = new IplImageHeader(raf);
	    	return im.getImageData(raf);
	    	
	    }
}

