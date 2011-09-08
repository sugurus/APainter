package apainter.drawer;

public interface PressureInterpolate {

	/**
	 * 現在の点での筆圧を返します。
	 * @return 筆圧
	 */
	public double getPressure();

	/**
	 * PressureAdjusterを設定します。<br>
	 * pがnullの場合は無視してください。
	 * @param p
	 */
	public void setPressureAdjuster(PressureAdjuster p);
	/**
	 * 設定されているPressureAdjusterを返します。
	 * @return
	 */
	public PressureAdjuster getPressureAdjuster();

}
