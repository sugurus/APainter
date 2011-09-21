package apainter.drawer.painttool;

import static apainter.GlobalKey.*;
import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Color;
import apainter.Device;
import apainter.GlobalValue;
import apainter.canvas.Canvas;
import apainter.drawer.DrawPoint;
import apainter.drawer.DrawTarget;
import apainter.drawer.Drawer;
import apainter.drawer.event.DrawerEvent;
import apainter.gui.CanvasMouseListener;
import apainter.pen.PenShape;

public abstract class BasicDrawer extends Drawer implements CanvasMouseListener{

	protected GlobalValue global;

	public BasicDrawer(GlobalValue global,int id) {
		super(id);
		this.global = global;
	}


	protected Device getDevice(){
		return global.get(OnDevice, Device.class);
	}

	protected DrawTarget getTarget(Canvas canvas){
		DrawTarget d= canvas.getDrawTarget();
		if(d==null)return null;
		return isDrawable(d)?d:null;
	}
	protected abstract boolean isDrawable(DrawTarget dt);

	@Override
	public void press(PenTabletMouseEvent e,Canvas canvas) {
		DrawTarget d = getTarget(canvas);
		if(d==null){
			cancel(canvas);
			return;
		}
		DrawerEvent[] de=start(DrawPoint.convert(e), d,getDevice());
		postEvent(de);
	}
	@Override
	public void drag(PenTabletMouseEvent e,Canvas canvas) {
		DrawTarget d = getTarget(canvas);
		if(d==null){
			cancel(canvas);
			return;
		}
		DrawerEvent[] de=drawLine(DrawPoint.convert(e),d,getDevice());
		postEvent(de);
	}

	@Override
	public void release(PenTabletMouseEvent e,Canvas canvas) {
		DrawTarget d = getTarget(canvas);
		if(d==null){
			cancel(canvas);
			return;
		}
		DrawerEvent[] de=end(DrawPoint.convert(e), d,getDevice());
		postEvent(de);
	}

	private void cancel(Canvas canvas){
		//TODO かけないときの処理
	}

	@Override
	public void move(PenTabletMouseEvent e,Canvas canvas) {

	}


	@Override
	protected Color getFrontColor(DrawPoint e, PenShape pen) {
		return global.getFrontColor();
	}

	@Override
	protected Color getBackColor(DrawPoint e, PenShape pen) {
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
