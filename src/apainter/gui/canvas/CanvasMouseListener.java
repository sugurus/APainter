package apainter.gui.canvas;

import apainter.canvas.Canvas;
import nodamushi.pentablet.PenTabletMouseEvent;

public interface CanvasMouseListener {
	public void press(PenTabletMouseEvent e,Canvas canvas);
	public void drag(PenTabletMouseEvent e,Canvas canvas);
	public void release(PenTabletMouseEvent e,Canvas canvas);
	public void move(PenTabletMouseEvent e,Canvas canvas);
}
