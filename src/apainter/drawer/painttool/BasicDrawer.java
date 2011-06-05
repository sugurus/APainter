package apainter.drawer.painttool;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.color.Color;
import apainter.drawer.Drawer;
import apainter.pen.PenShape;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

//TODO BasicDrawer
public abstract class BasicDrawer extends Drawer implements Renderer{

	@Override
	protected Color getFrontColor(PenTabletMouseEvent e, PenShape pen) {
		return null;
	}

	@Override
	protected Color getBackColor(PenTabletMouseEvent e, PenShape pen) {
		return null;
	}

	@Override
	protected void setOption(RenderingOption option, PenTabletMouseEvent e) {

	}

}
