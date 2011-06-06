package apainter.rendering.impl.cpu;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

import static apainter.rendering.impl.cpu.UtilR.*;
public class DefaultRenderer implements Renderer{

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
					int k = 65536/(a1+oa);
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

				int oc = pixel(overpixel,x-px,y-py,over_width);
				int oa = layeralph(a(oc),layer);
				int or = r(oc);
				int og = g(oc);
				int ob = b(oc);

				int fa = calca(a, oa);
				if(a !=0){
					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a1+oa);
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

}


