package apainter.drawer;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.bind.annotation.BindProperty;
import apainter.canvas.event.EventConstant;
import apainter.canvas.layerdata.LayerHandler;
import apainter.color.Color;
import apainter.construct.DimensionDouble;
import apainter.data.PixelDataBuffer;
import apainter.pen.PenShape;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

abstract public class Drawer {
	public static final String
		penShapeChangeProperty = "penshape";

	private double smin;// 筆圧最小時の筆の大きさの割合
	private int density_min;// 筆圧最小時の筆の濃度（0~256）
	//private int stabilization = 0;// 手ぶれ補正値
	private double density = 1;// ペン濃度 0～1
	private PenTabletMouseEvent before;
	private double length;//length ペンを書くのに少なくともどれほどの距離が必要か
	protected PenShape pen;

	protected DrawEvent start(PenTabletMouseEvent e,LayerHandler target){
		before = e;
		return createOneEvent(e, target);
	}

	private DrawEvent createOneEvent(PenTabletMouseEvent e,LayerHandler target){
		Point2D.Double xy=e.getPointDouble();
		double pressure =e.getPressure();
		DimensionDouble pensize = pen.getSize();
		pensize = getPenSize(pensize.width, pensize.height, pressure, xy.x, xy.y);
		PixelDataBuffer map = pen.getFootPrint(xy.x, xy.y, pensize.width, pensize.height);
		Rectangle bounds = new Rectangle((int)xy.x, (int)xy.y, map.width, map.height);
		Color front =getFrontColor(e, pen),back = getBackColor(e, pen);
		int dens =(int) ((((256-density_min)*pressure)+density_min)*density);
		RenderingOption option = new RenderingOption(front, back, null, dens);
		setOption(option,e);
		DrawEvent de =
			new DrawEvent(EventConstant.ID_PaintStart, this, target, bounds,
					getRenderer(), getUsableDevices(), map, option);
		length = pen.getIntervalLength(pensize.width, pensize.height);
		return de;
	}

	abstract protected Renderer getRenderer();
	abstract protected Device[] getUsableDevices();

	protected DrawEvent[] end(PenTabletMouseEvent e,LayerHandler target){
		PenTabletMouseEvent bef = before;
		Point2D.Double befp = bef.getPointDouble(),p = e.getPointDouble();
		double bx=befp.x,by = befp.y;
		double dl = Math.hypot(bx-p.x, by-p.y),l=length;
		if(dl < l){
			return new DrawEvent[]{createOneEvent(bef, target)};
		}

		DrawEvent[] ret = paint(bef, target);

		length = 0;
		before = null;

		return ret;
	}

	protected DrawEvent[] paint(PenTabletMouseEvent e,LayerHandler target){
		PenTabletMouseEvent bef = before;
		Point2D.Double befp = bef.getPointDouble(),p = e.getPointDouble();
		double bx=befp.x,by = befp.y;
		double dl = Math.hypot(bx-p.x, by-p.y),l=length;
		if(dl < l){
			return new DrawEvent[0];
		}
		double Opressure =bef.getPressure();
		double Ppressure = e.getPressure();
		DimensionDouble pensize = pen.getSize();
		double cos = (p.x-befp.x)/dl,sin =(p.y-befp.y)/dl;
		Renderer renderer = getRenderer();

		ArrayList<DrawEvent> es = new ArrayList<DrawEvent>();
		Device[] device = getUsableDevices();
		while(dl < l){
			double x = l*cos+bx,y = l*sin+by;
			double pressure =k(Opressure,Ppressure,l,dl);
			pensize = getPenSize(pensize.width, pensize.height, pressure, x, y);
			PixelDataBuffer map = pen.getFootPrint(x, y, pensize.width, pensize.height);
			Rectangle bounds = new Rectangle((int)x, (int)y, map.width, map.height);
			Color front =getFrontColor(e, pen),back = getBackColor(e, pen);
			int dens =(int) ((((256-density_min)*pressure)+density_min)*density);
			RenderingOption option = new RenderingOption(front, back, null, dens);
			setOption(option,e);
			DrawEvent de =
				new DrawEvent(EventConstant.ID_Paint, this, target,
						bounds, renderer, device, map, option);
			es.add(de);
			double d = pen.getIntervalLength(pensize.width, pensize.height);
			l += d;
		}
		before = e;
		length = l-dl;

		return es.toArray(new DrawEvent[es.size()]);
	}

	abstract protected Color getFrontColor(PenTabletMouseEvent e,PenShape pen);
	abstract protected Color getBackColor(PenTabletMouseEvent e,PenShape pen);
	abstract protected void setOption(RenderingOption option,PenTabletMouseEvent e);

	@BindProperty(penShapeChangeProperty)
	public void setPen(PenShape p){
		if(p!=null){
			PenShape old  =pen;
			pen = p;
			firePropertyChange(penShapeChangeProperty, old, pen);
		}
	}


	private double k(double o,double p,double t,double l){
		return o*(l-t)+p*t;
	}

	protected DimensionDouble getPenSize(double width,double height,double pressure,double x,double y){
		double t = ((1-smin) * pressure) + smin;
		double w = width * t, h = height * t;
		return new DimensionDouble(w, h);
	}



	private ArrayList<PropertyChangeListener> propertylistener = new ArrayList<PropertyChangeListener>();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (!propertylistener.contains(l))
			propertylistener.add(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertylistener.remove(l);
	}

	protected void firePropertyChange(String name, Object oldValue,
			Object newValue) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue,
				newValue);
		for (PropertyChangeListener l : propertylistener) {
			l.propertyChange(e);
		}
	}

}