package apainter.drawer;

import static java.lang.Math.*;

import java.awt.geom.Point2D;

/**
 * 点と点の間を、線、筆圧共に線形補完します。
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
	public void begin(DrawPoint e) {
		nextx=nowx = e.x;
		nexty=nowy = e.y;
		nextp=nowp = e.getPressure();
		distance = l=sin=0;
		cos = 1;
		isend = false;
	}

	@Override
	public void end(DrawPoint e) {
		if(plotend)isend = true;
		setNextPoint(e);
	}

	@Override
	public void setNextPoint(DrawPoint e) {
		double exl=l-distance;
		double nx =e.x,ny =e.y;
		double distance = hypot(nx-nextx, ny-nexty);
		if(exl >distance){
			if(isend){
				l=0;
				this.distance=distance;
			}
			return;
		}
		l=exl;
		nowx = nextx;
		nowy = nexty;
		nextx = nx;
		nexty = ny;
		nowp=nextp;
		nextp=e.getPressure();
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
		l+=length;
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
