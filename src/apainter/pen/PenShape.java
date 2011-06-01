package apainter.pen;

import java.awt.Dimension;

import apainter.data.PixelDataBuffer;

public interface PenShape {
	public PixelDataBuffer getFootPring(double x,double y,double w,double h);
	public Dimension getMapSize(double w,double h);
	public Dimension getSize();
	public double getIntervalLength(double w,double h);
	public void setIntervalLengthPercent(double percent);
	public String getName();
	public long getID();

}
