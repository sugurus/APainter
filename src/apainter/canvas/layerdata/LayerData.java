package apainter.canvas.layerdata;

import java.awt.Rectangle;

import javax.swing.JComponent;

import apainter.canvas.Canvas;
import apainter.drawer.DrawEvent;

abstract public class LayerData {
	protected final Canvas canvas;
	private int nextID=0;
	protected LayerList layerlist = new LayerList();


	public LayerData(Canvas canvas) {
		if(canvas==null)throw new NullPointerException("canvas");
		this.canvas = canvas;
		layerlist = new LayerList();
		LayerHandler lh;
		layerlist.addElement(lh=makeLayer());
		lh.setTransparent(127);
		layerlist.addElement(lh=makeLayer());
		lh.setTransparent(127);

	}

	public int getWidth(){
		return canvas.getWidth();
	}

	public int getHeight(){
		return canvas.getHeight();
	}

	public LayerHandler makeLayer(){
		Layer l = createLayer();
		return l.getHandler();
	}

	protected abstract Layer createLayer();

	/*TODO Group作成
	 * public LayerHandler makeGroup(){
	 * Layer l = createGroup();
	 * return l.getHandler();
	 * }
	 *
	 * protected abstract Group  createGropu();
	 */

	abstract void draw(DrawEvent e);

	abstract void rendering();
	abstract void rendering(Rectangle clip);

	public abstract JComponent testMethod_createViewPanel();

	/**
	 * レイヤーが設定すべきIDを返します。
	 * @return
	 */
	int getNextLayerID(){
		return nextID++;
	}

}
