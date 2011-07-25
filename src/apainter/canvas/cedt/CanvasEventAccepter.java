package apainter.canvas.cedt;

import apainter.canvas.event.CanvasEvent;

public interface CanvasEventAccepter {
	public void passEvent(CanvasEvent e);
	public void initCEDT();
	public void shutDownCEDT();
}
