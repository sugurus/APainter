package apainter.canvas.layerdata;

public interface Layer extends LayerHandle{

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
}
