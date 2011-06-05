package apainter.canvas.layerdata;

import apainter.data.PixelDataBuffer;

abstract class Mask implements PixelSetable{

	protected boolean enable=false;

	void setEnable(boolean b){
		enable = b;
	}
	boolean isEnable(){
		return enable;
	}

	abstract PixelDataBuffer getDataBuffer();
}
