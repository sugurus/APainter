package apainter.canvas.layerdata;

import apainter.Handler;
import apainter.canvas.CanvasHandler;
import apainter.canvas.CanvasHandlerElement;
import apainter.rendering.ColorMode;

public interface LayerHandler extends Handler,CanvasHandlerElement{

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
	 * マスクを持つことができるか否か。<br>
	 * この関数がfalseを返すとき、マスク関連のメソッドを呼び出しても意味がありません。
	 * @see LayerHandler#isEnableMask()
	 * @see LayerHandler#setEnableMask(boolean)
	 * @see LayerHandler#createMask()
	 * @return
	 */
	public boolean isMaskContainer();

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

	public boolean isGroup();
	public boolean isLayer();


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
	public String getLayerTypeName();
}
