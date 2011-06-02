package apainter.canvas.layerdata;

import apainter.color.Color;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;

public class CPUMask extends Mask{

	private PixelDataByteBuffer buffer;
	private byte[] pixel;

	public CPUMask(int width,int height) {
		buffer = PixelDataByteBuffer.create(width, height);
		pixel = buffer.getData();
	}

	@Override
	public void setPixel(int color, int x, int y) {
		int ycrcb = Color.RGB2YCrCb(color);
		int Y = ycrcb>>16&0xff;
		buffer.setData((byte)Y, x, y);
	}

	@Override
	public void setPixels(int[] colors, int x, int y, int width, int height) {
		// TODO mase setPixels

	}

	@Override
	public int getPixel(int x, int y) {
		return buffer.contains(x, y)?Color.NotColor:pixel[x+y*buffer.width];
	}

	@Override
	public int[] getPixels(int x, int y, int width, int height) {
		return copyPixels(x, y, width, height, new int[width*height]);
	}

	@Override
	public int[] copyPixels(int x, int y, int width, int height,
			int[] distenation) {
		// TODO copy
		return null;
	}

	@Override
	PixelDataByteBuffer getDataBuffer() {
		return buffer;
	}

}
