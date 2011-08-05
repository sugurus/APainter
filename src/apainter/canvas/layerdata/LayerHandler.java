package apainter.canvas.layerdata;

import java.awt.Image;

import apainter.canvas.Canvas;
import apainter.data.PixelDataBuffer;
import apainter.hierarchy.Element;
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
	public abstract String getLayerTypeName();


	abstract Layer getLayer();
	abstract PixelDataBuffer getOriginalData();
	abstract Image getImage();
	abstract PixelDataBuffer getMaskOriginalData();
	abstract Image getMaskImage();
	abstract PixelSetable getDrawable();

	private Element<LayerHandler> thiselement=null;
	final synchronized void setElement(Element<LayerHandler> e){
		if(thiselement==null){
			thiselement=e;
		}
	}

	final Element<LayerHandler> getElement(){
		return thiselement;
	}

	final boolean hasElement(){
		return thiselement!=null;
	}

	private static final String p ="%s[ID:%d,Name:%s]";
	@Override
	public String toString() {
		return String.format(p, getLayerTypeName(),getID(),getName());
	}

}
