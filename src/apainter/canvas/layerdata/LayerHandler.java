package apainter.canvas.layerdata;

import java.awt.Image;

import apainter.canvas.Canvas;
import apainter.data.PixelDataBuffer;
import apainter.drawer.DrawEvent;
import apainter.rendering.ColorMode;

public abstract class LayerHandler implements LayerHandle,PixelSetable{

	/**
	 * 現在のレンダリングモードに使っているColorModeを返します
	 * @return
	 */
	public abstract ColorMode getRenderingMode();
	/**
	 * レンダリングに使うColorModeを設定します。<br>
	 * 利用不可能なColorModeが来た場合例外を発生させます。
	 * @param mode
	 */
	public abstract void setRenderingMode(ColorMode mode);
	/**
	 * 利用可能なColorModeを返します。
	 * @return
	 */
	public abstract ColorMode[] getUsableModes();
	public abstract Canvas getCanvas();


	abstract Layer getLayer();
	abstract PixelDataBuffer getOriginalData();
	abstract Image getImage();
	abstract PixelDataBuffer getMaskOriginalData();
	abstract Image getMaskImage();
	abstract PixelSetable getDrawable();


}
