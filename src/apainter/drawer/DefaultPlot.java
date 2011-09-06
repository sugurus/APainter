package apainter.drawer;

import static java.lang.Math.*;

import java.awt.geom.Point2D;

import nodamushi.pentablet.PenTabletMouseEvent;

public class DefaultPlot implements PlotPointMaker{
	double distance;//元の点と、次の点の距離
	double l;//今までに進んだ距離
	double nowx,nowy;
	double nextx,nexty;
	double cos,sin;

	@Override
	public double getDistance() {
		return distance;
	}

	@Override
	public void begin(PenTabletMouseEvent e) {
		Point2D.Double d = e.getPointDouble();
		nextx=nowx = d.x;
		nexty=nowy = d.y;
		distance = l=sin=0;
		cos = 1;
	}

	@Override
	public void end(PenTabletMouseEvent e) {
		setNextPoint(e);
	}

	@Override
	public void setNextPoint(PenTabletMouseEvent e) {
		double exl=l-distance;
		Point2D.Double d = e.getPointDouble();
		double nx =d.x,ny =d.y;
		double distance = hypot(nx-nextx, ny-nexty);
		if(exl >distance)return;
		l=exl;
		nowx = nextx;
		nowy = nexty;
		nextx = nx;
		nexty = ny;
		if(distance < 0.00001){
			this.distance=0;
			cos=1;
			sin=0;
		}else{
			this.distance = distance;
			cos = (nextx-nowx)/distance;
			sin = (nexty-nowy)/distance;
		}
	}

	@Override
	public Point2D getPoint(double rato) {
		if(rato>1 || rato<0)return null;
		double l = distance * rato;
		double x = nowx+l*cos;
		double y = nowy+l*sin;
		return new Point2D.Double(x,y);
	}

	@Override
	public Point2D getNext() {
		if(distance<l)return null;
		double x = nowx+l*cos;
		double y = nowy+l*sin;
		return new Point2D.Double(x,y);
	}

	@Override
	public void move(double length) {
		l+=length;
	}

	@Override
	public boolean hasNext() {
		return distance >=l;
	}
	@Override
	public double getMoveRato() {
		return distance==0?1:l/distance;
	}

}
