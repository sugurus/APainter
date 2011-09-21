package apainter.drawer.painttool;

import static apainter.misc.Utility_PixelFunction.*;

import java.awt.Point;
import java.awt.Rectangle;


import apainter.Color;
import apainter.data.PixelData15BitBuffer;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

abstract class PenCPURendering implements Renderer{

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

	final void dolayer(PixelDataIntBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(option.alphaFixed){
			if(option.hasDestinationMask()){
				if(over instanceof PixelDataByteBuffer){
					renderint_alphfix_dmask(base, (PixelDataByteBuffer)over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
				}
			}else{
				if(over instanceof PixelDataByteBuffer)
					renderint_alphfix(base, (PixelDataByteBuffer)over, p, clip, option);
			}
		}
		else{
			if(option.hasDestinationMask()){
				if(over instanceof PixelDataByteBuffer)
					renderint_dmask(base, (PixelDataByteBuffer)over, p, clip, option,(PixelDataByteBuffer)option.destinationmask);
			}else{
				if(over instanceof PixelDataByteBuffer)
					renderint(base, (PixelDataByteBuffer)over, p, clip, option);
			}
		}
	}

	//TODO mask
	final void domask(PixelDataByteBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option){
		//TODO draw mask
		if(option.hasDestinationMask()){
			renderbyte_dmask(base, (PixelDataByteBuffer)over, p, clip, option,
					(PixelDataByteBuffer)option.destinationmask);
		}else{
			renderbyte(base, (PixelDataByteBuffer)over, p,
					clip, option);
		}
	}


	protected void renderbyte(PixelDataByteBuffer base,
			PixelDataByteBuffer over,Point p,Rectangle clip,
			RenderingOption option){
		byte[] basep = base.getData();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int color = Color.RGB2YCrCb(option.frontColor.getARGB());
		int yvalue=r(color);//get Y
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int y = clip.y;y<endy;y++){
			for(int x = clip.x;x<endx;x++){
				int basevalue = pixel(basep,x,y,basew);
				int setdensity = layeralph(
						pixel(overp,x-px,y-py,overw),
						overalph);
				byte setvalue = (byte)(
						(yvalue*setdensity+basevalue*(255-setdensity))
						*div255shift24>>>24);
				set(basep, setvalue, x, y, basew);
			}//for x
		}//for y
	}

	protected void renderbyte_dmask(PixelDataByteBuffer base,
			PixelDataByteBuffer over,Point p,Rectangle clip,
			RenderingOption option,PixelDataByteBuffer dmask){
		byte[] basep = base.getData();
		int basew = base.width;
		byte[] dmaskp = dmask.getData();
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		color =Color.RGB2YCrCb(color);
		int yvalue=r(color);//get Y
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;
		for(int y = clip.y;y<endy;y++){
			for(int x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				int basevalue = pixel(basep,x,y,basew);
				int setdensity = layeralph(
						dmaskv,
						layeralph(
								pixel(overp,x-px,y-py,overw)
								,overalph));
				byte setvalue = (byte)(
					(yvalue*setdensity+basevalue*(255-setdensity))
					*div255shift24>>>24);
				set(basep, setvalue, x, y, basew);
			}//for x
		}//for y
	}

	abstract protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option);
	abstract protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,
			Point p,Rectangle clip,RenderingOption option,PixelDataByteBuffer dmask);
	abstract protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option);
	abstract protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,PixelDataByteBuffer mask);


	abstract protected void render15bit(PixelData15BitBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option);
	abstract protected void render15bit_dmask(PixelData15BitBuffer base,PixelDataByteBuffer over,
			Point p,Rectangle clip,RenderingOption option,PixelDataByteBuffer dmask);
	abstract protected void render15bit_alphfix(PixelData15BitBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option);
	abstract protected void render15bit_alphfix_dmask(PixelData15BitBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,PixelDataByteBuffer mask);
}
