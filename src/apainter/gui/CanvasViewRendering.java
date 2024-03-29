package apainter.gui;

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
	public void rendering(Rectangle[] rect);
	public void repaintMove();

	public void rotation();
	public void qualityRendering(boolean b);

	public void init();

	/**
	 * 使用されなくなったので、使用しているメモリを解放します。
	 */
	public void dispose();
	
	/**
	 * 拡大機能が使えるかどうかを返します
	 * @return
	 */
	public boolean isEnableZoom();
	/**
	 * 回転機能が使えるかどうかを返します。
	 * @return
	 */
	public boolean isEnableRotation();

}
