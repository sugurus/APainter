package apainter.rendering.impl.cpu;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer2;
import apainter.rendering.RenderingOption;

public class DefaultRenderer implements Renderer2{

	private static final int calca(int a,int oa){
		return (((a+oa)*255-a*oa+254)*div255shift24>>>24);
	}

	private static final int a(int c){
		return c>>>24;
	}
	private static final int r(int c){
		return c >> 18 &0xff;
	}
	private static final int g(int c){
		return c >> 8 & 0xff;
	}
	private static final int b(int c){
		return c&0xff;
	}
	private static final int argb(int a,int r,int g,int b){
		return a <<24 | r << 16 | g << 8 | b;
	}
	private static final int argb(int a,int rgb){
		return a <<24 | (rgb&0xffffff);
	}

	private static final int pixel(int[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}
	private static final void set(int[] pixel,int val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	private static final void set(int[] pixel,int a,int rgb,int x,int y,int w){
		pixel[x+y*w] = a << 24 | (rgb&0xffffff);
	}

	private static final long pixel(long[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}
	private static final void set(long[] pixel,long val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	private static final void set(byte[] pixel,byte val,int x,int y,int w){
		pixel[x+y*w] = val;
	}
	private static final byte pixel(byte[] pixel,int x,int y,int w){
		return pixel[x+y*w];
	}

	private static final int i(byte b){
		return ((int)b)&0xff;
	}
	private static final int mask(int a,byte b){
		return a*(((int)b)&0xff)/255;
	}

	private static final int ar(long c){
		return (int)(c>>>32);
	}
	private static final int gb(long c){
		return (int)c;
	}
	private static final int _a(int ar){
		return ar >>> 16;
	}
	private static final int _r(int ar){
		return ar & 0xffff;
	}
	private static final int _g(int gb){
		return gb >>> 16;
	}
	private static final int _b(int gb){
		return gb&0xffff;
	}
	private static final int a(long c){
		return (int) (c>>>48);
	}
	private static final int r(long c){
		return (int)(c >>> 32) &0xffff;
	}
	private static final int g(long c){
		return (int)(c) >> 16 & 0xffff;
	}
	private static final int b(long c){
		return (int)(c)&0xffff;
	}
	private static final long argbL(int a,int r,int g,int b){
		return (long)(a <<16 | r)<<32  | (long)((g << 16 | b)&0xffffffffL);
	}

	@Override
	public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if (base instanceof PixelDataIntBuffer) {
			PixelDataIntBuffer intb = (PixelDataIntBuffer) base;
			if (over instanceof PixelDataIntBuffer) {
				PixelDataIntBuffer into = (PixelDataIntBuffer) over;
				if(option.hasMask()){
					rendering_mask(intb, into, p, clip, option);
				}else
					rendering(intb, into, p, clip, option);
			}
		}


	}

	@Override
	public void rendering(PixelDataBuffer base, PixelDataBuffer over,
			PixelDataBuffer over2, Point p, Rectangle clip,
			RenderingOption option) {
		if (base instanceof PixelDataIntBuffer) {
			if (over instanceof PixelDataIntBuffer) {
				if(option.hasMask()&&option.hasMask2())
					rendering_mask_mask((PixelDataIntBuffer)base, (PixelDataIntBuffer)over, (PixelDataIntBuffer)over2, p, clip, option);
				else if(option.hasMask()&&!option.hasMask2())
					rendering_mask((PixelDataIntBuffer)base, (PixelDataIntBuffer)over, (PixelDataIntBuffer)over2, p, clip, option);
				else if(!option.hasMask() && option.hasMask2())
					rendering__mask((PixelDataIntBuffer)base, (PixelDataIntBuffer)over, (PixelDataIntBuffer)over2, p, clip, option);
				else
					rendering((PixelDataIntBuffer)base, (PixelDataIntBuffer)over, (PixelDataIntBuffer)over2, p, clip, option);
			}
		}
	}

	private static final int val(int b,int o,int a1,int oa,int t,int k){
		return ((b*a1 + o * oa+t)*k>>16);
	}


	private void rendering_mask(PixelDataIntBuffer base, PixelDataIntBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		int base_width = base.width,base_height = base.height;
		int over_width = over.width,over_height = over.height;
		int[] basepixel = base.getData(),overpixel = over.getData();
		byte[] maskpixel = option.mask.getData();
		int px = p.x,py=p.y;

		int x,y,w = clip.width+clip.x,h = clip.height+clip.y;
		for(y = clip.y;y<h;y++){
			for(x = clip.x;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = mask(a(oc),pixel(maskpixel,x-px,y-py,over_width));
				int or = r(oc);
				int og = g(oc);
				int ob = b(oc);

				int fa = calca(a, oa);
				if(a !=0){
					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a+oa);
					int t = oa + a1-1;
					set(basepixel,
							argb(fa,
									val(r,or,a1,oa,t,k),
									val(g,og,a1,oa,t,k),
									val(b,ob,a1,oa,t,k)),
						x,y,base_width);
					continue;
				}else{
					set(basepixel,oa,oc,x,y,base_width);
					continue;
				}
			}
		}
	}


	private void rendering(PixelDataIntBuffer base, PixelDataIntBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		int base_width = base.width,base_height = base.height;
		int over_width = over.width,over_height = over.height;
		int[] basepixel = base.getData(),overpixel = over.getData();
		int px = p.x,py=p.y;

		int x,y,w = clip.width+clip.x,h = clip.height+clip.y;
		for(y = clip.y;y<h;y++){
			for(x = clip.x;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = a(oc);
				int or = r(oc);
				int og = g(oc);
				int ob = b(oc);

				int fa = calca(a, oa);
				if(a !=0){
					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a+oa);
					int t = oa + a1-1;
					set(basepixel,
							argb(fa,
									val(r,or,a1,oa,t,k),
									val(g,og,a1,oa,t,k),
									val(b,ob,a1,oa,t,k)),
						x,y,base_width);
					continue;
				}else{
					set(basepixel,oa,oc,x,y,base_width);
					continue;
				}
			}
		}
	}


	private void rendering_mask(PixelDataIntBuffer base, PixelDataIntBuffer over,
			PixelDataIntBuffer over2, Point p, Rectangle clip,
			RenderingOption option) {
		int base_width = base.width,base_height = base.height;
		int over_width = over.width,over_height = over.height;
		int over2_width = over2.width,over2_height = over2.height;
		int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		byte[] maskpixel = option.mask.getData();
		int px = p.x,py=p.y;

		int x,y,w = clip.width+clip.x,h = clip.height+clip.y;
		for(y = clip.y;y<h;y++){
			for(x = clip.x;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = mask(a(oc),pixel(maskpixel,x-px,y-py,over_width));
				int or = r(oc);
				int og = g(oc);
				int ob = b(oc);

				int fa =calca(a, oa);
				int a2 = fa;
				int r2 =0;
				int g2 =0;
				int b2 =0;
				if(a !=0){
					if(fa!=0){
						int a1 = (255-oa > a)? a:255-oa;
						int k = 65536/(a+oa);
						int t = oa + a1-1;
						r2 =val(r,or,a1,oa,t,k);
						g2 =val(g,og,a1,oa,t,k);
						b2 =val(b,ob,a1,oa,t,k);
					}
				}else{
					r2 = or;
					g2 = og;
					b2 = ob;
				}

				int oc2 = pixel(overpixel2,x-px,y-py,over2_width);
				int oa2 = a(oc2);
				int or2 = r(oc2);
				int og2 = g(oc2);
				int ob2 = b(oc2);
				int fa2 = calca(a2, oa2);
				if(a2 !=0){
					int a1 = (255-oa2 > a2)? a2:255-oa2;
					int k = 65536/(a2+oa2);
					int t = oa2 + a1-1;
					set(basepixel,
							argb(fa2,
									val(r2,or2,a1,oa2,t,k),
									val(g2,og2,a1,oa2,t,k),
									val(b2,ob2,a1,oa2,t,k)),
						x,y,base_width);
					continue;
				}else{
					set(basepixel,a2,oc2,x,y,base_width);
					continue;
				}
			}
		}
	}
	private void rendering_mask_mask(PixelDataIntBuffer base, PixelDataIntBuffer over,
			PixelDataIntBuffer over2, Point p, Rectangle clip,
			RenderingOption option) {
		int base_width = base.width,base_height = base.height;
		int over_width = over.width,over_height = over.height;
		int over2_width = over2.width,over2_height = over2.height;
		int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		byte[] maskpixel = option.mask.getData(),
		maskpixel2 = option.mask.getData();
		int px = p.x,py=p.y;

		int x,y,w = clip.width+clip.x,h = clip.height+clip.y;
		for(y = clip.y;y<h;y++){
			for(x = clip.x;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = mask(a(oc),pixel(maskpixel,x-px,y-py,over_width));
				int or = r(oc);
				int og = g(oc);
				int ob = b(oc);

				int fa =calca(a, oa);
				int a2 = fa;
				int r2 =0;
				int g2 =0;
				int b2 =0;
				if(a !=0){
					if(fa!=0){
						int a1 = (255-oa > a)? a:255-oa;
						int k = 65536/(a+oa);
						int t = oa + a1-1;
						r2 =val(r,or,a1,oa,t,k);
						g2 =val(g,og,a1,oa,t,k);
						b2 =val(b,ob,a1,oa,t,k);
					}
				}else{
					r2 = or;
					g2 = og;
					b2 = ob;
				}

				int oc2 = pixel(overpixel2,x-px,y-py,over2_width);
				int oa2 = mask(a(oc2),pixel(maskpixel2,x-px,y-py,over2_width));
				int or2 = r(oc2);
				int og2 = g(oc2);
				int ob2 = b(oc2);
				int fa2 = calca(a2, oa2);
				if(a2 !=0){
					int a1 = (255-oa2 > a2)? a2:255-oa2;
					int k = 65536/(a2+oa2);
					int t = oa2 + a1-1;
					set(basepixel,
							argb(fa2,
									val(r2,or2,a1,oa2,t,k),
									val(g2,og2,a1,oa2,t,k),
									val(b2,ob2,a1,oa2,t,k)),
						x,y,base_width);
					continue;
				}else{
					set(basepixel,a2,oc2,x,y,base_width);
					continue;
				}
			}
		}
	}

	private void rendering__mask(PixelDataIntBuffer base, PixelDataIntBuffer over,
			PixelDataIntBuffer over2, Point p, Rectangle clip,
			RenderingOption option) {
		int base_width = base.width,base_height = base.height;
		int over_width = over.width,over_height = over.height;
		int over2_width = over2.width,over2_height = over2.height;
		int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		byte[] maskpixel2 = option.mask.getData();
		int px = p.x,py=p.y;

		int x,y,w = clip.width+clip.x,h = clip.height+clip.y;
		for(y = clip.y;y<h;y++){
			for(x = clip.x;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = a(oc);
				int or = r(oc);
				int og = g(oc);
				int ob = b(oc);

				int fa =calca(a, oa);
				int a2 = fa;
				int r2 =0;
				int g2 =0;
				int b2 =0;
				if(a !=0){
					if(fa!=0){
						int a1 = (255-oa > a)? a:255-oa;
						int k = 65536/(a+oa);
						int t = oa + a1-1;
						r2 =val(r,or,a1,oa,t,k);
						g2 =val(g,og,a1,oa,t,k);
						b2 =val(b,ob,a1,oa,t,k);
					}
				}else{
					r2 = or;
					g2 = og;
					b2 = ob;
				}

				int oc2 = pixel(overpixel2,x-px,y-py,over2_width);
				int oa2 = mask(a(oc2),pixel(maskpixel2,x-px,y-py,over2_width));
				int or2 = r(oc2);
				int og2 = g(oc2);
				int ob2 = b(oc2);
				int fa2 = calca(a2, oa2);
				if(a2 !=0){
					int a1 = (255-oa2 > a2)? a2:255-oa2;
					int k = 65536/(a2+oa2);
					int t = oa2 + a1-1;
					set(basepixel,
							argb(fa2,
									val(r2,or2,a1,oa2,t,k),
									val(g2,og2,a1,oa2,t,k),
									val(b2,ob2,a1,oa2,t,k)),
						x,y,base_width);
					continue;
				}else{
					set(basepixel,a2,oc2,x,y,base_width);
					continue;
				}
			}
		}
	}

	private void rendering(PixelDataIntBuffer base, PixelDataIntBuffer over,
			PixelDataIntBuffer over2, Point p, Rectangle clip,
			RenderingOption option) {
		int base_width = base.width,base_height = base.height;
		int over_width = over.width,over_height = over.height;
		int over2_width = over2.width,over2_height = over2.height;
		int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		int px = p.x,py=p.y;

		int x,y,w = clip.width+clip.x,h = clip.height+clip.y;
		for(y = clip.y;y<h;y++){
			for(x = clip.x;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = a(oc);
				int or = r(oc);
				int og = g(oc);
				int ob = b(oc);

				int fa = calca(a, oa);
				int a2 = fa;
				int r2 =0;
				int g2 =0;
				int b2 =0;
				if(a !=0){
					if(fa!=0){
						int a1 = (255-oa > a)? a:255-oa;
						int k = 65536/(a+oa);
						int t = oa + a1-1;
						r2 =val(r,or,a1,oa,t,k);
						g2 =val(g,og,a1,oa,t,k);
						b2 =val(b,ob,a1,oa,t,k);
					}
				}else{
					r2 = or;
					g2 = og;
					b2 = ob;
				}

				int oc2 = pixel(overpixel2,x-px,y-py,over2_width);
				int oa2 = a(oc2);
				int or2 = r(oc2);
				int og2 = g(oc2);
				int ob2 = b(oc2);
				int fa2 = calca(a2, oa2);
				if(a2 !=0){
					int a1 = (255-oa2 > a2)? a2:255-oa2;
					int k = 65536/(a2+oa2);
					int t = oa2 + a1-1;
					set(basepixel,
							argb(fa2,
									val(r2,or2,a1,oa2,t,k),
									val(g2,og2,a1,oa2,t,k),
									val(b2,ob2,a1,oa2,t,k)),
						x,y,base_width);
					continue;
				}else{
					set(basepixel,a2,oc2,x,y,base_width);
					continue;
				}
			}
		}
	}

}
