package apainter.pen;

import java.awt.Dimension;
import java.awt.Point;

import apainter.data.PixelDataBuffer;

/**
 * sizeとは、１０倍された値になっています。
 * 1 -> 0.1
 * これは、0.1が浮動小数で表せないためです。
 * @author nodamushi
 *
 */
public interface PenShape {
	/**
	 * CPUの場合はPixelDataIntBuffer
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public PixelDataBuffer getFootPrint(double x,double y,int size);
	public Point getCenterPoint(int size);
	public Dimension getMapSize();
	public int getSize();
	public double getMoveDistance(int size);
	public void setIntervalLengthPercent(double percent);
	public String getName();
	public long getID();

}
