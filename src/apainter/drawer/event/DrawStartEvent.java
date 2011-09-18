package apainter.drawer.event;

import apainter.drawer.DrawTarget;
import apainter.drawer.Drawer;

/**
 * Drawerがターゲットに対して書き込みイベントを生成しはじめた事を示すイベントです。
 */
public class DrawStartEvent extends DrawerEvent{
	public DrawStartEvent(Drawer source, DrawTarget target) {
		super(source, target);
	}

	@Override
	public String toString() {
		return "draw start event:Drawer["+drawer+"],DrawTarget["+target+"]";
	}
}
