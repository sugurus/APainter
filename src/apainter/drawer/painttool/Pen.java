package apainter.drawer.painttool;

import static apainter.misc.Utility_PixelFunction.*;

import java.awt.Point;
import java.awt.Rectangle;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalValue;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;


public class Pen extends BasicDrawer{

	Renderer cpu8bit = new PenCPURendering();

	public Pen(GlobalValue global) {
		super(global);
	}

	@Override
	protected Renderer getRenderer() {
		return cpu8bit;
	}

	@Override
	protected Device[] getUsableDevices() {
		//TODO GPU対応したいね。
		return CPUOnly();
	}

	@Override
	protected void setOption(RenderingOption option, PenTabletMouseEvent e) {}

}


class PenCPURendering implements Renderer{

	static final int val(int b,int o,int a1,int oa,int t,int k){
		return ((b*a1 + o * oa+t)*k>>16);
	}


	@Override
	final public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(base instanceof PixelDataIntBuffer){
			dolayer((PixelDataIntBuffer)base, over, p, clip, option);
		}else if(base instanceof PixelDataByteBuffer){
			domask((PixelDataByteBuffer)base,over,p,clip,option);
		}
	}

	final static void dolayer(PixelDataIntBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(option.alphaFixed){
			if(option.hasDestinationMask()){
				renderint_alphfix_dmask(base, (PixelDataIntBuffer)over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
			}else{
				renderint_alphfix(base, (PixelDataIntBuffer)over, p, clip, option);
			}
		}
		else{
			if(option.hasDestinationMask()){
				renderint_dmask(base, (PixelDataIntBuffer)over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
			}else{
				renderint(base, (PixelDataIntBuffer)over, p, clip, option);
			}
		}
	}

	final static void domask(PixelDataByteBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option){
		//TODO draw mask
		if(option.hasDestinationMask()){

		}else{

		}

	}

	final static void renderint(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option){
		int[] basep = base.getData();
		int basew = base.width;
		int[] overp = over.getData();
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
				int oc = pixel(overp,x-px,y-py,overw);
				int oa = layeralph(oc,overalph);


				if(a !=0){
					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a1+oa);
					int t = oa + a1-1;
					set(basep,argb(
							calca(a,oa),
							val(r(c),or,a1,oa,t,k),
							val(g(c),og,a1,oa,t,k),
							val(b(c),ob,a1,oa,t,k)),
							x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	final static void renderint_dmask(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] dmaskp = dmask.getData();
		int[] overp = over.getData();
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
				int oc = pixel(overp,x-px,y-py,overw);
				int oa = layeralph(oc,overalph);


				if(a !=0){
					int alpha=calca(a,oa);

					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a1+oa);
					int t = oa + a1-1;
					set(basep,argb(
							(alpha*dmaskv+a*rdmaskv)/255,
							(val(r(c),or,a1,oa,t,k)*dmaskv+r(c)*rdmaskv)/255,
							(val(g(c),og,a1,oa,t,k)*dmaskv+g(c)*rdmaskv)/255,
							(val(b(c),ob,a1,oa,t,k)*dmaskv+b(c)*rdmaskv)/255),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv/255,
							(r(color)*dmaskv+r(c)*rdmaskv)/255,
							(g(color)*dmaskv+g(c)*rdmaskv)/255,
							(b(color)*dmaskv+b(c)*rdmaskv)/255
					),x,y,basew);
					continue;
				}
			}
		}
	}

	final static void renderint_alphfix(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option){
		int[] basep = base.getData();
		int basew = base.width;
		int[] overp = over.getData();
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
					int oc = pixel(overp,x-px,y-py,overw);
					int oa = layeralph(oc,overalph);
					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a1+oa);
					int t = oa + a1-1;
					set(basep,
							argb(a,val(r(c),or,a1,oa,t,k),val(g(c),og,a1,oa,t,k),val(b(c),ob,a1,oa,t,k)),
							x,y,basew);
					continue;
				}
			}
		}
	}


	final static void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] dmaskp = mask.getData();
		int[] overp = over.getData();
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
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
				if(dmaskv==0)continue;
				int rdmaskv=255-dmaskv;
				int c = pixel(basep,x,y,basew);
				int a = a(c);

				if(a !=0){
					int oc =pixel(overp,x-px,y-py,overw);
					int oa = layeralph(a(oc),overalph);
					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a1+oa);
					int t = oa + a1-1;
					set(basep,
							argb(a,
									(val(r(c),or,a1,oa,t,k)*dmaskv+r(c)*rdmaskv)/255,
									(val(g(c),og,a1,oa,t,k)*dmaskv+g(c)*rdmaskv)/255,
									(val(b(c),ob,a1,oa,t,k)*dmaskv+b(c)*rdmaskv)/255),
									x,y,basew);
					continue;
				}
			}
		}
	}

	//TODO mask

}