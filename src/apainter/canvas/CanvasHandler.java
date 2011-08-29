package apainter.canvas;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import apainter.APainter;
import apainter.BindKey;
import apainter.bind.BindObject;
import apainter.canvas.event.CanvasEvent;
import apainter.gui.canvas.CanvasView;

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

	public void repaintOnlyMove(){
		canvas.getCanvasView().repaintMove();
	}

	public void repaint(){
		canvas.getCanvasView().rendering();
	}

	public void repaint_only_rotation(){
		canvas.getCanvasView().rendering_rotation();
	}

	public void bind(BindObject bind,BindKey key){
		CanvasView v = canvas.getCanvasView();

		switch(key){
		case CanvasPositionBIND:
			v.addposBindObject(bind);
			break;
		case ZoomBIND:
			v.addzoomBindObject(bind);
			break;
		case AngleBIND:
			v.addangleBindObject(bind);
			break;
		case ReverseBIND:
			v.addreverseBindObject(bind);
			break;

		}
	}

	public void unbind(BindObject bind,BindKey key){
		CanvasView v = canvas.getCanvasView();

		switch(key){
		case CanvasPositionBIND:
			v.removeposBindObject(bind);
			break;
		case ZoomBIND:
			v.removezoomBindObject(bind);
			break;
		case AngleBIND:
			v.removeangleBindObject(bind);
			break;
		case ReverseBIND:
			v.removereverseBindObject(bind);
			break;

		}
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


	public void dispatchEvent(CanvasEvent e){
		canvas.dispatchEvent(e);
	}


	public void setViewQuality(boolean b) {
		canvas.getCanvasView().setQuarityRendering(b);

	}

	public BufferedImage getImage(){
		return canvas.createSaveImage();
	}


	public void dispose() {
		canvas.dispose();
	}

}
