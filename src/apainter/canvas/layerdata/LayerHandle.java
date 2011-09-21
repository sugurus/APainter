package apainter.canvas.layerdata;

import apainter.canvas.event.PaintEventAccepter;

public interface LayerHandle extends PaintEventAccepter{
	/**
	 * レイヤーもしくはグループのオリジナルのIDを返します。<br>
	 * 他のインスタンスとかぶってはいけません。
	 * @see LayerData#getNextID()
	 * @return
	 */
	public int getID();
	/**
	 * レイヤーの設定をします。<br>
	 * nullの時""が設定されます。
	 * @param name
	 */
	public void setName(String name);

	/**
	 * マスクを持つことができるかどうか。
	 * @return
	 */
	public boolean isMaskContainer();

	/**
	 * isMaskContainerがtrueをかえすとき、MaskContainerを返します。<br>
	 * それ以外の時はnull
	 * @return
	 */
	public MaskContainer getMaskContainer();

	/**
	 * レイヤーの名前を返します。
	 * @return
	 */
	public String getName();

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

	public boolean isPixelContainer();

	public boolean isGroup();
	public boolean isLayer();

	/**
	 * このレイヤーデータを完全に破棄します。
	 */
	public void dispose();


}
