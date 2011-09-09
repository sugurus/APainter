package apainter.history;

import apainter.canvas.Canvas;
import apainter.canvas.event.CanvasEvent;

public class HisotryReduceEvent extends CanvasEvent{

	public HisotryReduceEvent(int id, History source, Canvas canvas) {
		super(id, source, canvas);
	}

	public History getSource(){
		return (History)super.getSource();
	}
}
