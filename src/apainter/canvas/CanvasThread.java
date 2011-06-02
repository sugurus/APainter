package apainter.canvas;

import apainter.canvas.event.PainterEvent;

abstract public class CanvasThread {
	public abstract void dispatch(PainterEvent e);
}
