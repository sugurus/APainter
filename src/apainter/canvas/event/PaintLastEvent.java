package apainter.canvas.event;
import apainter.canvas.event.CanvasEvent;

public class PaintLastEvent extends CanvasEvent{

	PaintEventAccepter target;

	public PaintLastEvent( Object source, PaintEventAccepter target) {
		super(EventConstant.ID_PAINT_LAST, source, target.getCanvas());
		this.target = target;
	}

	public PaintEventAccepter getTarget(){
		return target;
	}

}