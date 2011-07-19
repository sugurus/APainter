package apainter.drawer.painttool;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.canvas.Canvas;
import apainter.color.Color;
import apainter.drawer.DrawEvent;
import apainter.drawer.Drawer;
import apainter.gui.canvas.CanvasMouseListener;
import apainter.pen.PenShape;

public abstract class BasicDrawer extends Drawer implements CanvasMouseListener{

	protected GlobalValue global;

	public BasicDrawer(GlobalValue global) {
		this.global = global;
	}
	private Canvas getCanvas(){
		return (Canvas)global.get(GlobalKey.CurrentCanvas);
	}

	@Override
	public void press(PenTabletMouseEvent e) {
		DrawEvent de=start(e, getCanvas().getSelectedLayerHandler());
		postEvent(de);
	}

	@Override
	public void drag(PenTabletMouseEvent e) {
		DrawEvent[] de=paint(e, getCanvas().getSelectedLayerHandler());
		postEvent(de);
	}

	@Override
	public void release(PenTabletMouseEvent e) {
		DrawEvent[] de=end(e, getCanvas().getSelectedLayerHandler());
		postEvent(de);
	}

	@Override
	public void move(PenTabletMouseEvent e) {

	}


	@Override
	protected Color getFrontColor(PenTabletMouseEvent e, PenShape pen) {
		return global.getFrontColor();
	}

	@Override
	protected Color getBackColor(PenTabletMouseEvent e, PenShape pen) {
		return global.getBackColor();
	}


	protected Device[] CPUOnly(){
		return new Device[]{Device.CPU};
	}
	protected Device[] GPUOnly(){
		return new Device[]{Device.GPU};
	}
	protected Device[] CPUandGPU(){
		return new Device[]{Device.CPU,Device.GPU};
	}


}
