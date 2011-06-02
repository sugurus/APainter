package apainter.canvas.event;

public class PainterEvent {

	public final int id;
	public final Object source;
	public final long when;

	public PainterEvent(int id,Object source) {
		this(id,source,System.currentTimeMillis());
	}

	public PainterEvent(int id,Object source,long time) {
		if(source == null)throw new NullPointerException("source");
		this.id = id;
		this.when = time;
		this.source = source;
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
