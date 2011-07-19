package apainter.drawer.painttool;

import static apainter.Utility_PixelFunction.*;

import java.awt.Point;
import java.awt.Rectangle;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalValue;
import apainter.data.PixelDataBuffer;
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

	@Override
	public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(base instanceof PixelDataIntBuffer){
			if(option.alphaFixed)
				renderint_alphfix((PixelDataIntBuffer)base, (PixelDataIntBuffer)over, p, clip, option);
			else
				renderint((PixelDataIntBuffer)base, (PixelDataIntBuffer)over, p, clip, option);
		}
	}

	void renderint(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option){
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
				int r = r(c);
				int g = g(c);
				int b = b(c);

				int oc = pixel(overp,x-px,y-py,overw);
				int oa = layeralph(oc,overalph);


				int fa = calca(a, oa);
				if(a !=0){
					int a1 = (255-oa > a)? a:255-oa;
					int k = 65536/(a1+oa);
					int t = oa + a1-1;
					set(basep,
							argb(fa,
									val(r,or,a1,oa,t,k),
									val(g,og,a1,oa,t,k),
									val(b,ob,a1,oa,t,k)),
						x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	//TODO alphfix
	void renderint_alphfix(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option){
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
							argb(a,
									val(r(c),or,a1,oa,t,k),
									val(g(c),og,a1,oa,t,k),
									val(b(c),ob,a1,oa,t,k)),
									x,y,basew);
					continue;
				}
			}
		}
	}

	private static final int val(int b,int o,int a1,int oa,int t,int k){
		return ((b*a1 + o * oa+t)*k>>16);
	}

	//TODO MaskのPixelDataByteBuffer

}