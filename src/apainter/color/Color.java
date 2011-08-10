package apainter.color;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import apainter.bind.annotation.BindProperty;
import apainter.misc.Util;
import static apainter.PropertyChangeNames.*;
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
	private long colorlong=0;
	private int colorint=0;


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
		colorint = c.colorint;
		colorlong = c.colorlong;
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
	 @BindProperty(ColorPropertyChange)
	 public void setARGB(int argb){
		 if(colorint==argb)return;
		 int oldValue = colorint;
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
		 firePropertyChange(ColorPropertyChange, oldValue, colorint);
	 }

	 public void setARGB(int a,int r,int g,int b){
		 setARGB(a<<24 | r << 16|g<<8|b);
	 }

	 @BindProperty(LongColorPropertyChange)
	 public void set16bitARGB(long argb){
		 if(argb == colorlong)return;
		 long oldValue = colorlong;
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
		 firePropertyChange(LongColorPropertyChange, oldValue, colorlong);
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




	private EventListenerList listener = new EventListenerList();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listener.remove(PropertyChangeListener.class, l);
		listener.add(PropertyChangeListener.class, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listener.remove(PropertyChangeListener.class, l);
	}

	public void firePropertyChange(String name, Object oldValue, Object newValue) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue,
				newValue);

		Object[] listeners = this.listener.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}

}
