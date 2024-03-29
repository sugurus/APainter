package apainter.canvas.layerdata;

import java.awt.Rectangle;

import apainter.data.PixelData;
import apainter.rendering.ColorMode;

interface Renderable {
	/**
	 * destinationの原点にあわせてrの範囲をdestinationに描き込みます。
	 * @param destination
	 * @param r
	 */
	public void render(PixelData destination,Rectangle r);

	/**
	 * 現在のレンダリングモードに使っているColorModeを返します
	 * @return
	 */
	public ColorMode getRenderingMode();
	/**
	 * レンダリングに使うColorModeを設定します。<br>
	 * 利用不可能なColorModeが来た場合例外を発生させます。
	 * @param mode
	 */
	public void setRenderingMode(ColorMode mode);
	/**
	 * 利用可能なColorModeを返します。
	 * @return
	 */
	public ColorMode[] getUsableModes();
}
