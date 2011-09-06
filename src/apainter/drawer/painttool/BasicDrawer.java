package apainter.drawer.painttool;

import static apainter.GlobalKey.*;
import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Color;
import apainter.Device;
import apainter.GlobalValue;
import apainter.canvas.Canvas;
import apainter.drawer.DrawEvent;
import apainter.drawer.Drawer;
import apainter.gui.canvas.CanvasMouseListener;
import apainter.pen.PenShape;

public abstract class BasicDrawer extends Drawer implements CanvasMouseListener{

	protected GlobalValue global;

	public BasicDrawer(GlobalValue global,int id) {
		super(id);
		this.global = global;
	}

	@Override
	public void press(PenTabletMouseEvent e,Canvas canvas) {
		DrawEvent de=start(e, canvas.getSelectedLayer(),getDevice());
		postEvent(de);
	}

	protected Device getDevice(){
		return global.get(OnDevice, Device.class);
	}

	@Override
	public void drag(PenTabletMouseEvent e,Canvas canvas) {
		DrawEvent[] de=paint(e, canvas.getSelectedLayer(),getDevice());
		postEvent(de);
	}

	@Override
	public void release(PenTabletMouseEvent e,Canvas canvas) {
		DrawEvent[] de=end(e, canvas.getSelectedLayer(),getDevice());
		postEvent(de);
	}

	@Override
	public void move(PenTabletMouseEvent e,Canvas canvas) {

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
