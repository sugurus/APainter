package apainter.drawer;

import apainter.drawer.event.DrawEvent;
import apainter.drawer.event.DrawLastEvent;
import apainter.drawer.event.DrawStartEvent;
import apainter.drawer.event.DrawerEvent;

/**
 * Drawerが生成するイベントのターゲットを定義します。
 */
public interface DrawTarget {
	/**
	 * Drawerが生成したイベントを処理します。
	 * @param e DrawEvent,DrawStartEvent,DrawLastEventのいずれか
	 * @see DrawerEvent
	 * @see DrawEvent
	 * @see DrawStartEvent
	 * @see DrawLastEvent
	 */
	public void acceptEvent(DrawerEvent e);
	/**
	 * このターゲットの名前を返します。<br>
	 * 形式<br>
	 * name datatype colortype bitsize<br>
	 * <br><br>
	 * name:このターゲット名<br>
	 * datatype:intやbyteなどの格納形式<br>
	 * colortype:argbまたはrgbまたはgray<br>
	 * bitsize:一つの色データ（赤や緑）のビットサイズ<br>
	 * 例；<br>
	 * cpulayer int argb 8
	 * @return
	 */
	public String getDrawTargetName();
}
