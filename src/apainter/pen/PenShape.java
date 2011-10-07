package apainter.pen;

import java.awt.Dimension;
import java.awt.Point;

import apainter.data.PixelData;
import apainter.data.PixelDataContainer;

/**
 * sizeとは、１０倍された値になっています。
 * 1 -> 0.1
 * これは、0.1が浮動小数で表せないためです。
 * @author nodamushi
 *
 */
public interface PenShape {
	public PixelDataContainer getFootPrint(double x,double y);
	public Point getCenterPoint();
	public Dimension getMapSize();
	public int getSize();
	public double getMoveDistance();
	public void setIntervalLengthPercent(double percent);
	public String getName();
	public long getID();

}
