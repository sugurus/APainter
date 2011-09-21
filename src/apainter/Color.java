package apainter;

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

	private double a,r,g,b;

	GlobalValue gv;
	String propertyName;

	public final BindObject bindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			double[] fs = (double[])value;
			a = fs[0];
			r = fs[1];
			g = fs[2];
			b = fs[3];
		}

		@Override
		public Object get() {
			return new double[]{a,r,g,b};
		}

		public Object convert(Object o) {
			if(o instanceof float[]){
				double[] d = new double[4];
				float[] f = (float[])o;
				d[0] = f[0];
				d[1] = f[1];
				d[2] = f[2];
				d[3] = f[3];
				return d;
			}
			if(o instanceof int[]){
				double[] d = new double[4];
				int[] i = (int[])o;
				d[0] = i[0]/255d;
				d[1] = i[1]/255d;
				d[2] = i[2]/255d;
				d[3] = i[3]/255d;
				return d;
			}
			return o;
		}

		public boolean isSettable(Object obj) {
			if(obj instanceof double[]){
				double[] fs = (double[])obj;
				if(fs.length >= 4){
					for(int i=0;i<4;i++){
						if(fs[i]>1d || fs[i] < 0d)return false;
					}
					return true;
				}return false;
			}
			if(obj instanceof float[]){
				float[] fs = (float[])obj;
				if(fs.length >= 4){
					for(int i=0;i<4;i++){
						if(fs[i]>1f || fs[i] < 0f)return false;
					}
					return true;
				}return false;
			}
			if(obj instanceof int[]){
				int[] fs = (int[])obj;
				if(fs.length >= 4){
					for(int i=0;i<4;i++){
						if(fs[i]>255 || fs[i] < 0)return false;
					}
					return true;
				}return false;
			}else return false;


		}

		@Override
		public void setend(Object oldobj, Object newobj) {
			if(gv!=null){
				gv.firePropertyChange(propertyName, oldobj, newobj, Color.this);
			}
		}
	};


	public static int RGB2YCrCb(int argb){
		int a = argb&0xff000000;
		double r = (argb>>16&0xff);
		double g = (argb>>8 &0xff);
		double b = (argb & 0xff);
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
		double y = (aycrcb>>16&0xff);
		double cr = (aycrcb>>8 &0xff)-128;
		double cb = (aycrcb & 0xff)-128;
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
	 * hsv値から、rgbを計算します
	 * @param h 0～1の値の色相
	 * @param s 0～1の値の彩度　
	 * @param v 0～1の値の明度
	 * @return 0～1の範囲の値のrgbが入った配列
	 */
	public static double[] HSV2RGB(double h,double s,double v){
		h = h*360;
		 double hmod =  Util.mod(h, 6);
		 int hi = (int)hmod;
		 double f = hmod-hi;
		 double p,q,t,V;
		 p = v*(1-s);
		 q = v*(1-f*s);
		 t = v*(1-(1-f)*s);
		 V = v;
		 double r,g,b;
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
		 return new double[] {r,g,b};
	}

	/**
	 * 正規化されたhsvデータから色を設定します。透明度は変わりません。
	 * @param h
	 * @param s
	 * @param v
	 */
	 public void setHSV(double h,double s,double v){
		 double[] rgb = HSV2RGB(h, s, v);
		 setARGB(a, rgb[0], rgb[1], rgb[2]);
	 }

	 /**
	  * RGBからHSVを計算します
	  * @see http://ja.wikipedia.org/wiki/HSV色空間
	  * @return 順にH,S,Vの値が0～1の範囲で格納されています。
	  */
	 public static double[] RGB2HSV(double r,double g,double b){
		 double h,s,v;
		 double max,min;
		 byte maxpos;
		 {
			 double[] m = Util.max_min(r,g,b);
			 max = m[0];min = m[1];
			 if(max==r)maxpos = 0;
			 else if(max==g)maxpos=1;
			 else maxpos = 2;
		 }
		 switch(maxpos){
		 case 0://if R
			 h = ((g-b))/(max-min)/6d;
			 break;
		 case 1://if G
			 h = (((b-r))/(max-min)+2d)/6d;
			 break;
		 default://if B
			 h = (((r-g))/(max-min)+4d)/6d;
			 break;
		 }
		 h = Util.mod(h, 1);
		 s = (max-min)/max;
		 v = max;
		 return new double[]{h,s,v};
	 }

	 /**
	  * RGBからHSVを計算します
	  * @see http://ja.wikipedia.org/wiki/HSV色空間
	  * @return 順にH,S,Vの値が0～1の範囲で格納されています。
	  */
	 public double[] getHSV(){
		 return RGB2HSV(r, g, b);
	 }

	 @Override
	 public Color clone() {
		 return new Color(this);
	 }


	 /**
	  * 色を設定します。
	  * @param argb 各チャネル8bitでargbの順に値が入っている物と見なす。
	  */
	 public void setARGB(int argb){
		 int a,r,g,b;
		 a = Utility_PixelFunction.a(argb);
		 r = Utility_PixelFunction.r(argb);
		 g = Utility_PixelFunction.g(argb);
		 b = Utility_PixelFunction.b(argb);
		 setARGB(a, r, g, b);
	 }
	 /**
	  * 値は0～255の8bitで表現されている物とする。
	  * @param a
	  * @param r
	  * @param g
	  * @param b
	  */
	 public void setARGB(int a,int r,int g,int b){
		 setARGB(a/255d,r/255d,g/255d,b/255d);
	 }

	 /**
	  * 色を設定します。
	  * @param argb 各チャネル16bitでargbの順に値が入っている物と見なす。
	  */
	 public void set16bitARGB(long argb){
		 int a,r,g,b;
		 a = Utility_PixelFunction.a(argb);
		 r = Utility_PixelFunction.r(argb);
		 g = Utility_PixelFunction.g(argb);
		 b = Utility_PixelFunction.b(argb);
		 set16bitARGB(a, r, g, b);
	 }
	 /**
	  * 値は0～65535の16bitで表現されている物とする。
	  * @param a
	  * @param r
	  * @param g
	  * @param b
	  */
	 public void set16bitARGB(int a,int r,int g,int b){
		 setARGB(a/65535d,r/65535d,g/65535d,b/65535d);
	 }

	 /**
	  * 各値は0～1の間であるとする。<br>
	  * 範囲を超える値は、最も範囲に近い値に変更される。
	  * @param a
	  * @param r
	  * @param g
	  * @param b
	  */
	 public void setARGB(double a,double r,double g,double b){
		 if(a < 0)a = 0;
		 else if(a > 1)a = 1;
		 if(r < 0)r = 0;
		 else if (r>1)r = 1;
		 if(g < 0)g = 0;
		 else if(g > 1)g = 1;
		 if(b < 0)b = 0;
		 else if (b>1)b = 1;
		 double[] d = {a,r,g,b};
		 bindObject.set(d);
	 }



	 public int getARGB(){
		 return getA()<<24 | getR() << 16 | getG() << 8 | getB();
	 }

	 public long get16bitARGB(){
		 return  (long)(get16bitA()<<16 | get16bitR())<<32 | (get16bitG() << 16 | get16bitB());
	 }

	 /**
	  * 0～255の範囲で値を返します。
	  * @return
	  */
	 public int getA(){
		 return (int) (a*255);
	 }
	 public int get16bitA(){
		 return (int) (a*65535);
	 }
	 /**
	  * 最大値をmaxValueとして不透明度の値を返します。
	  * @param maxValue
	  * @return 0～maxValueの間の値を取る不透明度の値
	  */
	 public int getA(int maxValue){
		 return (int)(a*maxValue);
	 }
	 /**
	  * 不透明度を0~1の範囲で返します。
	  * @return
	  */
	 public double getA_double(){
		 return a;
	 }
	 /**
	  * 0～255の範囲で値を返します。
	  * @return
	  */
	 public int getR(){
		 return (int) (r*255);
	 }
	 public int get16bitR(){
		 return (int) (r*65535);
	 }

	 /**
	  * 最大値をmaxValueとして赤の値を返します。
	  * @param maxValue
	  * @return 0～maxValueの間の値を取る赤の値
	  */
	 public int getR(int maxValue){
		 return (int)(r*maxValue);
	 }

	 /**
	  * 値を0~1の範囲で返します。
	  * @return
	  */
	 public double getR_double(){
		 return r;
	 }

	 /**
	  * 0～255の範囲で値を返します。
	  * @return
	  */
	 public int getG(){
		 return (int) (g*255);
	 }
	 /**
	  * 最大値をmaxValueとして緑の値を返します。
	  * @param maxValue
	  * @return 0～maxValueの間の値を取る緑の値
	  */
	 public int getG(int maxValue){
		 return (int)(g*maxValue);
	 }
	 /**
	  * 値を0~1の範囲で返します。
	  * @return
	  */
	 public double getG_double(){
		 return g;
	 }
	 public int get16bitG(){
		 return (int) (g*65535);
	 }
	 /**
	  * 0～255の範囲で値を返します。
	  * @return
	  */
	 public int getB(){
		 return (int) (b*255);
	 }
	 /**
	  * 最大値をmaxValueとして青の値を返します。
	  * @param maxValue
	  * @return 0～maxValueの間の値を取る青の値
	  */
	 public int getB(int maxValue){
		 return (int)(b*maxValue);
	 }

	 public int get16bitB(){
		 return (int) (b*65535);
	 }
	 /**
	  * 値を0~1の範囲で返します。
	  * @return
	  */
	 public double getB_double(){
		 return b;
	 }

	 public java.awt.Color toAwtColor(){
		 return new java.awt.Color((float)r, (float)g, (float)b, (float)a);
	 }
}
