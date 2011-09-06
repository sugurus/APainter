package apainter.canvas.layerdata;

import apainter.drawer.DrawAccepter;
import apainter.rendering.RenderingOption;

/**
 * paint関数は例外を投げることがあります。
 * @author nodamushi
 *
 */
interface Layer extends LayerHandle,Renderable,DrawAccepter{

	/**
	 * このレイヤーを操作するためのハンドラーを返します。
	 * @return
	 */
	public InnerLayerHandler getHandler();

	public RenderingOption getRenderingOption();

}
