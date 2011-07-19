package apainter.canvas;

import apainter.GlobalValue;
import apainter.canvas.event.PainterEvent;

abstract public class CanvasThread {
	protected final GlobalValue global;
	protected final Canvas canvas;
	public CanvasThread(GlobalValue global,Canvas canvas) {
		this.global = global;
		this.canvas = canvas;
	}
	public abstract void init();
	public abstract void shutdown();
	public abstract void dispatch(PainterEvent e);
}
