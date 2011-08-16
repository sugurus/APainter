package apainter.drawer.painttool;

import java.awt.Point;
import java.awt.Rectangle;


import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

abstract class PenCPURendering implements Renderer{

	@Override
	final public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(base instanceof PixelDataIntBuffer){
			dolayer((PixelDataIntBuffer)base, over, p, clip, option);
		}else if(base instanceof PixelDataByteBuffer){
			domask((PixelDataByteBuffer)base,over,p,clip,option);
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

	final void domask(PixelDataByteBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option){
		//TODO draw mask
		if(option.hasDestinationMask()){

		}else{

		}
	}

	//TODO mask

	abstract protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option);
	abstract protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,
			Point p,Rectangle clip,RenderingOption option,PixelDataByteBuffer dmask);
	abstract protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option);
	abstract protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,PixelDataByteBuffer mask);
}
