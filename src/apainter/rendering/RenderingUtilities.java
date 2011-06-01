package apainter.rendering;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;

public class RenderingUtilities {
	public static Rectangle getEnableClipBounds
		(PixelDataBuffer base,PixelDataBuffer over,Point p,Rectangle clip){
		Rectangle ret = base.crossRegion(clip);
		ret = ret.intersection(over.getBounds(p));
		return ret;
	}


	private RenderingUtilities(){}
}
