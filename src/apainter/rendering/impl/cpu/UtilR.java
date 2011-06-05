package apainter.rendering.impl.cpu;

import apainter.color.Color;

class UtilR {
	static final int div255shift23 = 32896;//(1<<23)/255
	static final int div255shift24 = 65793;//(1<<24)/255
	static final int ClearColor = Color.ClearColor;
	static final int NullColor = Color.NotColor;

	static final int calca(int a,int oa){
		return (((a+oa)*255-a*oa+254)*div255shift24>>>24);
	}

	static final int layeralph(int a,int layer){
		return a*layer>>8;
	}

	static final int a(int c){
		return c>>>24;
	}
	static final int r(int c){
		return c >> 16 &0xff;
	}
	static final int g(int c){
		return c >> 8 & 0xff;
	}
	static final int b(int c){
		return c&0xff;
	}
	static final int argb(int a,int r,int g,int b){
		return a <<24 | r << 16 | g << 8 | b;
	}
	static final int argb(int a,int rgb){
		return a <<24 | (rgb&0xffffff);
	}

	static final int pixel(int[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}
	static final void set(int[] pixel,int val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	static final void set(int[] pixel,int a,int rgb,int x,int y,int w){
		pixel[x+y*w] = a << 24 | (rgb&0xffffff);
	}

	static final long pixel(long[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}
	static final void set(long[] pixel,long val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	static final void set(byte[] pixel,byte val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	static final byte pixel(byte[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}

	static final int i(byte b){
		return b&0xff;
	}
	static final int mask(int a,byte b){
		return a*(b&0xff)/255;
	}

	static final int ar(long c){
		return (int)(c>>>32);
	}
	static final int gb(long c){
		return (int)c;
	}
	static final int _a(int ar){
		return ar >>> 16;
	}
	static final int _r(int ar){
		return ar & 0xffff;
	}
	static final int _g(int gb){
		return gb >>> 16;
	}
	static final int _b(int gb){
		return gb&0xffff;
	}
	static final int a(long c){
		return (int) (c>>>48);
	}
	static final int r(long c){
		return (int)(c >>> 32) &0xffff;
	}
	static final int g(long c){
		return (int)(c) >> 16 & 0xffff;
	}
	static final int b(long c){
		return (int)(c)&0xffff;
	}
	static final long argbL(int a,int r,int g,int b){
		return (long)(a <<16 | r)<<32  | ((g << 16 | b)&0xffffffffL);
	}
}
