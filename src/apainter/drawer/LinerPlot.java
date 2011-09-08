package apainter.drawer;

import static java.lang.Math.*;

import java.awt.geom.Point2D;

import nodamushi.pentablet.PenTabletMouseEvent;
/**
 * 点と点の間を、線、筆圧共に線形補完します。
 * @author ruby
 *
 */
public class LinerPlot implements Plot{
	double distance;//元の点と、次の点の距離
	double l;//今までに進んだ距離
	double nowx,nowy;
	double nextx,nexty;
	double cos,sin;
	double nowp,nextp;
	boolean plotend = false;
	boolean isend=false;
	PressureAdjuster adj=NormalPressureAdjuster.obj;

	@Override
	public PressureAdjuster getPressureAdjuster() {
		return adj;
	}
	@Override
	public void setPressureAdjuster(PressureAdjuster p) {
		if(p==null)return;
		adj=p;
	}


	@Override
	public void begin(PenTabletMouseEvent e) {
		Point2D.Double d = e.getPointDouble();
		nextx=nowx = d.x;
		nexty=nowy = d.y;
		nextp=nowp = e.getPressure();
		distance = l=sin=0;
		cos = 1;
		isend = false;
	}

	@Override
	public void end(PenTabletMouseEvent e) {
		if(plotend)isend = true;
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
	public Point2D getPlotPoint() {
		if(distance<l)return null;
		double x = nowx+l*cos;
		double y = nowy+l*sin;
		return new Point2D.Double(x,y);
	}

	@Override
	public void move(double length) {
		if(isend && l < distance && l+length >distance){
			l =distance;
		}else l+=length;
	}

	@Override
	public boolean hasNext() {
		return distance >=l;
	}

	@Override
	public double getPressure() {
		double rato =distance==0?1: l/distance;
		double p= nowp*(1-rato)+nextp*rato;
		return adj.adjustPressure(p);
	}

	@Override
	public boolean isEndPointPlot() {
		return plotend;
	}
	@Override
	public void setEndPointPlot(boolean b) {
		plotend = b;
	}

	@Override
	public String toString() {
		return "Liner Plot[plot end point:"+plotend+" PressureAdjuster:"+adj+"]";
	}

}