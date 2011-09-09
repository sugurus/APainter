package apainter.drawer;

import apainter.canvas.event.CanvasEvent;
import apainter.canvas.layerdata.InnerLayerHandler;

public class PaintStartEvent extends CanvasEvent{

	InnerLayerHandler target;

	public PaintStartEvent(int id, Object source, InnerLayerHandler target) {
		super(id, source, target.getCanvas());
		this.target = target;
	}

	public InnerLayerHandler getTarget(){
		return target;
	}

}
