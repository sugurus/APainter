package apainter.rendering.impl.cpu;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer2;
import apainter.rendering.RenderingOption;

import static apainter.rendering.impl.cpu.UtilR.*;
public class DefaultRenderer implements Renderer2{

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
		final int base_width = base.width;
		final int over_width = over.width;
		final int[] basepixel = base.getData(),overpixel = over.getData();
		final byte[] maskpixel = option.mask.getData();
		final int px = p.x,py=p.y;
		final int layer = option.overlayeralph;
		final int clipx = clip.x;
		final int w = clip.width+clipx,h = clip.height+clip.y;

		for(int x,y = clip.y;y<h;y++){
			for(x = clipx;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = layeralph(mask(a(oc),pixel(maskpixel,x-px,y-py,over_width)),layer);
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
		final int base_width = base.width;
		final int over_width = over.width;
		final int[] basepixel = base.getData(),overpixel = over.getData();
		final int px = p.x,py=p.y;
		final int layer = option.overlayeralph;
		final int clipx = clip.x;
		final int w = clip.width+clipx,h = clip.height+clip.y;

		for(int x,y = clip.y;y<h;y++){
			for(x = clipx;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = layeralph(pixel(overpixel,x-px,y-py,over_width),layer);
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
		final int base_width = base.width;
		final int over_width = over.width;
		final int over2_width = over2.width;
		final int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		final byte[] maskpixel = option.mask.getData();
		final int px = p.x,py=p.y;
		final int layer = option.overlayeralph,layer2=option.over2layeralph;
		final int clipx = clip.x;
		final int w = clip.width+clipx,h = clip.height+clip.y;

		for(int x,y = clip.y;y<h;y++){
			for(x = clipx;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = layeralph(mask(a(oc),pixel(maskpixel,x-px,y-py,over_width)),layer);
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
				int oa2 = layeralph(a(oc2),layer2);
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
		final int base_width = base.width;
		final int over_width = over.width;
		final int over2_width = over2.width;
		final int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		final byte[] maskpixel = option.mask.getData(),
		maskpixel2 = option.mask.getData();
		final int px = p.x,py=p.y;
		final int layer = option.overlayeralph,layer2=option.over2layeralph;
		final int clipx = clip.x;
		final int w = clip.width+clipx,h = clip.height+clip.y;

		for(int x,y = clip.y;y<h;y++){
			for(x = clipx;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = layeralph(mask(a(oc),pixel(maskpixel,x-px,y-py,over_width)),layer);
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
				int oa2 = layeralph(mask(a(oc2),pixel(maskpixel2,x-px,y-py,over2_width)),layer2);
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
		final int base_width = base.width;
		final int over_width = over.width;
		final int over2_width = over2.width;
		final int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		final byte[] maskpixel2 = option.mask.getData();
		final int px = p.x,py=p.y;
		final int layer = option.overlayeralph,layer2=option.over2layeralph;
		final int clipx = clip.x;
		final int w = clip.width+clipx,h = clip.height+clip.y;

		for(int x,y = clip.y;y<h;y++){
			for(x = clipx;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = layeralph(a(oc),layer);
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
				int oa2 = layeralph(mask(a(oc2),pixel(maskpixel2,x-px,y-py,over2_width)),layer2);
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
		final int base_width = base.width;
		final int over_width = over.width;
		final int over2_width = over2.width;
		final int[] basepixel = base.getData(),
		overpixel = over.getData(),
		overpixel2 = over2.getData();
		final int px = p.x,py=p.y;
		final int layer = option.overlayeralph,layer2=option.over2layeralph;
		final int clipx = clip.x;
		final int w = clip.width+clipx,h = clip.height+clip.y;

		for(int x,y = clip.y;y<h;y++){
			for(x = clipx;x<w;x++){
				int c = pixel(basepixel,x,y,base_width);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = layeralph(a(oc),layer);
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
				int oa2 = layeralph(a(oc2),layer2);
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
