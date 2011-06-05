package apainter.rendering;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;

public class RenderingUtilities {
	/**
	 *
	 * @param base
	 * @param over
	 * @param p  overの左上のポイント(null不可)
	 * @param clip 全体のクリッピング領域(null可)
	 * @return
	 */
	public static Rectangle getEnableClipBounds
		(PixelDataBuffer base,PixelDataBuffer over,Point p,Rectangle clip){
		Rectangle ret;
		if(clip!=null)
			ret = base.intersection(clip);
		else
			ret = base.getBounds();
		ret = ret.intersection(over.getBounds(p));
		return ret;
	}


	private RenderingUtilities(){}
}
