package apainter.gui.canvas;

import java.awt.Rectangle;

public interface CanvasViewRendering {
	/**
	 * 全てをレンダリングし直します。
	 */
	public void rendering();

	/**
	 * 指定された範囲レンダリングします。
	 * @param r
	 */
	public void rendering(Rectangle r);


}
