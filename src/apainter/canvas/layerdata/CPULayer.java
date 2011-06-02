package apainter.canvas.layerdata;

import java.awt.Rectangle;

import apainter.color.Color;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;

class CPULayer extends DefaultLayer{

	private PixelDataIntBuffer buffer;
	private int[] pixel;
	private CPUMask mask;

	public CPULayer(int id, String name,int width,int height) {
		super(id, name);
		buffer = PixelDataIntBuffer.create(width, height);
		pixel = buffer.getData();
		mask = new CPUMask(width, height);

	}

	@Override
	public void dispose() {
		buffer=null;
		pixel = null;
		mask = null;
	}

	@Override
	public LayerHandler getHandler() {
		// TODO layer handler
		return null;
	}

	@Override
	public boolean isDrawable() {
		// TODO layer isDrawable
		return false;
	}

	@Override
	public boolean isEnableMask() {
		return mask.isEnable();
	}

	@Override
	public void setEnableMask(boolean b) {
		mask.setEnable(b);
	}

	@Override
	public void createMask() {
		if(isEnableMask())throw new RuntimeException("mask enable");
		// TODO layer createMask

	}

	@Override
	public void render(PixelDataBuffer destination, Rectangle r) {
		// TODO layer render

	}

	@Override
	public void setPixel(int color, int x, int y) {
		if(color>>>24==0)color = 0;
		buffer.setData(color, x, y);
	}

	@Override
	public void setPixels(int[] colors, int x, int y, int width, int height) {
		//TODO layer setPixels
	}

	@Override
	public int getPixel(int x, int y) {
		return buffer.contains(x,y)?pixel[x+y*buffer.width]:Color.NotColor;
	}

	@Override
	public int[] getPixels(int x, int y, int width, int height) {
		return copyPixels(x, y, width, height, new int[width*height]);
	}

	@Override
	public int[] copyPixels(int x, int y, int width, int height,
			int[] distenation) {
		// TODO layer copypixels
		return null;
	}

}
