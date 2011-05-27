package apainter.color;

import java.util.ArrayList;

/**
 * ARGBの順に格納されている。<br>
 * A透明度<br>
 * RGB　sRGBと同様
 *
 * @author nodamushi
 *
 */
public class Color {
	private long colorlong=0;
	private int colorint=0;


	private ArrayList<ColorListner> lis = new ArrayList<ColorListner>();

	public void addColorListner(ColorListner l) {
		if (!lis.contains(l))
			lis.add(l);
	}

	public void removeColorListner(ColorListner l) {
		lis.remove(l);
	}

	/**
	 * 色要素の値が255の時のみ、16bitでの値を0xffffとし、それ以外の場合下位8bitは0になります。
	 * @param argb
	 */
	public void setARGB(int argb){
		int a = argb>>>24;
		if(a==0)argb = 0;
		colorint = argb;
		int r = argb>>16&0xff;
		int g = argb>>8&0xff;
		int b = argb &0xff;
		a <<=8;
		r <<=8;
		g <<=8;
		b <<=8;
		if(a==0xff00)a=0xffff;
		if(r==0xff00)r=0xffff;
		if(g==0xff00)g=0xffff;
		if(b==0xff00)b=0xffff;
		colorlong = (long)a << 48 | (long)r << 32 | g << 16 | b;
		for(ColorListner l:lis)l.colorChanged(this);
	}

	public void setARGB(int a,int r,int g,int b){
		setARGB(a<<24 | r << 16|g<<8|b);
	}

	public void set16bitARGB(long argb){
		int a = (int) (argb>>>48);
		if(a==0)argb = 0;
		colorlong = argb;
		int r = (int) (argb>>32&0xffff);
		int g = (int) (argb>>16&0xffff);
		int b = (int) (argb &0xffff);
		a >>=8;
		r >>=8;
		g >>=8;
		b >>=8;
		colorint =a<<24 | r << 16|g<<8|b;
		for(ColorListner l:lis)l.colorChanged(this);
	}

	public void set16bitARGB(int a,int r,int g,int b){
		set16bitARGB((long)a << 48 | (long)r << 32 | g << 16 | b);
	}



	public int getARGB(){
		return colorint;
	}

	public long get16bitARGB(){
		return colorlong;
	}

	public int getA(){
		return colorint>>>24;
	}
	public int get16bitA(){
		return (int) (colorlong >>>48);
	}
	public int getR(){
		return colorint>>16&0xff;
	}
	public int get16bitR(){
		return (int)(colorlong>>>32&0xffff);
	}

	public int getG(){
		return colorint>>8&0xff;
	}
	public int get16bitG(){
		return (int)(colorlong>>>16&0xffff);
	}
	public int getB(){
		return colorint&0xff;
	}
	public int get16bitB(){
		return (int)(colorlong&0xffff);
	}

	public java.awt.Color toAwtColor(){
		return new java.awt.Color(colorint, true);
	}
}
