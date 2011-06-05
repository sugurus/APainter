package apainter;

import apainter.canvas.Canvas;

class Main {

	public static void main(APainter apainter){
System.out.println(apainter);
		Canvas canvas = new Canvas(400, 400, Device.CPU);
		apainter.getContentPane()
		.add(canvas.testMethod_getPanel());
		apainter.pack();
	}

}
