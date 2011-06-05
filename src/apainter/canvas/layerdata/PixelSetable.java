package apainter.canvas.layerdata;

import java.awt.Rectangle;

import apainter.drawer.DrawAccepter;

interface PixelSetable extends PixelContainer{
	//TODO DrawEvent作ったらコメント解除
	//void draw(DrawEvent d);

	public void setPixel(int color,int x,int y);
	public void setPixels(int[] colors,int x,int y,int width,int height);
	/**
	 * 全て0にします。
	 */
	public void clear();
	/**
	 * 指定範囲を0にします。
	 * @param r
	 */
	public void clear(Rectangle r);
}
