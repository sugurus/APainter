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

	/**
	 * 指定された範囲レンダリングするフラグを立てます。<br>
	 * さらにここでここで処理をするか否かはデバイスによります。
	 * @param r
	 */
	public void renderingFlag(Rectangle r);

}
