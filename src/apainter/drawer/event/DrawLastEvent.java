package apainter.drawer.event;

import apainter.drawer.DrawTarget;
import apainter.drawer.Drawer;

/**
 * Drawerが書き込みイベントを全て生成し終えたことを示すイベントです。
 */
public class DrawLastEvent extends DrawerEvent{
	public DrawLastEvent(Drawer source, DrawTarget target) {
		super(source,target);
	}

	@Override
	public String toString() {
		return "draw last event:Drawer["+drawer+"],DrawTarget["+target+"]";
	}

}
