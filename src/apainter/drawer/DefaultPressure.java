package apainter.drawer;

public class DefaultPressure implements PressureValueMaker{

	double now,next;

	@Override
	public void begin(double pressure) {
		now = next=pressure;
	}

	@Override
	public void setNextPressure(double pressure, double distance) {
		now = next;
		next = pressure;
	}

	@Override
	public void end(double pressure, double distance) {
		now = next;
		next = pressure;
	}

	@Override
	public double getPressure(double rato) {
		return now*(1-rato)+next*rato;
	}

}
