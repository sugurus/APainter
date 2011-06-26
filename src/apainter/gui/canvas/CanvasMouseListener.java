package apainter.gui.canvas;

import nodamushi.pentablet.PenTabletMouseEvent;

public interface CanvasMouseListener {
	public void press(PenTabletMouseEvent e);
	public void drag(PenTabletMouseEvent e);
	public void release(PenTabletMouseEvent e);
	public void move(PenTabletMouseEvent e);
}
