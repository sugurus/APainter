package apainter;

import apainter.canvas.Canvas;
import apainter.gui.canvas.CanvasView;

class MainFunction {

	private static GlobalValue global;

	/**
	 * 初期化スレッド以外からは呼べません。
	 * @param apainter
	 */
	public static void init(APainter apainter){
		global = GlobalValue.getInstance();

		Canvas canvas = createNewCanvas(400, 400, Device.CPU);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);

		APainter.getContentPane()
		.add(canvas.getCanvasView());
		CanvasView c = canvas.getCanvasView();
		c.setCenterPointX(100);
		c.setAngle(120);
		//c.setZoom(2);
		apainter.pack();

		GlobalValue.instance = null;
	}


	public static Canvas createNewCanvas(int width,int height,Device device){
		return new Canvas(width, height, device, global);
	}

	public static Canvas createNewCanvas(int width,int height,Device device,GlobalValue globalvalue,
			String author,String canvasname,long makeDay,long workTime,long actionCount){
		return new Canvas(width, height, device, globalvalue, author, canvasname, makeDay, workTime, actionCount);
	}

}
