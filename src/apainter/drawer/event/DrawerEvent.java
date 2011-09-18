package apainter.drawer.event;

import static apainter.misc.Util.*;
import apainter.drawer.DrawTarget;
import apainter.drawer.Drawer;

/**
 * Drawerが生成するイベントです。<br>
 * DrawEvent（フットプリントを設置するイベント）<br>
 * DrawStartEvent（線を描画しはじめたイベント）<br>
 * DrawLastEvent(線を描画し終わったイベント）<br>
 * の3つが子クラスとして定義されています。
 */
public abstract class DrawerEvent {
	public final Drawer drawer;
	public final DrawTarget target;
	DrawerEvent(Drawer d,DrawTarget target) {
		this.drawer = nullCheack(d);
		this.target = nullCheack(target);
	}

	/**
	 * このイベントのターゲットを返します。
	 * @return
	 */
	public DrawTarget getTarget(){
		return target;
	}

	/**
	 * このイベントを生成したDrawerを返します
	 * @return
	 */
	public Drawer getDrawer(){
		return drawer;
	}

}
