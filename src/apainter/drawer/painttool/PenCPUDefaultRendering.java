package apainter.drawer.painttool;

import static apainter.misc.Utility_PixelFunction.*;
import static apainter.rendering.ColorOperations.*;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.RenderingOption;

class PenCPUDefaultRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
}

class PenCPUAddRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,addOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = addOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,addOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = addOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUSubtractiveRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,subtractiveOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = subtractiveOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,subtractiveOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = subtractiveOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUMultiplicationRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,multiplicationOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = multiplicationOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,multiplicationOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = multiplicationOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUScreenRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,screenOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = screenOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,screenOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = screenOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUOverlayRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,overlayOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = overlayOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,overlayOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = overlayOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUSoftlightRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,softlightOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = softlightOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,softlightOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = softlightOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUHardlightRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,hardlightOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = hardlightOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,hardlightOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = hardlightOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUDodgeRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,dodgeOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = dodgeOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,dodgeOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = dodgeOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUBurnRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,burnOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = burnOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,burnOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = burnOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUDarkenRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,darkenOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = darkenOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,darkenOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = darkenOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPULightRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,lightOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = lightOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,lightOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = lightOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUDifferenceRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,differenceOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = differenceOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,differenceOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = differenceOp(a, r, g, b, oa, or, og, ob);

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
}

class PenCPUExclusionRendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,exclusionOp(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
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
					int argb = exclusionOp(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
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
					set(basep,exclusionOp(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
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
					int argb = exclusionOp(a, r, g, b, oa, or, og, ob);

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
}

