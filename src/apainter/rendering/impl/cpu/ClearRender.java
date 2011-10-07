package apainter.rendering.impl.cpu;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelData;
import apainter.data.PixelDataContainer;
import apainter.data.PixelDataInt;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;
import static apainter.misc.Utility_PixelFunction.*;
/**
 * レンダリング中に生成されてしまう透明度０で色が０でない色を削除します
 * @author nodamushi
 *
 */
public class ClearRender implements Renderer{

	@Override
	public void rendering(PixelData base, PixelDataContainer over, Point p,
			Rectangle clip, RenderingOption option) {
		if (base instanceof PixelDataInt) {
			rendering((PixelDataInt)base, (PixelData)null, p, clip, option);
		}
	}

	public void rendering(PixelDataInt base, PixelData over, Point p,
			Rectangle clip, RenderingOption option) {
		final int basew = base.width;
		final int w = clip.width+clip.x,h=clip.height+clip.y;
		final int[] pixel = base.getData();
		final int clipx = clip.x;

		for(int x,y=clip.y;y<h;y++){
			for(x=clipx;x<w;x++){
				if(a(pixel(pixel,x,y,basew))==0)
					set(pixel, 0, x, y, basew);
			}
		}


	}

}
