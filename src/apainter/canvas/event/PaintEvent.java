package apainter.canvas.event;

import static apainter.misc.Util.*;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelData;
import apainter.data.PixelDataContainer;
import apainter.drawer.Drawer;
import apainter.drawer.event.DrawEvent;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

public class PaintEvent extends CanvasEvent{

	public static PaintEvent convert(DrawEvent e,PaintEventAccepter l,int id){
		return new PaintEvent(id, e.getDrawer(), l, e.getBounds(), e.getLocation(),
				e.getRenderer(), e.getFootprint(), e.getOption());
	}

	private PaintEventAccepter target;
	private Rectangle rect;
	private Point setPoint;
	private PixelDataContainer mapdata;
	private RenderingOption option;
	private Renderer renderer;

	public PaintEvent(int id, Drawer source,
	PaintEventAccepter target,Rectangle bounds,Point setpoint,
	Renderer renderer,PixelDataContainer mapdata,RenderingOption option) {
	super(id, source,target.getCanvas());
	if(mapdata==null)throw new NullPointerException("mapdata");
	if(bounds==null)throw new NullPointerException("bounds");
	if(option==null)throw new NullPointerException("option");
	if(renderer == null)throw new NullPointerException("rendere");
	this.renderer = renderer;
	this.target = target;
	this.rect = bounds;
	this.mapdata = mapdata;
	this.option = option;
	setPoint =nullCheack(setpoint);
	}

	public PaintEvent subsetEvent(Rectangle r){
	if(!rect.contains(r)){
	throw new RuntimeException("r isn't subset");
	}
	return new PaintEvent(id, getSource(), target, r,setPoint, renderer, mapdata, option);
	}


	public Renderer getRenderer(){
	return renderer;
	}

	@Override
	public Drawer getSource() {
	return (Drawer)super.getSource();
	}

	public Rectangle getBounds(){
	return (Rectangle) rect.clone();
	}

	public Point getLocation(){
	return setPoint;
	}

	public RenderingOption getOption(){
	return option;
	}

	public PaintEventAccepter getTarget(){
	return target;
	}

	public PixelDataContainer getMapData(){
	return mapdata;
	}


}
