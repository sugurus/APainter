package apainter.gui;

import apainter.canvas.Canvas;
import apainter.canvas.CanvasAction;
import nodamushi.pentablet.TabletMouseEvent;

public interface CanvasMouseListener extends CanvasAction{
	public void press(TabletMouseEvent e,Canvas canvas);
	public void drag(TabletMouseEvent e,Canvas canvas);
	public void release(TabletMouseEvent e,Canvas canvas);
	public void move(TabletMouseEvent e,Canvas canvas);
	public void exit(TabletMouseEvent e,Canvas canvas);
	public void enter(TabletMouseEvent e,Canvas canvas);
}
