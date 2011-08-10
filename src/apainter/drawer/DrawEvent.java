package apainter.drawer;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.Device;
import apainter.canvas.event.CanvasEvent;
import apainter.canvas.layerdata.InnerLayerHandler;
import apainter.data.PixelDataBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

public class DrawEvent extends CanvasEvent{

	private InnerLayerHandler target;
	private Rectangle rect;
	private PixelDataBuffer mapdata;
	private RenderingOption option;
	private Renderer renderer;

	public DrawEvent(int id, Drawer source,
			InnerLayerHandler target,Rectangle bounds,
			Renderer renderer,PixelDataBuffer mapdata,RenderingOption option) {
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
	}

	public DrawEvent subsetEvent(Rectangle r){
		if(!rect.contains(r)){
			throw new RuntimeException("r isn't subset");
		}
		return new DrawEvent(id, getSource(), target, r, renderer,  mapdata, option);
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
		return rect.getLocation();
	}

	public RenderingOption getOption(){
		return option;
	}

	public InnerLayerHandler getTarget(){
		return target;
	}

	public PixelDataBuffer getMapData(){
		return mapdata;
	}

}
