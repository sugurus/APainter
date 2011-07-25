package apainter.canvas.event;

import static apainter.misc.Util.*;
import apainter.canvas.Canvas;

public class CanvasEvent {

	public final int id;
	public final Object source;
	public final long when;
	public final Canvas canvas;

	public CanvasEvent(int id,Object source,Canvas canvas) {
		this(id,source,System.currentTimeMillis(),canvas);
	}

	public CanvasEvent(int id,Object source,long time,Canvas canvas) {
		this.id = id;
		this.when = time;
		this.source = nullCheack(source,"source");
		this.canvas = nullCheack(canvas,"canvas");
	}

	public int getID() {
		return id;
	}

	public Object getSource() {
		return source;
	}

	public long getWhen() {
		return when;
	}

}
