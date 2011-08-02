package nodamushi.pentablet;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;

/**
 * PenTabletRecognizerのアダプタークラスです。<br>
 * ユーザーは必要なメソッドだけオーバーライドできます。
 * @author nodamushi
 *
 */
public abstract class PenTabletAdapter extends PenTabletRecognizer{
	public PenTabletAdapter(Component c) {super(c);}
	@Override public void onDragged(PenTabletMouseEvent e) {}
	@Override public void onMove(PenTabletMouseEvent e) {}
	@Override public void onPressed(PenTabletMouseEvent e) {}
	@Override public void onReleased(PenTabletMouseEvent e) {}
	@Override public void onScroll(MouseWheelEvent e) {}
	@Override public void operatorChanged(PenTabletMouseEvent e) {}
	@Override public void onEnter(PenTabletMouseEvent e) {}
	@Override public void onExit(PenTabletMouseEvent e) {}
}
