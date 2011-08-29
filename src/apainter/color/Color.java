package apainter.color;

import java.util.Arrays;

import apainter.bind.BindObject;
import apainter.misc.Util;
import apainter.misc.Utility_PixelFunction;
/**
 * ARGBの順に格納されている。<br>
 * A透明度<br>
 * RGB　sRGBと同様
 *
 * @author nodamushi
 *
 */
public class Color implements Cloneable{
	/**
	 * 無色透明を表すargb値
	 */
	public static final int ClearColor = 0;
	/**
	 * 色がないという情報を表すargb値
	 */
	public static final int NotColor = 1;

	private float a,r,g,b;
	public final BindObject bindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			float[] fs = (float[])value;
			a = fs[0];
			r = fs[1];
			g = fs[2];
			b = fs[3];
		}

		@Override
		public Object get() {
			return new float[]{a,r,g,b};
		}

		public boolean isSettable(Object obj) {
			if(obj instanceof float[]){
				float[] fs = (float[])obj;
				if(fs.length >= 4){
					if(fs[0] > 1)fs[0] = 1;
					else if(fs[0] < 0) fs[0] = 0;
					if(fs[1] > 1)fs[1] = 1;
					else if(fs[1] < 0) fs[1] = 0;
					if(fs[2] > 1)fs[2] = 1;
					else if(fs[2] < 0) fs[2] = 0;
					if(fs[3] > 1)fs[3] = 1;
					else if(fs[3] < 0) fs[3] = 0;
					return true;
				}return false;
			}else return false;


		}
	};


	public static int RGB2YCrCb(int argb){
		int a = argb&0xff000000;
		int r = argb>>16&0xff;
		int g = argb>>8 &0xff;
		int b = argb & 0xff;
		int y,cr,cb;
		y=(int) (0.299*r+0.587*g+0.114*b);
		cr=(int) (0.5*r-0.419*g-0.081*b+128);
		cb=(int) (-0.169*r-0.332*g+0.5*b+128);

		if (y<0)
			y=0;
		else if (y>255)
			y=255;

		if (cr<0)
			cr=0;
		else if (cr>255)
			cr=255;

		if (cb<0)
			cb=0;
		else if (cb>255)
			cb=255;
		return a | y<<16 | cr << 8 | cb;
	}
	public static int YCrCb2RGB(int aycrcb){
		int a = aycrcb&0xff000000;
		int y = aycrcb>>16&0xff;
		int cr = (aycrcb>>8 &0xff)-128;
		int cb = (aycrcb & 0xff)-128;
		int r,g,b;
		r=(int) (y+1.402*cr);
		g=(int) (y-0.714*cr-0.344*cb);
		b=(int) (y+1.772*cb);

		if (r<0)
			r=0;
		else if (r>255)
			r=255;

		if (g<0)
			g=0;
		else if (g>255)
			g=255;

		if (b<0)
			b=0;
		else if (b>255)
			b=255;
		return a | r << 16| g << 8 | b;
	}

	public Color() {}
	public Color(int argb){
		setARGB(argb);
	}
	public Color(long argb){
		set16bitARGB(argb);
	}
	public Color(Color c){
		a = c.a;
		r = c.r;
		g = c.g;
		b = c.b;
	}

	@Override
	public String toString() {
		int a = getA();
		int r = getR();
		int g = getG();
		int b = getB();
		return String.format("alpha:%d, red:%d, green:%d, blue:%d", a,r,g,b);
	}

	public void setHSV(double[] hsv){
		this.setHSV(hsv[0], hsv[1], hsv[2]);
	}

	/**
	 * 正規化されたhsvデータから色を設定します。透明度は変わりません。
	 * @param h
	 * @param s
	 * @param v
	 */
	 public void setHSV(double h,double s,double v){
		 h = h*360;
		 double hmod =  Util.mod(h, 6);
		 int hi = (int)hmod;
		 double f = hmod-hi;
		 int p,q,t,V;
		 p = (int) (v*(1-s)*0xffff);
		 q = (int) (v*(1-f*s)*0xffff);
		 t = (int) (v*(1-(1-f)*s)*0xffff);
		 V = (int) (v*0xffff);
		 int r,g,b;
		 switch(hi){
		 case 0:
			 r = V;
			 g = t;
			 b = p;
			 break;
		 case 1:
			 r = q;
			 g = V;
			 b = p;
			 break;
		 case 2:
			 r =p;
			 g = V;
			 b = t;
			 break;
		 case 3:
			 r = p;
			 g = q;
			 b = V;
			 break;
		 case 4:
			 r = t;
			 g = p;
			 b = V;
			 break;
		 default:
			 r = V;
			 g = p;
			 b = q;
		 }
		 set16bitARGB(get16bitA(), r, g, b);
	 }

	 /**
	  * 正規化されたhsvデータを返します。hも０～１
	  * @see http://ja.wikipedia.org/wiki/HSV色空間
	  * @return
	  */
	 public double[] getHSV(){
		 double h,s,v;
		 int r,g,b;
		 int max,min,maxpos;
		 {
			 int[] m = Util.max_min(r=get16bitR(),g=get16bitG(),b=get16bitB());
			 max = m[0];min = m[1];
			 if(max==r)maxpos = 0;
			 else if(max==g)maxpos=1;
			 else maxpos = 2;
		 }
		 switch(maxpos){
		 case 0:
			 h = 60d*((g-b))/(max-min);
			 break;
		 case 1:
			 h = 60d*((b-r))/(max-min)+120d;
			 break;
		 default:
			 h = 60d*((r-g))/(max-min)+240d;
			 break;
		 }
		 h /= 360;
		 h = Util.mod(h, 1);
		 s = (double)(max-min)/max;
		 v = max/(double)0xffff;
		 return new double[]{h,s,v};
	 }

	 @Override
	 public Color clone() {
		 return new Color(this);
	 }


	 /**
	  * 色要素の値が255の時のみ、16bitでの値を0xffffとし、それ以外の場合下位8bitは0になります。
	  * @param argb
	  */
	 public void setARGB(int argb){
		 int a,r,g,b;
		 a = Utility_PixelFunction.a(argb);
		 r = Utility_PixelFunction.r(argb);
		 g = Utility_PixelFunction.g(argb);
		 b = Utility_PixelFunction.b(argb);
		 setARGB(a, r, g, b);
	 }

	 public void setARGB(int a,int r,int g,int b){
		 float[] f = {a/255f,r/255f,g/255f,b/255f};
		 bindObject.set(f);
	 }

	 public void set16bitARGB(long argb){
		 int a,r,g,b;
		 a = Utility_PixelFunction.a(argb);
		 r = Utility_PixelFunction.r(argb);
		 g = Utility_PixelFunction.g(argb);
		 b = Utility_PixelFunction.b(argb);
		 set16bitARGB(a, r, g, b);
	 }

	 public void set16bitARGB(int a,int r,int g,int b){
		 float[] f = {a/65535f,r/65535f,g/65535f,b/65535f};
		 bindObject.set(f);
	 }



	 public int getARGB(){
		 return getA()<<24 | getR() << 16 | getG() << 8 | getB();
	 }

	 public long get16bitARGB(){
		 return  (long)(get16bitA()<<16 | get16bitR())<<32 | (get16bitG() << 16 | get16bitB());
	 }

	 public int getA(){
		 return (int) (a*255);
	 }
	 public int get16bitA(){
		 return (int) (a*65535);
	 }
	 public int getR(){
		 return (int) (r*255);
	 }
	 public int get16bitR(){
		 return (int) (r*65535);
	 }

	 public int getG(){
		 return (int) (g*255);
	 }
	 public int get16bitG(){
		 return (int) (g*65535);
	 }
	 public int getB(){
		 return (int) (b*255);
	 }
	 public int get16bitB(){
		 return (int) (b*65535);
	 }

	 public java.awt.Color toAwtColor(){
		 return new java.awt.Color(r, g, b, a);
	 }
}
