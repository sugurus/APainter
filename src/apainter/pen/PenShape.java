package apainter.pen;

import java.awt.Dimension;

import apainter.Device;
import apainter.construct.DimensionDouble;
import apainter.data.PixelDataBuffer;

public interface PenShape {
	/**
	 * CPUの場合はPixelDataIntBuffer
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public PixelDataBuffer getFootPrint(double x,double y,double w,double h);
	public DimensionDouble getSize();
	public double getIntervalLength(double w,double h);
	public void setIntervalLengthPercent(double percent);
	public String getName();
	public long getID();

}
