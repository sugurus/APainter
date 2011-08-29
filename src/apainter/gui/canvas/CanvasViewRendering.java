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
	public void repaintMove();

	public void rotation();
	public void qualityRendering(boolean b);

	public void init();


}
