package apainter.data;

public class BytePixelDataBuffer extends PixelDataBuffer{
	private byte[] pixel;

	public BytePixelDataBuffer(int w,int h,byte[] pixel) {
		super(w,h);
		if(pixel.length!=w*h)throw new RuntimeException(String.format("pixel length(%d) != w(%d)*h(%d)",pixel.length,w,h));
		this.pixel = pixel;
	}


	@Override
	byte[] getData() {
		return pixel;
	}

}
