package apainter.drawer;


public interface Plot extends PlotPointInterpolate,PressureInterpolate{

	/**
	 * マウスが押された点を追加します。
	 * @param d
	 */
	public void begin(DrawPoint h);
	/**
	 * マウスがドラッグされた点を追加します。
	 * @param d
	 */
	public void setNextPoint(DrawPoint d);
	/**
	 * マウスがリリースされた端点を追加します。
	 * @param d
	 */
	public void end(DrawPoint d);

	/**
	 * まだ設置するべきか否か。
	 * @return
	 */
	public boolean hasNext();

	/**
	 * マウスがリリースされた端点に必ずプロットするか否か。<br>
	 * このフラグがtrueの時、move関数で端点を超えた場合でも、必ず端点にプロットしなくてはなりません。<br>
	 * falseの時はmove関数で端点を超えても、端点にプロットする必要はありません。<br>
	 * デフォルトの設定はfalseにしてください。
	 * @param b
	 */
	public void setEndPointPlot(boolean b);
	public boolean isEndPointPlot();
	/**
	 * lengthだけ、次にプロットする点を移動させます。<br>
	 * 1は1pixel幅を表します。
	 */
	public void move(double length);
}
