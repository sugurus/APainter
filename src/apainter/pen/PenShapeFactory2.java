package apainter.pen;

import apainter.Device;

/**
 * 設定などを指定しても、データの保持参照先は一つにしたいのでPenShapeFactoryから分離。<br>
 * 現状は名前の設定以外何もないけど、そのうち設定項目を追加したい。<br>
 * 分離しちゃったことでPenShapeFactoryの保持や参照の仕方を少し考えないといかんなぁ。
 * @author nodamushi
 *
 */
public interface PenShapeFactory2 extends PenShapeFactory{

	/**
	 * サイズは10倍された値です。1サイズは10です。
	 * @param size
	 * @param device
	 * @return
	 */
	public PenShape getPenShape(int size,Device device);
	public void setName(String name);
	/**
	 * 移動距離の割合を設定します。<br>
	 * 0より大の値です。基本は1です。
	 * @param n
	 */
	public void setMoveDistancePercent(double n);
	public double getMoveDistancePercent();
}

