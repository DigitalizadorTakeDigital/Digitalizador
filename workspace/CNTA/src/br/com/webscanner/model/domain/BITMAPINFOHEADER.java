package br.com.webscanner.model.domain;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class BITMAPINFOHEADER extends Structure {
	
	public BITMAPINFOHEADER(Pointer p, int align) {
		super(p);
		setAlignType(align);
		read();
	}
	public BITMAPINFOHEADER(Pointer p) {
		super(p);
		setAlignType(Structure.ALIGN_NONE);
		read();
	}
	public int biSize; 
	public int biWidth; 
	public int biHeight; 
	public short biPlanes; 
	public short biBitCount; 
	public int biCompression; 
	public int biSizeImage; 
	public int biXPelsPerMeter; 
	public int biYPelsPerMeter; 
	public int biClrUsed; 
	public int biClrImportant;
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] { "biSize", "biWidth", "biHeight", "biPlanes", "biBitCount", "biCompression", "biSizeImage", "biXPelsPerMeter", "biYPelsPerMeter", "biClrUsed", "biClrImportant" });
	}
}
