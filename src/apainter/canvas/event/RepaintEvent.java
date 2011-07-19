package apainter.canvas.event;

import java.awt.Rectangle;

public class RepaintEvent extends PainterEvent{

	Rectangle rect;

	public RepaintEvent(int id, Object source,Rectangle r) {
		super(id, source);
		if(r==null)throw new NullPointerException("r");
		rect = r;
	}
	/**
	 * コピーしないので中身を変更しないでください。
	 * @return
	 */
	public Rectangle getBounds(){
		return rect;
	}

}
