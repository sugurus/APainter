package apainter.drawer.painttool;

import static apainter.misc.Utility_PixelFunction.*;
import static apainter.rendering.ColorOperations.*;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelData15BitColor;
import apainter.data.PixelData15BitGray;
import apainter.data.PixelDataByte;
import apainter.data.PixelDataInt;
import apainter.rendering.RenderingOption;
import static apainter.data.PixelData15BitColor.*;

class PenCPUDefaultRendering extends PenCPURendering{
	//0b111111111111111 (15bit最大値)
	final static int max15bitValue=PixelData15BitColor.max15bitValue;
	@Override
	final protected void renderint(PixelDataInt base,PixelDataByte over,Point p,Rectangle clip,RenderingOption option){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;

		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);
				int oc = pixel(overp,x-px,y-py,overw);
				int oa = layeralph(oc,overalph);


				if(a !=0){
					set(basep,defaultOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	
	@Override
	protected void render15bit(PixelData15BitColor base,
			PixelDataByte over, Point p, Rectangle clip,
			RenderingOption option) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oc = pixel(overp,x-px,y-py,overw)<<7;
				int oa = layeralph(oc,overalph);
				if(a!=0){
					default15bitOp(a, r, g, b, oa, or, og, ob, baseintp, basedecp, x+y*basew);
					continue;
				}else{
					set(baseintp,argb(integer(oa),integer(or),integer(og),integer(ob)), x, y, basew);
					set(basedecp,argb(decimal(oa),decimal(or),decimal(og),decimal(ob)),x,y,basew);
					continue;
				}
			}
		}
	}
	
	@Override
	protected void render15bit(PixelData15BitColor base,
			PixelData15BitGray over, Point p, Rectangle clip,
			RenderingOption option) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		int basew = base.width;
		byte[] overintp = over.getInteger();
		byte[] overdecp =over.getDecimal();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oi = pixel(overintp,x-px,y-py,overw);
				int od = pixel(overdecp,x-px,y-py,overw);
				int oc = convine(oi,od);
				int oa = layeralph(oc,overalph);
				if(a!=0){
					default15bitOp(a, r, g, b, oa, or, og, ob, baseintp, basedecp, x+y*basew);
					continue;
				}else{
					set(baseintp,argb(integer(oa),integer(or),integer(og),integer(ob)), x, y, basew);
					set(basedecp,argb(decimal(oa),decimal(or),decimal(og),decimal(ob)),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	protected void render15bit_dmask(PixelData15BitColor base,
			PixelDataByte over, Point p, Rectangle clip,
			RenderingOption option, PixelDataByte dmask) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		byte[] dmaskp = dmask.getData();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				int rdmaskv = 255-dmaskv;
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oc = pixel(overp,x-px,y-py,overw)<<7;
				int oa = layeralph(oc,overalph);
				if(a!=0){
					int position = x+y*basew;
					int a1 = calca15bit(a, oa);
					int underalpha =  (a*(32767-oa)/32767);
					int r1 = (oa*or+underalpha*r)/a1;
					r1 = (r1*dmaskv+r*rdmaskv)/255;
					int g1 = (oa*og+underalpha*g)/a1;
					g1 = (g1*dmaskv+g*rdmaskv)/255;
					int b1 = (oa*ob+underalpha*b)/a1;
					b1 = (b1*dmaskv+b*rdmaskv)/255;
					baseintp[position] = argb(integer(a1), integer(r1), integer(g1), integer(b1));
					basedecp[position] = argb(decimal(a1),decimal(r1),decimal(g1),decimal(b1));
					continue;
				}else{
					oa = oa*dmaskv/255;
					set(baseintp,argb(integer(oa),integer(or),integer(og),integer(ob)), x, y, basew);
					set(basedecp,argb(decimal(oa),decimal(or),decimal(og),decimal(ob)),x,y,basew);
					continue;
				}
			}
		}//for y
	}
	
	@Override
	protected void render15bit_dmask(PixelData15BitColor base, PixelData15BitGray over, Point p, Rectangle clip, RenderingOption option, PixelDataByte dmask) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		byte[] dmaskp = dmask.getData();
		int basew = base.width;
		byte[] overintp = over.getInteger();
		byte[] overdecp =over.getDecimal();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				int rdmaskv = 255-dmaskv;
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oi = pixel(overintp,x-px,y-py,overw);
				int od = pixel(overdecp,x-px,y-py,overw);
				int oc = convine(oi,od);
				int oa = layeralph(oc,overalph);
				if(a!=0){
					int position = x+y*basew;
					int a1 = calca15bit(a, oa);
					int underalpha =  (a*(32767-oa)/32767);
					int r1 = (oa*or+underalpha*r)/a1;
					r1 = (r1*dmaskv+r*rdmaskv)/255;
					int g1 = (oa*og+underalpha*g)/a1;
					g1 = (g1*dmaskv+g*rdmaskv)/255;
					int b1 = (oa*ob+underalpha*b)/a1;
					b1 = (b1*dmaskv+b*rdmaskv)/255;
					baseintp[position] = argb(integer(a1), integer(r1), integer(g1), integer(b1));
					basedecp[position] = argb(decimal(a1),decimal(r1),decimal(g1),decimal(b1));
					continue;
				}else{
					oa = oa*dmaskv/255;
					set(baseintp,argb(integer(oa),integer(or),integer(og),integer(ob)), x, y, basew);
					set(basedecp,argb(decimal(oa),decimal(or),decimal(og),decimal(ob)),x,y,basew);
					continue;
				}
			}
		}//for y
	}

	@Override
	final protected void renderint_dmask(PixelDataInt base,PixelDataByte over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByte dmask){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] dmaskp = dmask.getData();
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				int rdmaskv = 255-dmaskv;
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);
				int oc = pixel(overp,x-px,y-py,overw);
				int oa = layeralph(oc,overalph);


				if(a !=0){
					int argb = defaultOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							or,
							og,
							ob),x,y,basew);
					continue;
				}
			}//for x
		}//for y
	}

	@Override
	protected void render15bit_alphfix(PixelData15BitColor base,
			PixelDataByte over, Point p, Rectangle clip,
			RenderingOption option) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oc = pixel(overp,x-px,y-py,overw)<<7;
				int oa = layeralph(oc,overalph);
				if(a!=0){
					int position = x+y*basew;
					int a1 = calca15bit(a, oa);
					int underalpha =  (a*(32767-oa)/32767);
					int r1 = (oa*or+underalpha*r)/a1;
					int g1 = (oa*og+underalpha*g)/a1;
					int b1 = (oa*ob+underalpha*b)/a1;
					baseintp[position] = argb(integer(a), integer(r1), integer(g1), integer(b1));
					basedecp[position] = argb(decimal(a),decimal(r1),decimal(g1),decimal(b1));
					continue;
				}
			}
		}//for y
	}

	@Override
	protected void render15bit_alphfix(PixelData15BitColor base,
			PixelData15BitGray over, Point p, Rectangle clip,
			RenderingOption option) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		int basew = base.width;
		byte[] overintp = over.getInteger();
		byte[] overdecp =over.getDecimal();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oi = pixel(overintp,x-px,y-py,overw);
				int od = pixel(overdecp,x-px,y-py,overw);
				int oc = convine(oi,od);
				int oa = layeralph(oc,overalph);
				if(a!=0){
					int position = x+y*basew;
					int a1 = calca15bit(a, oa);
					int underalpha =  (a*(32767-oa)/32767);
					int r1 = (oa*or+underalpha*r)/a1;
					int g1 = (oa*og+underalpha*g)/a1;
					int b1 = (oa*ob+underalpha*b)/a1;
					baseintp[position] = argb(integer(a), integer(r1), integer(g1), integer(b1));
					basedecp[position] = argb(decimal(a),decimal(r1),decimal(g1),decimal(b1));
					continue;
				}
			}
		}//for y
	}
	
	@Override
	final protected void renderint_alphfix(PixelDataInt base,PixelDataByte over,Point p,Rectangle clip,RenderingOption option){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;


		for(int y = clip.y;y<endy;y++){
			for(int x = clip.x;x<endx;x++){
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				if(a !=0){
					set(basep,defaultOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataInt base,PixelDataByte over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByte mask){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] dmaskp = mask.getData();
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;


		for(int y = clip.y;y<endy;y++){
			for(int x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				int rdmaskv=255-dmaskv;
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);
				int oc =pixel(overp,x-px,y-py,overw);
				int oa = layeralph(a(oc),overalph);

				if(a !=0){
					int argb = defaultOp(a, r, g, b, oa, or, og, ob);

					set(basep,
							argb(a,
									(r(argb)*dmaskv+r*rdmaskv)/255,
									(g(argb)*dmaskv+g*rdmaskv)/255,
									(b(argb)*dmaskv+b*rdmaskv)/255),
									x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	protected void render15bit_alphfix_dmask(PixelData15BitColor base,
			PixelDataByte over, Point p, Rectangle clip,
			RenderingOption option, PixelDataByte mask) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		int basew = base.width;
		byte[] dmaskp = mask.getData();
		byte[] overp = over.getData();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				int rdmaskv=255-dmaskv;
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oc = pixel(overp,x-px,y-py,overw)<<7;
				int oa = layeralph(oc,overalph);
				if(a!=0){
					int position = x+y*basew;
					int a1 = calca15bit(a, oa);
					int underalpha =  (a*(32767-oa)/32767);
					int r1 = (oa*or+underalpha*r)/a1;
					r1 = (r1*dmaskv+r*rdmaskv)/255;
					int g1 = (oa*og+underalpha*g)/a1;
					g1 = (g1*dmaskv+g*rdmaskv)/255;
					int b1 = (oa*ob+underalpha*b)/a1;
					b1 = (b1*dmaskv+b*rdmaskv)/255;
					baseintp[position] = argb(integer(a), integer(r1), integer(g1), integer(b1));
					basedecp[position] = argb(decimal(a),decimal(r1),decimal(g1),decimal(b1));
					continue;
				}
			}
		}//for y

	}
	
	@Override
	protected void render15bit_alphfix_dmask(PixelData15BitColor base,
			PixelData15BitGray over, Point p, Rectangle clip,
			RenderingOption option, PixelDataByte mask) {
		int[] baseintp = base.getInteger();
		int[] basedecp = base.getDecimal();
		int basew = base.width;
		byte[] dmaskp = mask.getData();
		byte[] overintp = over.getInteger();
		byte[] overdecp =over.getDecimal();
		int overw = over.width;
		int or=option.frontColor.getR( max15bitValue );
		int og=option.frontColor.getG( max15bitValue );
		int ob=option.frontColor.getB( max15bitValue );
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				int rdmaskv=255-dmaskv;
				int ci = pixel(baseintp,x,y,basew);
				int cd = pixel(basedecp,x,y,basew);
				int a = convine(a(ci), a(cd));
				int r = convine(r(ci), r(cd));
				int g = convine(g(ci), g(cd));
				int b = convine(b(ci), b(cd));
				int oi = pixel(overintp,x-px,y-py,overw);
				int od = pixel(overdecp,x-px,y-py,overw);
				int oc = convine(oi,od);
				int oa = layeralph(oc,overalph);
				if(a!=0){
					int position = x+y*basew;
					int a1 = calca15bit(a, oa);
					int underalpha =  (a*(32767-oa)/32767);
					int r1 = (oa*or+underalpha*r)/a1;
					r1 = (r1*dmaskv+r*rdmaskv)/255;
					int g1 = (oa*og+underalpha*g)/a1;
					g1 = (g1*dmaskv+g*rdmaskv)/255;
					int b1 = (oa*ob+underalpha*b)/a1;
					b1 = (b1*dmaskv+b*rdmaskv)/255;
					baseintp[position] = argb(integer(a), integer(r1), integer(g1), integer(b1));
					basedecp[position] = argb(decimal(a),decimal(r1),decimal(g1),decimal(b1));
					continue;
				}
			}
		}//for y
	}
}
