package apainter.drawer;


public interface DrawAccepter {
	/**
	 * ピクセルに対し変更を加えることを通知します。<br>
	 * @param source
	 */
	public abstract void startPaint(Object source);
	public abstract void endPaint(Object source);

	public boolean paint(DrawEvent e);
}
