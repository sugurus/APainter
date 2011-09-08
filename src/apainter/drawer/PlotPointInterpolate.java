package apainter.drawer;

import java.awt.geom.Point2D;

public interface PlotPointInterpolate {

	/**
	 * 次にプロットする点を返します。<br>
	 *
	 * @return
	 */
	public Point2D getPlotPoint();
}
