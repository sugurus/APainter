package apainter.drawer;

public interface PressureValueMaker {
	public void begin(double pressure);
	/**
	 * 次の点の筆圧と、次の点までの距離を設定します。
	 * @param pressure
	 * @param distance
	 */
	public void setNextPressure(double pressure,double distance);
	public void end(double pressure,double distance);

	/**
	 * 今の点と次の点をrato:1-ratoに内分する点での筆圧を返します。
	 * @param rato
	 */
	public double getPressure(double rato);

}
