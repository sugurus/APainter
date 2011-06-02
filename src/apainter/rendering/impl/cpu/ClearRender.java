package apainter.rendering.impl.cpu;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;
import static apainter.rendering.impl.cpu.UtilR.*;
/**
 * レンダリング中に生成されてしまう透明度０で色が０でない色を削除します
 * @author nodamushi
 *
 */
public class ClearRender implements Renderer{

	@Override
	public void rendering(PixelDataBuffer base, PixelDataBuffer over, Point p,
			Rectangle clip, RenderingOption option) {
		if (base instanceof PixelDataIntBuffer) {
			rendering((PixelDataIntBuffer)base, over, p, clip, option);
		}
	}

	public void rendering(PixelDataIntBuffer base, PixelDataBuffer over, Point p,
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
