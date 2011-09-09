package apainter.drawer;

import apainter.canvas.event.CanvasEvent;
import apainter.canvas.layerdata.InnerLayerHandler;

public class PaintLastEvent extends CanvasEvent{

	InnerLayerHandler target;

	public PaintLastEvent(int id, Object source, InnerLayerHandler target) {
		super(id, source, target.getCanvas());
		this.target = target;
	}

	public InnerLayerHandler getTarget(){
		return target;
	}

}
