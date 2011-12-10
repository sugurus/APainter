package apainter.pen;

import java.awt.Point;

import apainter.data.PixelDataContainer;

/**
 * sizeとは、10倍された値になっています。
 * 1 -> 0.1
 * これは、0.1等が浮動小数で表せないためです。
 * @author nodamushi
 *
 */
public interface PenShape {
	public PixelDataContainer getFootPrint(double x,double y);
	/**
	 * 
	 * @param x 中心座標の小数部
	 * @param y 中心座標の小数部
	 * @return
	 */
	public Point getCenterPoint(double x,double y);
	public int getSize();
	public double getMoveDistance();
	public String getName();
	public long getID();

}
