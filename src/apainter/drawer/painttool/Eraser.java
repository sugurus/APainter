package apainter.drawer.painttool;

import java.awt.Point;
import java.awt.Rectangle;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalValue;
import apainter.color.Color;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

import static apainter.misc.Utility_PixelFunction.*;

public class Eraser extends BasicDrawer{

	Renderer cpu8bit = new EraserCPURendering();

	public Eraser(GlobalValue global) {
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


class EraserCPURendering implements Renderer{

	private static PenCPUDefaultRendering pcdr = new PenCPUDefaultRendering();

	@Override
	final public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(base instanceof PixelDataIntBuffer){
			dolayer((PixelDataIntBuffer)base, over, p, clip, option);
		}else if(base instanceof PixelDataByteBuffer){
			domask((PixelDataByteBuffer)base,over,p,clip,option);
		}
	}

	final private static void dolayer(PixelDataIntBuffer base, PixelDataBuffer over, Point p,
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

	final private static void domask(PixelDataByteBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option){
		//TODO draw mask
		if(option.hasDestinationMask()){

		}else{

		}

	}

	final static void renderint(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option){
		final int[] basep = base.getData();
		final int basew = base.width;
		final int[] overp = over.getData();
		final int overw = over.width;
		final int overalph = option.overlayeralph;
		final int endy = clip.height+clip.y,endx=clip.width+clip.x;
		final int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				final int c = pixel(basep,x,y,basew);
				final int a = a(c);

				final int oa = layeralph(pixel(overp,x-px,y-py,overw),overalph);


				if(a !=0){
					int alpha = a-oa;

					set(basep,alpha<=0?Color.ClearColor:(alpha<<24)|c&0xffffff,
							x,y,basew);
					continue;
				}
			}
		}
	}

	final static void renderint_dmask(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
		final int[] basep = base.getData();
		final int basew = base.width;
		final byte[] dmaskp = dmask.getData();
		final int[] overp = over.getData();
		final int overw = over.width;
		final int overalph = option.overlayeralph;
		final int endy = clip.height+clip.y,endx=clip.width+clip.x;
		final int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				final int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
				if(dmaskv==0)continue;
				final int rdmaskv = 255-dmaskv;
				final int c = pixel(basep,x,y,basew);
				final int a = a(c);

				final int oa = layeralph(pixel(overp,x-px,y-py,overw),overalph);


				if(a !=0){
					int alpha = (a-oa)<=0?0:a-oa;
					alpha = (alpha*dmaskv+a*rdmaskv)/255;

					set(basep,alpha<=0?Color.ClearColor:(alpha<<24)|c&0xffffff,
							x,y,basew);
					continue;
				}
			}
		}
	}

	final static void renderint_alphfix(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option){
		option.frontColor=option.backColor;
		pcdr.renderint_alphfix(base, over, p, clip, option);
	}


	final static void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
		option.frontColor=option.backColor;
		pcdr.renderint_alphfix_dmask(base, over, p, clip, option, mask);
	}


	//TODO mask

}