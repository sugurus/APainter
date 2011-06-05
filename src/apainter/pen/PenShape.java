package apainter.pen;

import java.awt.Dimension;

import apainter.construct.DimensionDouble;
import apainter.data.PixelDataBuffer;

public interface PenShape {
	public PixelDataBuffer getFootPrint(double x,double y,double w,double h);
	public Dimension getMapSize(double w,double h);
	public DimensionDouble getSize();
	public double getIntervalLength(double w,double h);
	public void setIntervalLengthPercent(double percent);
	public String getName();
	public long getID();

}
