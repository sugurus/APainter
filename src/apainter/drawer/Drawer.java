package apainter.drawer;
import static java.lang.Math.*;
import static apainter.PropertyChangeNames.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Color;
import apainter.Device;
import apainter.canvas.event.CanvasEvent;
import apainter.canvas.event.EventConstant;
import apainter.canvas.layerdata.InnerLayerHandler;
import apainter.data.PixelDataBuffer;
import apainter.misc.PropertyChangeUtility;
import apainter.pen.PenShape;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

/**
 * プロパティー<br><br>
 * ペンの形状の変化。<br>
 * 　PenShapeChangeProperty="penshapechange"<br>
 * マウスのリリース点に必ずプロットするかどうかのフラグの変化<br>
 *  　PlotEndPointChangeProperty="plotendpointchange"<br>
 * Plotの変化<br>
 * 　PlotChangeProperty="plotchange"<br>
 * PressureAdjusterの変化<br>
 *　PressureAdjusterChangeProperty="penadjusterchange"<br>
 *筆の基本濃度の変化<br>
 *　DrawerDensityChangeProperty="drawerdensitychange"<br>
 *筆圧最小時の密度の変化<br>
 *　DrawerMinDensityChangeProperty="drawermindensitychange"<br>
 *筆圧最小時のペンサイズの変化
 *　DrawerMinPenSizeChangeProperty="drawerminpensizechange"

 */
abstract public class Drawer {

	public Drawer(int id) {
		this.id = id;
	}

	protected CanvasEvent[] start(PenTabletMouseEvent e,InnerLayerHandler target,Device d){
		plot.begin(e);
		DrawEvent de = createOneEvent(e, target, d);
		return new CanvasEvent[]{new PaintStartEvent(0, this, target),de};
	}
	protected CanvasEvent[] drawLine(PenTabletMouseEvent e,InnerLayerHandler target,Device dv){
		plot.setNextPoint(e);
		ArrayList<DrawEvent> es= createEvents(e, target, dv);
		return es.toArray(new CanvasEvent[es.size()]);
	}
	protected CanvasEvent[] end(PenTabletMouseEvent e,InnerLayerHandler target,Device dv){
		plot.end(e);
		ArrayList<DrawEvent> des = createEvents(e, target, dv);
		CanvasEvent[] ret =des.toArray(new CanvasEvent[des.size()+1]);
		ret[ret.length-1] = new PaintLastEvent(0, this, target);
		return ret;
	}



	protected void postEvent(CanvasEvent e){
		if(e==null)return;
		e.canvas.dispatchEvent(e);
	}

	protected void postEvent(CanvasEvent[] events){
		for(CanvasEvent e:events){
			if(e==null)continue;
				postEvent(e);
		}
	}

	private DrawEvent createOneEvent(PenTabletMouseEvent e,InnerLayerHandler target,Device d){
		Point2D plotpoint;
		double pressure =plot.getPressure();
		int pensize = pen.getSize();
		pensize = getPenSize(pensize, pressure);
		if(pensize==0){
			return null;
		}
		double length = pen.getMoveDistance(pensize);
		plotpoint = plot.getPlotPoint();
		plot.move(length);
		double x = plotpoint.getX(),y=plotpoint.getY();
		PixelDataBuffer map = pen.getFootPrint(x, y, pensize);
		Point center = pen.getCenterPoint(pensize);



		Rectangle bounds = new Rectangle((int)floor(x) -center.x, (int)floor(y)-center.y, map.width, map.height);
		Color front =getFrontColor(e, pen),back = getBackColor(e, pen);
		int dens =(int) ((((256-density_min)*pressure)+density_min)*density);
		RenderingOption option = new RenderingOption(front, back, null, dens);
		setOption(option,e);
		DrawEvent de =
			new DrawEvent(EventConstant.ID_PaintStart, this, target, bounds,bounds.getLocation(),
					getRenderer(d), map, option);
		return de;
	}




	private static DrawEvent[] toArray(ArrayList<DrawEvent> l){
		return l.toArray(new DrawEvent[l.size()]);
	}

