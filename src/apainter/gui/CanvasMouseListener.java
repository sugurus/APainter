package apainter.gui;

import apainter.canvas.Canvas;
import apainter.canvas.CanvasAction;
import nodamushi.pentablet.PenTabletMouseEvent;

public interface CanvasMouseListener extends CanvasAction{
	public void press(PenTabletMouseEvent e,Canvas canvas);
	public void drag(PenTabletMouseEvent e,Canvas canvas);
	public void release(PenTabletMouseEvent e,Canvas canvas);
	public void move(PenTabletMouseEvent e,Canvas canvas);
	public void exit(PenTabletMouseEvent e,Canvas canvas);
	public void enter(PenTabletMouseEvent e,Canvas canvas);
}
