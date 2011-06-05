package apainter.drawer;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.Device;
import apainter.canvas.event.PainterEvent;
import apainter.canvas.layerdata.LayerHandler;
import apainter.data.PixelDataBuffer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

public class DrawEvent extends PainterEvent{

	private LayerHandler target;
	private Rectangle rect;
	private PixelDataBuffer mapdata;
	private RenderingOption option;
	private Renderer renderer;
	private Device[] device;

	public DrawEvent(int id, Drawer source,
			LayerHandler target,Rectangle bounds,
			Renderer renderer,Device[] device,
			PixelDataBuffer mapdata,RenderingOption option) {
		super(id, source);
		if(target==null)throw new NullPointerException("target");
		if(mapdata==null)throw new NullPointerException("mapdata");
		if(bounds==null)throw new NullPointerException("bounds");
		if(option==null)throw new NullPointerException("option");
		if(renderer == null)throw new NullPointerException("rendere");
		if(device==null)throw new NullPointerException("device");
		this.renderer = renderer;
		this.device = device;
		this.target = target;
		this.rect = bounds;
		this.mapdata = mapdata;
		this.option = option;
	}

	public boolean canUseDevice(Device d){
		for(Device dd:device)
			if(dd.equals(d))return true;
		return false;
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

	public LayerHandler getTarget(){
		return target;
	}

	public PixelDataBuffer getMapData(){
		return mapdata;
	}

}
