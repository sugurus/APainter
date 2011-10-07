package apainter.rendering.impl.cpu;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.*;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

import static apainter.rendering.ColorOperations.*;
import static apainter.misc.Utility_PixelFunction.*;
public class SubtractiveRenderer implements Renderer{

	@Override
	public void rendering(PixelData base, PixelDataContainer over, Point p,
			Rectangle clip, RenderingOption option) {
		if (base instanceof PixelDataInt) {
			PixelDataInt intb = (PixelDataInt) base;
			if (over.getPixelData() instanceof PixelDataInt) {
				PixelDataInt into = (PixelDataInt) over.getPixelData();
				if(option.hasSourceMask()){
					rendering_mask(intb, into, p, clip, option);
				}else
					rendering(intb, into, p, clip, option);
			}
		}


	}

	private void rendering_mask(PixelDataInt base, PixelDataInt over, Point p,
			Rectangle clip, RenderingOption option) {

		final int base_width = base.width;
		final int over_width = over.width;
		final int[] basepixel = base.getData(),overpixel = over.getData();
		final byte[] maskpixel = ((PixelDataByte)option.sourcemask).getData();
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

				if(a !=0){
					set(basepixel,subtractiveOp(a, r, g, b, oa, or, og, ob),x,y,base_width);
					continue;
				}else{
					set(basepixel,oa,oc,x,y,base_width);
					continue;
				}
			}
		}
	}


	private void rendering(PixelDataInt base, PixelDataInt over, Point p,
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

				if(a !=0){
					set(basepixel,subtractiveOp(a, r, g, b, oa, or, og, ob),x,y,base_width);
					continue;
				}else{
					set(basepixel,oa,oc,x,y,base_width);
					continue;
				}
			}
		}
	}
}
