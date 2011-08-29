package demo.colorpicker.hsv;

import javax.swing.Icon;

public interface HSelecterIcon extends Icon
{
	/**
	 * 現在選択選択されているHの値
	 * @return
	 */
	public double getH();
	/**
	 * アイコンの座標系で点(x,y)のHの値。
	 * @param x
	 * @param y
	 * @return
	 */
	public double getH(int x,int y);

	/**
	 * Hを設定します。
	 */
	public void setH(double h);

	/**
	 * SVを設定します。
	 * @param s
	 * @param v
	 */
	public void setSV(double s,double v);

	/**
	 * SVの状態が見えるようにするかどうか設定します。
	 * @param b
	 */
	public void setVisibleSV(boolean b);
	/**
	 * SVの状態が見えているかどうか返します
	 * @return
	 */
	public boolean isVisibleSV();


}
