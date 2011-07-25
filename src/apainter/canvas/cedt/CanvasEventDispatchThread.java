package apainter.canvas.cedt;

import apainter.canvas.event.CanvasEvent;


public interface CanvasEventDispatchThread {
	public void init();
	public boolean isRunning();
	public void shutdown();
	public void dispatch(CanvasEvent e);
}
