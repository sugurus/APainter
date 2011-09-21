package apainter.data;

import java.awt.Rectangle;

public class PixelData15BitBuffer extends PixelDataBuffer{

	private PixelDataIntBuffer integer,decimal;


	public PixelData15BitBuffer(int w, int h) {
		super(w, h);
		integer =PixelDataIntBuffer.create(w, h);
		decimal = PixelDataIntBuffer.create(w,h);
	}

	public PixelData15BitBuffer(int w,int h,PixelDataIntBuffer integer,PixelDataIntBuffer decimal) {
		super(w,h);
		if(integer.getWidth()!=w || integer.getHeight()!=h||
				decimal.getWidth()!=w || decimal.getHeight()!=h)throw new IllegalArgumentException();
		this.integer = integer;
		this.decimal = decimal;
	}

	@Override
	public PixelData15BitBuffer copy(Rectangle r){
		PixelDataIntBuffer i1,i2;
		i1 = new PixelDataIntBuffer(r.width, r.height, getIntegerBuffer().copy(null, r));
		i2 = new PixelDataIntBuffer(r.width, r.height, getDecimalBuffer().copy(null, r));
		return new PixelData15BitBuffer(r.width, r.height, i1, i2);
	}

	@Override
	public void dispose() {
		integer.dispose();
		decimal.dispose();
	}

	public PixelDataIntBuffer getIntegerBuffer(){
		return integer;
	}

	public int[] getInteger(){
		return integer.getData();
	}

	public PixelDataIntBuffer getDecimalBuffer(){
		return decimal;
	}

	public int[] getDecimal(){
		return decimal.getData();
	}

	@Override
	PixelDataIntBuffer[] getData() {
		return new PixelDataIntBuffer[]{integer,decimal};
	}

	@Override
	public PixelDataBuffer clone() {
		return new PixelData15BitBuffer(width, height, (PixelDataIntBuffer) integer.clone(), (PixelDataIntBuffer) decimal.clone());
	}


	public void setData(PixelData15BitBuffer data,Rectangle bounds){
		integer.setData(data.integer.getData(), bounds);
		decimal.setData(data.decimal.getData(), bounds);
	}


	public static int convine(int integer,int decimal){
		return integer<<7 | decimal;
	}

	public static int integer(int value){
		return value>>>7;
	}

	public static int decimal(int value){
		return value&127;
	}
}
