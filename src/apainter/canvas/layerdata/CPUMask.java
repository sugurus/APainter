package apainter.canvas.layerdata;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

import apainter.Color;
import apainter.data.PixelDataByteBuffer;
import apainter.drawer.DrawAccepter;
import apainter.drawer.DrawEvent;

class CPUMask extends Mask implements DrawAccepter{


	private PixelDataByteBuffer buffer;
	private byte[] pixel;
	private MemoryImageSource imagesource;
	private Image img;

	public CPUMask(int width,int height) {
		buffer = PixelDataByteBuffer.create(width, height);
		pixel = buffer.getData();
		ColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_GRAY),
				new int[] {8},
				false, true,
				Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
		imagesource=new MemoryImageSource(width, height, colorModel, pixel, 0, width);
		imagesource.setAnimated(true);
		img = Toolkit.getDefaultToolkit().createImage(
				imagesource
		);
	}

	byte[] getPixel(){
		return pixel;
	}

	PixelDataByteBuffer getPixelDataBuffer(){
		return buffer;
	}

	Image getImage(){
		return img;
	}

	@Override
	public void setPixel(int color, int x, int y) {
		int ycrcb = Color.RGB2YCrCb(color);
		int Y = ycrcb>>16&0xff;
		buffer.setData((byte)Y, x, y);
	}

	@Override
	public void setPixels(int[] colors,final int x,final int y,final int width,final int height) {
		Rectangle r = buffer.intersection(new Rectangle(x,y,width,height));
		if(r.isEmpty())return;
		int ex = r.width+r.x;
		int ey = r.height+r.y;
		int add= width-r.width;
		int i= (r.y-y)*width-add+r.x-x;
		for(int X,Y=r.y;Y<ey;Y++){
			i+=add;
			for(X = r.x;X<ex ;X++,i++){
				int yrb = Color.RGB2YCrCb(colors[i]);
				byte b = (byte) ((yrb>>>24)*(yrb>>16&0xff)/255);
				buffer.setData(b, X,Y);
			}
		}
	}

	@Override
	public int getPixel(int x, int y) {
		return buffer.contains(x, y)?Color.NotColor:pixel[x+y*buffer.width];
	}

	@Override
	public int[] getPixels(int x, int y, int width, int height) {
		return copyPixels(x, y, width, height, null);
	}

	@Override
	public int[] copyPixels(int x, int y, int width, int height,
			int[] distination) {
		return buffer.copy(distination, new Rectangle(x,y,width,height));
	}

	@Override
	PixelDataByteBuffer getDataBuffer() {
		return buffer;
	}

	@Override
	public void clear() {
		Arrays.fill(pixel, (byte)0);
	}
	@Override
	public void clear(Rectangle r) {
		buffer.setData((byte)0, r);
	}

	@Override
	public boolean paint(DrawEvent e) {
		// TODO mask paint
		return false;
	}

	public void dispose(){
		buffer.dispose();
		pixel = null;
		img.flush();
	}


	@Override
	public void startPaint(Object source) {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void endPaint(Object source) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
