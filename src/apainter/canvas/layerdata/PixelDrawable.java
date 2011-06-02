package apainter.canvas.layerdata;

public interface PixelDrawable extends PixelContainer{
	//TODO DrawEvent作ったらコメント解除
	//void draw(DrawEvent d);

	public void setPixel(int color,int x,int y);
	public void setPixels(int[] colors,int x,int y,int width,int height);
}
