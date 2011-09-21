package apainter.drawer.painttool;

import static apainter.misc.Utility_PixelFunction.*;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.Color;
import apainter.Device;
import apainter.GlobalValue;
import apainter.data.PixelData15BitBuffer;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.drawer.DrawPoint;
import apainter.drawer.DrawTarget;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;
import static apainter.data.PixelData15BitBuffer.*;
//TODO mask対応
public class Eraser extends BasicDrawer{

	Renderer cpu8bit = new EraserCPURendering();

	public Eraser(GlobalValue global,int id) {
		super(global,id);
	}

	@Override
	protected Renderer getRenderer(Device d) {
		return cpu8bit;
	}

	@Override
	protected Device[] getUsableDevices() {
		//TODO GPU対応したいね。
		return CPUOnly();
	}

	@Override
	protected void setOption(RenderingOption option, DrawPoint e) {}

	@Override
	protected boolean isDrawable(DrawTarget dt) {
		String[] s =dt.getDrawTargetName().split(" ");
		return s[0].equals("cpulayer");
	}

}


class EraserCPURendering implements Renderer{

	private static PenCPUDefaultRendering pcdr = new PenCPUDefaultRendering();

	@Override
	final public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(base instanceof PixelData15BitBuffer){
			do15bitlayer((PixelData15BitBuffer)base, (PixelDataByteBuffer)over, p, clip, option);
		}else if(base instanceof PixelDataIntBuffer){
			dolayer((PixelDataIntBuffer)base, over, p, clip, option);
		}else if(base instanceof PixelDataByteBuffer){
			domask((PixelDataByteBuffer)base,over,p,clip,option);
		}
	}

	final private static void dolayer(PixelDataIntBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(option.alphaFixed){
			if(option.hasDestinationMask()){
				if(over instanceof PixelDataByteBuffer)
					renderint_alphfix_dmask(base, (PixelDataByteBuffer)over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
			}else{
				if(over instanceof PixelDataByteBuffer)
					renderint_alphfix(base, (PixelDataByteBuffer)over, p, clip, option);
			}
		}
		else{
			if(option.hasDestinationMask()){
				renderint_dmask(base, (PixelDataByteBuffer)over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
			}else{
				renderint(base, (PixelDataByteBuffer)over, p, clip, option);
			}
		}
	}

	final void do15bitlayer(PixelData15BitBuffer base,PixelDataByteBuffer over,Point p,
			Rectangle clip,RenderingOption option){
		if(option.alphaFixed){
			if(option.hasDestinationMask()){
					render15bit_alphfix_dmask(base,over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
			}else{
					render15bit_alphfix(base, over, p, clip, option);
			}
		}
		else{
			if(option.hasDestinationMask()){
					render15bit_dmask(base, over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
			}else{
					render15bit(base, over, p, clip, option);
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

	final static void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
		final int[] basep = base.getData();
		final int basew = base.width;
		final byte[] overp = over.getData();
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

	private void render15bit(PixelData15BitBuffer base,
			PixelDataByteBuffer over, Point p, Rectangle clip,
			RenderingOption option) {
		final int[] baseintp = base.getInteger();
		final int[] basedecp = base.getDecimal();
		final int basew = base.width;
		final byte[] overp = over.getData();
		final int overw = over.width;
		final int overalph = option.overlayeralph;
		final int endy = clip.height+clip.y,endx=clip.width+clip.x;
		final int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				final int ci = pixel(baseintp,x,y,basew);
				final int cd = pixel(basedecp,x,y,basew);
				final int a = convine(a(ci), a(cd));

				final int oa = layeralph(pixel(overp,x-px,y-py,overw),overalph)<<7;


				if(a !=0){
					int alpha = a-oa;

					int pos = x+y*basew;
					if(alpha<=0){
						baseintp[pos]=basedecp[pos]=0;
					}else{
						baseintp[pos] = (alpha>>>7)<<24 | (ci&0xffffff);
						basedecp[pos] = (alpha&127)<<24 | (cd&0xffffff);
					}
				}
			}
		}
	}



	final static void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
		final int[] basep = base.getData();
		final int basew = base.width;
		final byte[] dmaskp = dmask.getData();
		final byte[] overp = over.getData();
		final int overw = over.width;
		final int overalph = option.overlayeralph;
		final int endy = clip.height+clip.y,endx=clip.width+clip.x;
		final int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				final int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				final int rdmaskv = 255-dmaskv;
				final int c = pixel(basep,x,y,basew);
				final int a = a(c);

				final int oa = layeralph(pixel(overp,x-px,y-py,overw),overalph);


				if(a !=0){
					int alpha = (a-oa)<=0?0:a-oa;
					alpha = (alpha*dmaskv+a*rdmaskv)/255;

					set(basep,alpha==0?Color.ClearColor:(alpha<<24)|c&0xffffff,
							x,y,basew);
					continue;
				}
			}
		}
	}

	private void render15bit_dmask(PixelData15BitBuffer base,
			PixelDataByteBuffer over, Point p, Rectangle clip,
			RenderingOption option, PixelDataByteBuffer dmask) {
		final int[] baseintp = base.getInteger();
		final int[] basedecp = base.getDecimal();
		final int basew = base.width;
		final byte[] dmaskp = dmask.getData();
		final byte[] overp = over.getData();
		final int overw = over.width;
		final int overalph = option.overlayeralph;
		final int endy = clip.height+clip.y,endx=clip.width+clip.x;
		final int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				final int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				final int rdmaskv = 255-dmaskv;
				final int ci = pixel(baseintp,x,y,basew);
				final int cd = pixel(basedecp,x,y,basew);
				final int a = convine(a(ci), a(cd));

				final int oa = layeralph(pixel(overp,x-px,y-py,overw),overalph)<<7;


				if(a !=0){
					int alpha = (a-oa)<=0?0:a-oa;
					alpha = (alpha*dmaskv+a*rdmaskv)/255;
					int pos = x+y*basew;
					if(alpha==0){
						baseintp[pos]=basedecp[pos]=0;
					}else{
						baseintp[pos] = (alpha>>>7)<<24 | (ci&0xffffff);
						basedecp[pos] = (alpha&127)<<24 | (cd&0xffffff);
					}
				}
			}
		}
	}

	final static void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
		option.frontColor=option.backColor;
		pcdr.renderint_alphfix(base, over, p, clip, option);
	}

	private static void render15bit_alphfix(PixelData15BitBuffer base,
			PixelDataByteBuffer over, Point p, Rectangle clip,
			RenderingOption option) {
		option.frontColor=option.backColor;
		pcdr.render15bit_alphfix(base, over, p, clip, option);

	}

	final static void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
		option.frontColor=option.backColor;
		pcdr.renderint_alphfix_dmask(base, over, p, clip, option, mask);
	}

	private static void render15bit_alphfix_dmask(PixelData15BitBuffer base,
			PixelDataByteBuffer over, Point p, Rectangle clip,
			RenderingOption option, PixelDataByteBuffer mask) {
		option.frontColor=option.backColor;
		pcdr.render15bit_alphfix_dmask(base, over, p, clip, option, mask);
	}


	//TODO mask

}