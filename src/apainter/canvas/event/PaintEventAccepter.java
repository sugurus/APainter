package apainter.canvas.event;

import apainter.canvas.CanvasElement;

public interface PaintEventAccepter extends CanvasElement{
	public boolean paint(PaintEvent e);
	public void startPaint(Object obj);
	public void endPaint(Object obj);
	public boolean isPaintable();
}
