package apainter.drawer.painttool;

import java.awt.Point;
import java.awt.Rectangle;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalValue;
import apainter.color.Color;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.misc.Utility_PixelFunction;
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

	@Override
	public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if(base instanceof PixelDataIntBuffer){
			renderint((PixelDataIntBuffer)base, (PixelDataIntBuffer)over, p, clip, option);
		}
	}

	void renderint(PixelDataIntBuffer base,PixelDataIntBuffer over,Point p,Rectangle clip,RenderingOption option){
		int[] basep = base.getData();
		int basew = base.width;
		int[] overp = over.getData();
		int overw = over.width;
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px = p.x,py = p.y;

		for(int y=clip.y;y<endy;y++){
			int overy = y-py;
			for(int x=clip.x;x<endx;x++){
				int overx = x-px;
				int color =basep[x+y*basew];
				int alph = (color>>>24)-(overp[overx+overy*overw]*overalph>>8);
				basep[x+y*basew]=
					alph<=0?
							Color.ClearColor:
							alph<<24|(color&0xffffff);
			}
		}
	}

	//TODO alph fix

	//TODO MaskのPixelDataByteBuffer

}