package apainter.data;

import java.awt.Rectangle;

public class PixelData15BitGray extends PixelData{

	public static final int max15bitValue=0x7FFF;
	public static final byte maxInteger = (byte)0xff;
	public static final byte maxDecimal = (byte)127;
	private PixelDataByte integer,decimal;
	
	public PixelData15BitGray(int w, int h,byte[] integer,byte[] decimal) {
		super(w, h);
		this.integer = new PixelDataByte(w,h,integer);
		this.decimal = new PixelDataByte(w,h,decimal);
	}
	
	public PixelData15BitGray(int w,int h){
		super(w,h);
		integer = PixelDataByte.create(w, h);
		decimal = PixelDataByte.create(w,h);
	}
	
	public PixelData15BitGray(int w,int h,PixelDataByte integer,PixelDataByte decimal) {
		super(w,h);
		if(integer.getWidth()!=w || integer.getHeight()!=h||
				decimal.getWidth()!=w || decimal.getHeight()!=h)throw new IllegalArgumentException();
		this.integer = integer;
		this.decimal = decimal;
	}

	@Override
	public void dispose() {
		integer.dispose();
		decimal.dispose();
	}

	@Override
	PixelDataByte[] getData() {
		return new PixelDataByte[]{integer,decimal};
	}

	@Override
	public PixelData15BitGray clone() {
		return new PixelData15BitGray(width, height,
				integer.clone(), decimal.clone());
	}

	@Override
	public PixelData copy(Rectangle r) {
		PixelDataByte i1,i2;
		i1 = new PixelDataByte(r.width, r.height, getIntegerBuffer().copy((byte[])null, r));
		i2 = new PixelDataByte(r.width, r.height, getDecimalBuffer().copy((byte[])null, r));
		return new PixelData15BitGray(r.width, r.height, i1, i2);
	}
	
	public PixelDataByte getIntegerBuffer(){
		return integer;
	}

	public byte[] getInteger(){
		return integer.getData();
	}

	public PixelDataByte getDecimalBuffer(){
		return decimal;
	}

	public byte[] getDecimal(){
		return decimal.getData();
	}

	@Override
	public ColorType getColorType() {
		return ColorType.GRAY;
	}

	public static int convine(byte integer,byte decimal){
		return (integer&0xff)<<7 | (decimal&0xff);
	}

	public static byte integer(int value){
		return (byte)(value>>>7);
	}

	public static byte decimal(int value){
		return (byte)(value&127);
	}
	
}
