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
	 * 保持しているメモリーを解放します。<br>
	 * 一度呼び出すとインスタンスは二度と利用できません。
	 */
	public void dispose();
	/**
	 * このレイヤーを操作するためのハンドラーを返します。
	 * @return
	 */
	public LayerHandler getHandler();

	public RenderingOption getRenderingOption();

}
