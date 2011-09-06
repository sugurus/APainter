package apainter.drawer;
import static java.lang.Math.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Color;
import apainter.Device;
import apainter.bind.Bind;
import apainter.bind.BindObject;
import apainter.canvas.event.EventConstant;
import apainter.canvas.layerdata.InnerLayerHandler;
import apainter.data.PixelDataBuffer;
import apainter.pen.PenShape;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

abstract public class Drawer {

	public Drawer(int id) {
		this.id = id;
	}


	protected void postEvent(DrawEvent e){
		if(e==null)return;
			e.canvas.dispatchEvent(e);
	}
	protected void postEvent(DrawEvent[] events){
		for(DrawEvent e:events){
			if(e==null)continue;
				postEvent(e);
		}
	}

	public int getID(){
		return id;
	}

	public void setDensity(double d){
		if(d < 0)d = 0;
		else if(d > 1)d = 1;
		density = d;
	}

	public double getDensity(){
		return density;
	}

	public void setMinDensity(double d){
		if(d < 0)d = 0;
		else if(d > 1)d = 1;
		density_min = (int) (256*d);
	}

	public double getMinDensity(){
		return density_min/256d;
	}
	public void setMinSize(double d){
		if(d < 0)d = 0;
		else if(d > 1)d = 1;
		smin = d;
	}

	public double getMinSize(){
		return smin;
	}

	protected DrawEvent start(PenTabletMouseEvent e,InnerLayerHandler target,Device d){
		plot.begin(e);
		pressure.begin(e.getPressure());
		DrawEvent de = createOneEvent(e, target, d);
		before = e;
		return de;
	}

	private DrawEvent createOneEvent(PenTabletMouseEvent e,InnerLayerHandler target,Device d){
		Point2D plotpoint;
		double pressure =this.pressure.getPressure(0);
		int pensize = pen.getSize();
		pensize = getPenSize(pensize, pressure);
		if(pensize==0){
			return null;
		}
		double length = pen.getMoveDistance(pensize);
		plotpoint = plot.getNext();
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

	abstract protected Renderer getRenderer(Device d);
	abstract protected Device[] getUsableDevices();

	protected DrawEvent[] end(PenTabletMouseEvent e,InnerLayerHandler target,Device dv){
		plot.end(e);
		pressure.end(e.getPressure(),plot.getDistance());
		ArrayList<DrawEvent> des = createEvents(e, target, dv);
		if(endDraw) IF:{
			double rato = plot.getMoveRato();
			if(rato<1){
				Point2D p = plot.getPoint(1);
				double x = p.getX(),y=p.getY();
				int pensize = pen.getSize();
				double pres =pressure.getPressure(plot.getMoveRato());
				int pensize2 = getPenSize(pensize, pres);
				Renderer renderer = getRenderer(dv);
				if(pensize2==0){
					break IF;
				}
				PixelDataBuffer map = pen.getFootPrint(x, y, pensize2);
				Point center = pen.getCenterPoint(pensize2);
				Rectangle bounds = new Rectangle((int)x-center.x, (int)y-center.y,
						map.width, map.height);
				Color front =getFrontColor(e, pen),back = getBackColor(e, pen);
				int dens =(int) ((((256-density_min)*pres)+density_min)*density);
				RenderingOption option = new RenderingOption(front, back, null, dens);
				setOption(option,e);
				DrawEvent de =
					new DrawEvent(EventConstant.ID_Paint, this, target,
							bounds, bounds.getLocation(),renderer,  map, option);
				des.add(de);
			}
		}

		return toArray(des);
	}
	protected DrawEvent[] paint(PenTabletMouseEvent e,InnerLayerHandler target,Device dv){
		plot.setNextPoint(e);
		pressure.setNextPressure(e.getPressure(), plot.getDistance());
		ArrayList<DrawEvent> es= createEvents(e, target, dv);
		before = e;
		return toArray(es);
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
			plotpoint = plot.getNext();
			double x = plotpoint.getX(),y = plotpoint.getY();
			double pres =pressure.getPressure(plot.getMoveRato());
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

	//TODO 変更通知
	public void setPen(PenShape p){
		if(p!=null){
			PenShape old  =pen;
			pen = p;
		}
	}


	protected int getPenSize(int size,double pressure){
		double t = ((1-smin) * pressure) + smin;
		return (int) (size*t);
	}


	private final int id;
	private double smin=0;// 筆圧最小時の筆の大きさの割合
	private int density_min=256;// 筆圧最小時の筆の濃度（0~256）
	//private int stabilization = 0;// 手ぶれ補正値
	private double density = 0.5;// ペン濃度 0～1
	private PenTabletMouseEvent before=null;
	private PlotPointMaker plot=new DefaultPlot();
	private PressureValueMaker pressure = new DefaultPressure();

	protected PenShape pen;
	private boolean endDraw = false;
}
