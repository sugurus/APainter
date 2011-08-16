package apainter.pen;

import apainter.data.PixelDataByteBuffer;

public class Pen8bitGray extends Pen{
	byte[] mapdata;

	public Pen8bitGray(int w,int h,int x,int y){
		super(w,h,x,y);
		mapdata = new byte[w*h];
	}

	public Pen8bitGray(int w,int h,int x,int y,byte[] data){
		super(w,h,x,y);
		if(w*h!=data.length)throw new Error();
		mapdata =data;
	}

	public byte[] getMapData(){
		return mapdata;
	}

	@Override
	public PixelDataByteBuffer getDataBuffer() {
		PixelDataByteBuffer b = new PixelDataByteBuffer(width, height, mapdata);
		return b;
	}
}