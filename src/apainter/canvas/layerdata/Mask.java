package apainter.canvas.layerdata;

import static apainter.misc.Util.*;
import apainter.CreateHandler;
import apainter.canvas.Canvas;
import apainter.canvas.event.PaintEventAccepter;
import apainter.data.PixelDataBuffer;

abstract class Mask implements PixelSetable,PaintEventAccepter,CreateHandler{

	protected boolean enable=false;
	protected Canvas canvas;
	public Mask(Canvas canvas) {
		this.canvas = nullCheack(canvas);
	}

	void setEnable(boolean b){
		enable = b;
	}
	boolean isEnable(){
		return enable;
	}

	public Canvas getCanvas(){
		return canvas;
	}

	abstract PixelDataBuffer getDataBuffer();
	public abstract MaskHandler getHandler();
}
