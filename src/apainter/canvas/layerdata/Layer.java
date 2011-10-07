package apainter.canvas.layerdata;

import apainter.CreateHandler;
import apainter.data.PixelData;
import apainter.rendering.RenderingOption;

/**
 * paint関数は例外を投げることがあります。
 * @author nodamushi
 *
 */
interface Layer extends LayerHandle,Renderable,CreateHandler{

	/**
	 * このレイヤーを操作するためのハンドラーを返します。
	 * @return
	 */
	public InnerLayerHandler getHandler();

	public RenderingOption getRenderingOption();
	public PixelData getDataBuffer();
}
