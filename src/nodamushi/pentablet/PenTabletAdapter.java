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
	@Override public void mouseDragged(PenTabletMouseEvent e) {}
	@Override public void mouseMoved(PenTabletMouseEvent e) {}
	@Override public void mousePressed(PenTabletMouseEvent e) {}
	@Override public void mouseReleased(PenTabletMouseEvent e) {}
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void mouseOperatorChanged(PenTabletMouseEvent e) {}
	@Override public void mouseEntered(PenTabletMouseEvent e) {}
	@Override public void mouseExited(PenTabletMouseEvent e) {}
}
