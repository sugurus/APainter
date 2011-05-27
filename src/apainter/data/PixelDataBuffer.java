package apainter.data;

public abstract class PixelDataBuffer {
	public final int width,height;

	public PixelDataBuffer(int w,int h) {
		if(w <=0 || h <= 0)throw new RuntimeException(String.format("size error w:%d,h%d",w,h));
		width = w;
		height =h;
	}
	abstract Object getData();
}
