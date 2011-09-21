package apainter.misc;

import apainter.Color;

public final class Utility_PixelFunction {
	public static final int div255shift23 = 3289;//((1<<23))/255
	public static final int div255shift24 = 65794;//((1<<24))/255
	public static final int ClearColor = Color.ClearColor;
	public static final int NullColor = Color.NotColor;

	public static final int calca(int a,int oa){
		return a+oa-((a*oa)*div255shift24>>>24);
	}

	public static final int calca15bit(int a,int oa){
		return a+oa-(a*oa/32767);
	}

	public static final int layeralph(int a,int layer){
		return a*layer>>8;
	}

	public static final int a(int c){
		return c>>>24;
	}
	public static final int r(int c){
		return c >> 16 &0xff;
	}
	public static final int g(int c){
		return c >> 8 & 0xff;
	}
	public static final int b(int c){
		return c&0xff;
	}
	public static final int argb(int a,int r,int g,int b){
		return a <<24 | r << 16 | g << 8 | b;
	}
	public static final int argb(int a,int rgb){
		return a <<24 | (rgb&0xffffff);
	}

	public static final int pixel(int[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}
	public static final void set(int[] pixel,int val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	public static final void set(int[] pixel,int a,int rgb,int x,int y,int w){
		pixel[x+y*w] = a << 24 | (rgb&0xffffff);
	}

	public static final long pixel(long[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}
	public static final void set(long[] pixel,long val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	public static final void set(byte[] pixel,byte val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	public static final int pixel(byte[] pixel,int x,int y,int w){
		return pixel[x+y*w]&0xff;
	}

	public static final int i(byte b){
		return b&0xff;
	}
	public static final int mask(int a,byte b){
		return a*(b&0xff)/255;
	}
	public static final int mask(int a,int b){
		return a*b/255;
	}

	public static final int ar(long c){
		return (int)(c>>>32);
	}
	public static final int gb(long c){
		return (int)c;
	}
	public static final int _a(int ar){
		return ar >>> 16;
	}
	public static final int _r(int ar){
		return ar & 0xffff;
	}
	public static final int _g(int gb){
		return gb >>> 16;
	}
	public static final int _b(int gb){
		return gb&0xffff;
	}
	public static final int a(long c){
		return (int) (c>>>48);
	}
	public static final int r(long c){
		return (int)(c >>> 32) &0xffff;
	}
	public static final int g(long c){
		return (int)(c) >> 16 & 0xffff;
	}
	public static final int b(long c){
		return (int)(c)&0xffff;
	}
	public static final long argbL(int a,int r,int g,int b){
		return (long)(a <<16 | r)<<32  | ((g << 16 | b)&0xffffffffL);
	}
}
