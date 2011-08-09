package apainter.canvas.cedt.cpu;

import apainter.canvas.Canvas;
import apainter.canvas.cedt.CanvasEventAccepter;
import apainter.canvas.event.CanvasEvent;

public class CPUCEA_0 implements CanvasEventAccepter{
	Canvas canvas ;
	CPUCEDT cedt;

	public CPUCEA_0(Canvas c) {
		canvas = c;
		cedt = new CPUCEDT(c);
	}

	@Override
	public void passEvent(CanvasEvent e) {
		if(!cedt.isRunning())cedt.init();
		cedt.dispatch(e);
	}


	@Override
	public void initCEDT() {
		cedt.init();
	}

	@Override
	public void shutDownCEDT() {
		cedt.shutdown();
	}

	@Override
	public void runInAnyThread(Runnable r) {
		cedt.runInAnyThread(r);
	}

}
