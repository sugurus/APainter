package apainter.canvas.layerdata;

import java.awt.Rectangle;

import apainter.canvas.Canvas;
import apainter.history.PixelChangeHistory;

public class PaintLayerHistory extends PixelChangeHistory{

	InnerLayerHandler l;
	
	PaintLayerHistory(Canvas canvas,InnerLayerHandler layer) {
		super(canvas);
		l=layer;
		copyBeforePixel(l.getOriginalData());
	}
	
	void finishPaint(Rectangle r){
		setAfterPixel(l.getOriginalData(), r);
	}

	@Override
	protected boolean undo() throws Exception {
		drawBeforeData(l.getOriginalData());
		rendering();
		return true;
	}

	@Override
	protected boolean redo() throws Exception {
		drawAfterData(l.getOriginalData());
		rendering();
		return true;
	}

	
	@Override
	public String getHistoryName() {
		return "PaintLayerHistory";
	}

}
