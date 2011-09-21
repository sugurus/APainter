package apainter.data;

public class Compressed15BitPixelData extends CompressedPixelData{
	private CompressedIntPixelData integer,decimal;
	
	public Compressed15BitPixelData(PixelData15BitBuffer buffer) {
		PixelDataIntBuffer i1,i2;
		i1 = buffer.getIntegerBuffer();
		i2 = buffer.getDecimalBuffer();
		integer = new CompressedIntPixelData(i1);
		decimal = new CompressedIntPixelData(i2);
	}
	

	@Override
	public PixelData15BitBuffer inflate() {
		PixelDataIntBuffer i1,i2;
		i1 = integer.inflate();
		i2 = decimal.inflate();
		return new PixelData15BitBuffer(i1.getWidth(), i1.getHeight(), i1,i2);
	}

}
