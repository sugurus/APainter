package apainter.gui;

import java.awt.event.MouseWheelEvent;

import apainter.canvas.Canvas;

public interface CanvasScrollListener {
	public void scroll(MouseWheelEvent e,Canvas canvas);
}
