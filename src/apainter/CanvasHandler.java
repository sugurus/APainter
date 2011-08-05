package apainter;

import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import apainter.canvas.Canvas;

/**
 * Canvasのアダプタークラス
 * @author nodamushi
 *
 */
public class CanvasHandler {
	final Canvas canvas;
	final APainter ap;

	CanvasHandler(Canvas c,APainter a) {
		canvas = c;
		this.ap=a;
	}


	public APainter getAPainter(){
		return ap;
	}

	public JComponent getComponent(){
		return canvas.getCanvasView();
	}


	public int getID(){
		return canvas.getID();
	}

	public int getWidth() {
		return canvas.getWidth();
	}

	public int getHeight() {
		return canvas.getHeight();
	}

	public String getAuthor() {
		return canvas.getAuthor();
	}

	public void setAuthor(String s){
		canvas.setAuthor(s);
	}

	public long getMakeDay() {
		return canvas.getMakeDay();
	}

	public long getWorkTime() {
		return canvas.getWorkTime();
	}

	public long getActionCount() {
		return canvas.getActionCount();
	}

	public long getCreatedTime() {
		return canvas.getCreatedTime();
	}

	public String getCanvasName() {
		return canvas.getCanvasName();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		canvas.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		canvas.removePropertyChangeListener(l);
	}

}
