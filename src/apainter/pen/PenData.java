package apainter.pen;

import apainter.data.PixelData;

public abstract class PenData {
	protected final int width ,height,x,y;

	public PenData(int w,int h,int x,int y){
		width = w;
		height = h;
		this.x=x;
		this.y=y;
	}

	public abstract Object getMapData();
	public abstract PixelData getDataBuffer();

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public int getXPosition(){
		return x;
	}

	public int getYPosition(){
		return y;
	}

}
