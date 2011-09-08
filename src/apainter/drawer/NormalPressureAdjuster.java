package apainter.drawer;

/**
 * 渡された筆圧をそのまま返します。<br>
 * このクラスはシングルトンパターンで、getInstance関数でインスタンスを取得します。
 */
public final class NormalPressureAdjuster implements PressureAdjuster{
	static final NormalPressureAdjuster obj=new NormalPressureAdjuster();
	public static NormalPressureAdjuster getInstance(){return obj;}

	private  NormalPressureAdjuster(){}
	@Override
	public double adjustPressure(double pressure) {
		return pressure;
	}

	@Override
	public String toString() {
		return "Normal Pressure Adjuster";
	}
}
