package apainter.canvas.layerdata;

import java.awt.Rectangle;

import javax.swing.JComponent;

import apainter.canvas.Canvas;
import apainter.drawer.DrawAccepter;
import apainter.drawer.DrawEvent;

abstract public class LayerData implements DrawAccepter{
	protected final Canvas canvas;
	protected LayerList layerlist = new LayerList();


	public LayerData(Canvas canvas) {
		if(canvas==null)throw new NullPointerException("canvas");
		this.canvas = canvas;
		layerlist = new LayerList();
		LayerHandler lh;
		layerlist.addElement(lh=makeLayer(12));
		layerlist.setSelectLayer(lh);
		lh.setTransparent(127);
		layerlist.addElement(lh=makeLayer(3));
		lh.setTransparent(127);
	}

	@Override
	public boolean  paint(DrawEvent e){
		LayerHandler l = e.getTarget();
		if(!layerlist.contains(l) || !l.isDrawable())return false;
		Layer layer = l.getLayer();
		return layer.paint(e);
	}

	public LayerHandler getSelectedLayerHandler(){
		return layerlist.getSelectLayerHandler();
	}

	public int getWidth(){
		return canvas.getWidth();
	}

	public int getHeight(){
		return canvas.getHeight();
	}

	private LayerHandler makeLayer(int id){
		Layer l = createLayer(id);
		return l.getHandler();
	}
	//TODO レイヤーの追加の外部関数

	/**
	 * @param id 作成するレイヤーに設定するid
	 * @return
	 */
	protected abstract Layer createLayer(int id);

	/*TODO Group作成
	 * private LayerHandler makeGroup(int id){
	 * Layer l = createGroup(id);
	 * return l.getHandler();
	 * }
	 *
	 * protected abstract Group  createGropu(int id);
	 */


	public abstract void rendering();
	public abstract void rendering(Rectangle clip);

	public abstract JComponent testMethod_createViewPanel();

}
