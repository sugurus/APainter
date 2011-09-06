package apainter.drawer;
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
		DrawEvent de = createOneEvent(e, target, d);
		before = e;
		return de;
	}

	private DrawEvent createOneEvent(PenTabletMouseEvent e,InnerLayerHandler target,Device d){
		Point2D.Double xy=e.getPointDouble();
		double pressure =e.getPressure();
		int pensize = pen.getSize();
		pensize = getPenSize(pensize, pressure, xy.x, xy.y);
		if(pensize==0){
			length = 0;
			return null;
		}
		PixelDataBuffer map = pen.getFootPrint(xy.x, xy.y, pensize);


		Rectangle bounds = new Rectangle((int)xy.x -(map.width>>1), (int)xy.y-(map.height>>1), map.width, map.height);
		Color front =getFrontColor(e, pen),back = getBackColor(e, pen);
		int dens =(int) ((((256-density_min)*pressure)+density_min)*density);
		RenderingOption option = new RenderingOption(front, back, null, dens);
		setOption(option,e);
		DrawEvent de =
			new DrawEvent(EventConstant.ID_PaintStart, this, target, bounds,bounds.getLocation(),
					getRenderer(d), map, option);
		length = pen.getIntervalLength(pensize);
		return de;
	}

	abstract protected Renderer getRenderer(Device d);
	abstract protected Device[] getUsableDevices();

	protected DrawEvent[] end(PenTabletMouseEvent e,InnerLayerHandler target,Device d){
		PenTabletMouseEvent bef = before;
		Point2D.Double befp = bef.getPointDouble(),p = e.getPointDouble();
		double bx=befp.x,by = befp.y;
		double dl = Math.hypot(bx-p.x, by-p.y),l=length;
		if(endDraw&&dl < l){
			return new DrawEvent[]{createOneEvent(bef, target,d)};
		}

		DrawEvent[] ret = paint(bef, target,d);

		length = 0;
		before = null;

		return ret;
	}

	protected DrawEvent[] paint(PenTabletMouseEvent e,InnerLayerHandler target,Device dv){
		PenTabletMouseEvent bef = before;
		Point2D.Double befp = bef.getPointDouble(),p = e.getPointDouble();
		double bx=befp.x,by = befp.y;
		double dl = Math.hypot(bx-p.x, by-p.y),l=length;
		if(dl < l){
			return new DrawEvent[0];
		}
		double Opressure =bef.getPressure();
		double Ppressure = e.getPressure();
		int pensize = pen.getSize();
		double cos = (p.x-befp.x)/dl,sin =(p.y-befp.y)/dl;
		Renderer renderer = getRenderer(dv);

		ArrayList<DrawEvent> es = new ArrayList<DrawEvent>();
		Device[] device = getUsableDevices();
		while(l < dl){
			double x = l*cos+bx,y = l*sin+by;
			double pressure =k(Opressure,Ppressure,l,dl);
			int pensize2 = getPenSize(pensize, pressure, x, y);
			if(pensize2==0){
				l += 1/16d;
				continue;
			}
			PixelDataBuffer map = pen.getFootPrint(x, y, pensize2);
			Rectangle bounds = new Rectangle((int)x-(map.width>>1), (int)y-(map.height>>1),
					map.width, map.height);
			Color front =getFrontColor(e, pen),back = getBackColor(e, pen);
			int dens =(int) ((((256-density_min)*pressure)+density_min)*density);
			RenderingOption option = new RenderingOption(front, back, null, dens);
			setOption(option,e);
			DrawEvent de =
				new DrawEvent(EventConstant.ID_Paint, this, target,
						bounds, bounds.getLocation(),renderer,  map, option);
			es.add(de);
			double d = pen.getIntervalLength(pensize2);
			l += d;
		}
		before = e;
		length = l-dl;

		return es.toArray(new DrawEvent[es.size()]);
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


	private double k(double o,double p,double t,double l){
		return (o*(l-t)+p*t)/l;
	}

	protected int getPenSize(int size,double pressure,double x,double y){
		double t = ((1-smin) * pressure) + smin;
		return (int) (size*t);
	}


	private final int id;
	private double smin=0;// 筆圧最小時の筆の大きさの割合
	private int density_min=256;// 筆圧最小時の筆の濃度（0~256）
	//private int stabilization = 0;// 手ぶれ補正値
	private double density = 0.5;// ペン濃度 0～1
	private PenTabletMouseEvent before=null;
	private double length;//length ペンを書くのに少なくともどれほどの距離が必要か
	protected PenShape pen;
	private boolean endDraw = false;
}
