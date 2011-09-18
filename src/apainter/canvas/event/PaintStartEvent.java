package apainter.canvas.event;

import apainter.canvas.event.CanvasEvent;

public class PaintStartEvent extends CanvasEvent{

	PaintEventAccepter target;

	public PaintStartEvent( Object source, PaintEventAccepter target) {
		super(EventConstant.ID_PAINT_START, source, target.getCanvas());
		this.target = target;
	}

	public PaintEventAccepter getTarget(){
		return target;
	}

}