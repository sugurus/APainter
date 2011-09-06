package apainter.canvas.layerdata;

import apainter.canvas.Canvas;

public interface LayerHandle{
	/**
	 * レイヤーもしくはグループのオリジナルのIDを返します。<br>
	 * 他のインスタンスとかぶってはいけません。
	 * @see LayerData#getNextID()
	 * @return
	 */
	public int getID();
	/**
	 * レイヤーの名前を返します。
	 * @return
	 */
	public String getName();
	/**
	 * レイヤーの設定をします。<br>
	 * nullの時""が設定されます。
	 * @param name
	 */
	public void setName(String name);
	/**
	 * レイヤーやグループの透明度を設定します。<br>
	 * 値は0～256です。
	 * @param transparent
	 */
	public void setTransparent(int transparent);
	/**
	 * レイヤーやグループの透明度を返します
	 * @return
	 */
	public int getTransparent();

	/**
	 * 可視性を返します。falseの時レンダリングされず、描き込むことができません。
	 * @return
	 */
	public boolean isVisible();
	/**
	 * レイヤーやグループの可視性を設定します。
	 * @param b
	 */
	public void setVisible(boolean b);

	/**
	 * DrawEventを介して書き込みが可能かどうか。<br>
	 * グループでは不可になります。
	 * @return
	 */
	public boolean isDrawable();
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

	public boolean isPixelContainer();
	public boolean isPixelSetable();

	public boolean isGroup();
	public boolean isLayer();
	public Canvas getCanvas();

	/**
	 * このレイヤーデータを完全に破棄します。
	 */
	public void dispose();


}
