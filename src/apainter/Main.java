package apainter;

import apainter.canvas.Canvas;
import apainter.gui.canvas.CanvasView;

class Main {

	public static void main(APainter apainter){
System.out.println(apainter);
		Canvas canvas = new Canvas(400, 400, Device.CPU);
		APainter.getContentPane()
		.add(canvas.getCanvasView());
		CanvasView c = canvas.getCanvasView();
		c.setCenterPointX(100);
		c.setAngle(120);
		c.setZoom(2);
		apainter.pack();
	}

}
