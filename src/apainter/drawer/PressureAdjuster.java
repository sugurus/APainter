package apainter.drawer;

/**
 *　得られた筆圧に調節を加えるためのインターフェースです。
 */
public interface PressureAdjuster {
	/**
	 * 与えられた筆圧に対応する調節された筆圧を返します。
	 */
	public double adjustPressure(double pressure);
}
