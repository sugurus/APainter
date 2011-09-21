package apainter.canvas.layerdata;

public interface MaskContainer {

	/**
	 * マスクが有効になっているかどうか
	 * @return
	 */
	public boolean isEnableMask();
	/**
	 * マスクの有効性を設定します。<br>
	 * falseを指定してもデータが消えるわけではありません。
	 * @param b
	 */
	public void setEnableMask(boolean b);
	/**
	 * 新たなマスクを作成します。<br>
	 * 前提条件として、isEnableMaskの返す値がfalseである必要があります。<br>
	 * そうでないとき例外が発生します。
	 * @see LayerHandle#isEnableMask() isEnableMask
	 */
	public void createMask();
	public MaskHandler getMask();
}
