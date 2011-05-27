package apainter.data;

public class IntPixelDataBuffer extends PixelDataBuffer{
	private int[] pixel;

	public IntPixelDataBuffer(int w,int h,int[] pixel) {
		super(w,h);
		if(pixel.length!=w*h)throw new RuntimeException(String.format("pixel length(%d) != w(%d)*h(%d)",pixel.length,w,h));
		this.pixel = pixel;
	}


	@Override
	int[] getData() {
		return pixel;
	}

}
