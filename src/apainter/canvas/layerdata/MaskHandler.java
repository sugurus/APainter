package apainter.canvas.layerdata;

import apainter.Handler;
import apainter.canvas.event.PaintEventAccepter;
import apainter.drawer.DrawTarget;
/**
 * 外部には漏らさない事。
 * @author nodamushi
 *
 */
public interface MaskHandler extends PaintEventAccepter,DrawTarget,Handler{
	public Mask getMask();
}