	protected ArrayList<DrawEvent> createEvents(PenTabletMouseEvent e,InnerLayerHandler target,Device dv){
		if(!plot.hasNext())return new ArrayList<DrawEvent>();
		Point2D plotpoint;
		int pensize = pen.getSize();
		Renderer renderer = getRenderer(dv);

		ArrayList<DrawEvent> es = new ArrayList<DrawEvent>();
		Device[] device = getUsableDevices();
		while(plot.hasNext()){
			plotpoint = plot.getPlotPoint();
			double x = plotpoint.getX(),y = plotpoint.getY();
			double pres =plot.getPressure();
			int size = getPenSize(pensize, pres);
			if(size==0){
				plot.move(1/16d);
				continue;
			}
			PixelDataBuffer map = pen.getFootPrint(x, y, size);
			Point center = pen.getCenterPoint(size);
			Rectangle bounds = new Rectangle((int)x-center.x, (int)y-center.y,
					map.width, map.height);
			Color front =getFrontColor(e, pen),back = getBackColor(e, pen);
			int dens =(int) ((((256-density_min)*pres)+density_min)*density);
			RenderingOption option = new RenderingOption(front, back, null, dens);
			setOption(option,e);
			DrawEvent de =
				new DrawEvent(EventConstant.ID_Paint, this, target,
						bounds, bounds.getLocation(),renderer,  map, option);
			es.add(de);
			double d = pen.getMoveDistance(size);
			plot.move(d);
		}
		return es;
	}


	abstract protected Color getFrontColor(PenTabletMouseEvent e,PenShape pen);
	abstract protected Color getBackColor(PenTabletMouseEvent e,PenShape pen);
	abstract protected void setOption(RenderingOption option,PenTabletMouseEvent e);
	abstract protected Renderer getRenderer(Device d);
	abstract protected Device[] getUsableDevices();

	//TODO 変更通知
	public void setPen(PenShape p){
		if(p!=null){
			PenShape old  =pen;
			pen = p;
			firePropertyChange(PenShapeChangeProperty, old, p);
		}
	}

	public boolean isPlotEnd(){
		return plot.isEndPointPlot();
	}

	public void setPlotEnd(boolean b){
		if(plot.isEndPointPlot()!=b){
			plot.setEndPointPlot(b);
			firePropertyChange(PlotEndPointChangeProperty, !b, b);
		}
	}

	public void setPlot(Plot plot){
		if(plot==null||plot==this.plot)return;
		Object old = this.plot;
		boolean b = this.plot.isEndPointPlot();
		plot.setEndPointPlot(b);
		plot.setPressureAdjuster(padj);
		this.plot = plot;
		firePropertyChange(PlotChangeProperty, old, plot);
	}

	protected int getPenSize(int size,double pressure){
		double t = ((1-smin) * pressure) + smin;
		return (int) (size*t);
	}

	public void setPressureAdjuster(PressureAdjuster p){
		if(p==null||padj==p)return;
		Object old = padj;
		padj = p;
		plot.setPressureAdjuster(p);
		firePropertyChange(PressureAdjusterChangeProperty, old, p);
	}

	public int getID(){
		return id;
	}

	public void setDensity(double d){
		if(density==d)return;
		if(d < 0)d = 0;
		else if(d > 1)d = 1;
		double old = density;
		density = d;
		firePropertyChange(DrawerDensityChangeProperty, old, d);
	}

	public double getDensity(){
		return density;
	}

	public void setMinDensity(double d){
		if(density_min==d)return;
		if(d < 0)d = 0;
		else if(d > 1)d = 1;
		double old = density_min;
		density_min = (int) (256*d);
		firePropertyChange(DrawerMinDensityChangeProperty, old, d);
	}

	public double getMinDensity(){
		return density_min/256d;
	}
	public void setMinSize(double d){
		if(smin==d)return;
		if(d < 0)d = 0;
		else if(d > 1)d = 1;
		double old = smin;
		smin = d;
		firePropertyChange(DrawerMinPenSizeChangeProperty, old, d);
	}

	public double getMinSize(){
		return smin;
	}

	private final int id;
	private double smin=0;// 筆圧最小時の筆の大きさの割合
	private int density_min=256;// 筆圧最小時の筆の濃度（0~256）
	private double density = 0.5;// ペン濃度 0～1
	private Plot plot=new LinerPlot();
	{
		plot.setEndPointPlot(true);
	}
	private PressureAdjuster padj=NormalPressureAdjuster.obj;

	protected PenShape pen;


	private EventListenerList elist = new EventListenerList();
	public void addPropertyChangeEventListener(PropertyChangeListener l){
		PropertyChangeUtility.addPropertyChangeListener(l, elist);
	}
	public void removePropertyChangeEventListener(PropertyChangeListener l){
		PropertyChangeUtility.removePropertyChangeListener(l, elist);
	}
	protected void firePropertyChange(String name,Object oldValue,Object newValue){
		PropertyChangeUtility.firePropertyChange(name, oldValue, newValue, this, elist);
	}
}
