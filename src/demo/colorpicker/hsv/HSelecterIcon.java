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
	 * HSVを設定します。
	 * @param s
	 * @param v
	 */
	public void setHSV(double h,double s,double v);

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
