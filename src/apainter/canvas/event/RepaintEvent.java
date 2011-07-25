package apainter.canvas.event;

import static apainter.misc.Util.*;

import java.awt.Rectangle;

import apainter.canvas.Canvas;

public class RepaintEvent extends CanvasEvent{

	Rectangle rect;

	public RepaintEvent(int id, Object source,Rectangle r,Canvas canvas) {
		super(id, source,canvas);
		rect = nullCheack(r, "r is null");
	}
	/**
	 * コピーしないので中身を変更しないでください。
	 * @return
	 */
	public Rectangle getBounds(){
		return rect;
	}

}
