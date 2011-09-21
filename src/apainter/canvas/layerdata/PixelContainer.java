package apainter.canvas.layerdata;

import apainter.ColorType;
import apainter.data.OutBoundsException;

interface PixelContainer {
	/**
	 * 点(x,y)の色を返します。<br>
	 * この値はカラーの場合は0～255の8bitの値がARGBの値が格納されています。<br>
	 * グレースケールの場合は0～255の8bitの値が一つ格納されています。
	 * @param x
	 * @param y
	 * @return (x,y)地点の色を返します。
	 * @throws OutBoundsException x,yが画像の範囲外の場合例外が発生します。
	 */
	public int getPixel(int x,int y)throws OutBoundsException;
	/**
	 * 格納している色のタイプを返します。
	 * @return
	 */
	public ColorType getColorType();
	/**
	 * 範囲の色データを入れた配列を返します。<br>
	 * 値の格納のされ方についてはgetPixelと同じです。
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return コピーした配列
	 * @throws OutBoundsException 画像の範囲外の領域が含まれる場合例外が発生します。
	 */
	public int[] getPixels(int x,int y,int width,int height)throws OutBoundsException;
	/**
	 * 指定された範囲内の色を渡された配列にコピーします。<br>
	 * @param x 始点
	 * @param y 始点
	 * @param width 幅
	 * @param height 高さ
	 * @param destination コピー先の配列
	 * @return コピーした配列
	 * @throws OutBoundsException 画像の範囲外の領域が含まれる場合例外が発生します。
	 * @throws ArrayIndexOutOfBoundsException 配列長が足りないとき起こります。
	 */

	public int[] copyPixels(int x,int y,int width,int height,int[] destination)
			throws OutBoundsException,ArrayIndexOutOfBoundsException;
}
