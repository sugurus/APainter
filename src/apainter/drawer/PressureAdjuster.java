package apainter.drawer;

/**
 *　得られた筆圧に調節を加えるためのインターフェースです。
 */
public interface PressureAdjuster {
	/**
	 * 与えられた筆圧に対応する調節された筆圧を返します
	 * @param pressure 調節する筆圧
	 * @return 調節した結果
	 */
	public double adjustPressure(double pressure);
}
