package apainter.data;

import java.awt.Rectangle;
/**
 * 各チャネル15bit長のカラーデータを表します。<br>
 * このPixelDataはデータをinteger(上位8bitのデータをもつPixeldataInt)、
 * decimal(下部7bitのデータを持つPixeldataInt)の2つに分けて保持します。
 * @author nodamushi
 *
 */
public class PixelData15BitColor extends PixelData{

	public static final int max15bitValue=0x7FFF;
	private PixelDataInt integer,decimal;


	public PixelData15BitColor(int w, int h) {
		super(w, h);
		integer =PixelDataInt.create(w, h);
		decimal = PixelDataInt.create(w,h);
	}

	@Override
	public ColorType getColorType() {
		return ColorType.ARGB;
	}

	public PixelData15BitColor(int w,int h,int[] integer,int[] decimal){
		super(w,h);
		this.integer = new PixelDataInt(w, h, integer);
		this.decimal = new PixelDataInt(w, h, decimal);
	}

	public PixelData15BitColor(int w,int h,PixelDataInt integer,PixelDataInt decimal) {
		super(w,h);
		if(integer.getWidth()!=w || integer.getHeight()!=h||
				decimal.getWidth()!=w || decimal.getHeight()!=h)throw new IllegalArgumentException();
		this.integer = integer;
		this.decimal = decimal;
	}

	@Override
	public PixelData15BitColor copy(Rectangle r){
		PixelDataInt i1,i2;
		i1 = new PixelDataInt(r.width, r.height, getIntegerBuffer().copy(null, r));
		i2 = new PixelDataInt(r.width, r.height, getDecimalBuffer().copy(null, r));
		return new PixelData15BitColor(r.width, r.height, i1, i2);
	}

	@Override
	public void dispose() {
		integer.dispose();
		decimal.dispose();
	}

	public PixelDataInt getIntegerBuffer(){
		return integer;
	}

	public int[] getInteger(){
		return integer.getData();
	}

	public PixelDataInt getDecimalBuffer(){
		return decimal;
	}

	public int[] getDecimal(){
		return decimal.getData();
	}

	@Override
	PixelDataInt[] getData() {
		return new PixelDataInt[]{integer,decimal};
	}

	@Override
	public PixelData clone() {
		return new PixelData15BitColor(width, height, (PixelDataInt) integer.clone(), (PixelDataInt) decimal.clone());
	}


	public void setData(PixelData15BitColor data,Rectangle bounds){
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
