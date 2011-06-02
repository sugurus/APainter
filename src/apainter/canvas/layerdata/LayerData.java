package apainter.canvas.layerdata;

import apainter.canvas.Canvas;

abstract public class LayerData {
	private final Canvas canvas;
	private int nextID=0;


	public LayerData(Canvas canvas) {
		if(canvas==null)throw new NullPointerException("canvas");
		this.canvas = canvas;
	}

	public int getWidth(){
		return canvas.getWidth();
	}

	public int getHeight(){
		return canvas.getHeight();
	}

	/**
	 * レイヤーが設定すべきIDを返します。
	 * @return
	 */
	int getNextID(){
		return nextID++;
	}

}
