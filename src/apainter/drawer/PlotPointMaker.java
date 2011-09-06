package apainter.drawer;

import java.awt.geom.Point2D;

import nodamushi.pentablet.PenTabletMouseEvent;

public interface PlotPointMaker {

	public void begin(PenTabletMouseEvent e);
	/**
	 *
	 * @param e
	 */
	public void end(PenTabletMouseEvent e);
	public void setNextPoint(PenTabletMouseEvent e);
	public Point2D getNext();

	/**
	 * 次の点までの「移動距離」を返します。
	 * @return
	 */
	public double getDistance();

	/**
	 * 移動距離が前の点から次の点までの間をrato:1-ratoにする点の位置を返します。
	 * @param rato 0～1　それ以外の時はnullを返す
	 * @return
	 */
	public Point2D getPoint(double rato);

	/**
	 * lサイズだけ、次にプロットする点を移動させます。
	 * @param l
	 */
	public void move(double length);

	/**
	 * まだ設置するべきか否か。
	 * @return
	 */
	public boolean hasNext();


	public double getMoveRato();

}
