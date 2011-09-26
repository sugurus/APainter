package apainter.canvas;

public interface CanvasAction {
	/**
	 * 選択状態になりました。
	 */
	public void selected();
	/**
	 * 非選択状態になりました。<br>
	 */
	public void unselected();
	/**
	 * 選択状態を返して下さい。
	 * @return selectedが最後に呼ばれていればtrue
	 */
	public boolean isSelected();
}
