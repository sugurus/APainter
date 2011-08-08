package apainter.canvas.layerdata;

import static apainter.GlobalKey.*;
import static apainter.misc.Util.*;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.Properties;
import apainter.canvas.Canvas;
import apainter.drawer.DrawAccepter;
import apainter.drawer.DrawEvent;

abstract public class LayerData implements DrawAccepter{
	protected final Canvas canvas;
	protected final GlobalValue global;
	protected LayerList layerlist = new LayerList();
	protected Mask mask;
	protected boolean hasSelectedArea = false;
	protected int layerid=0;


	//デフォルト
	public LayerData(Canvas canvas,GlobalValue globalvalue) {
		this.canvas = nullCheack(canvas, "canvas is null");
		global = nullCheack(globalvalue, "globalvalue is null");
		layerlist = new LayerList();
		LayerHandler lh;
		layerlist.addElement(lh=makeLayer(makelayerid(),null));
		layerlist.setSelectLayer(lh);
	}

	private int makelayerid(){
		return layerid++;
	}

	@Override
	public boolean  paint(DrawEvent e){
		LayerHandler l = e.getTarget();
		if(!layerlist.contains(l) || !l.isDrawable())return false;
		Layer layer = l.getLayer();
		if(hasSelectedArea){
			e.getOption().destinationmask=mask.getDataBuffer();
		}
		return layer.paint(e);
	}

	public LayerHandler getSelectedLayerHandler(){
		return layerlist.getSelectLayerHandler();
	}

	public ArrayList<LayerHandler> getLayerHandlers(){
		return layerlist.getAllLayerHandler();
	}


	protected Rectangle rect(){
		return new Rectangle(0,0,getWidth(),getHeight());
	}

	public int getWidth(){
		return canvas.getWidth();
	}

	public int getHeight(){
		return canvas.getHeight();
	}

	public void setSelectLayer(LayerHandler l){
		if(l==null)return;
		LayerHandler old = layerlist.getSelectLayerHandler();
		if(l==old)return;
		layerlist.setSelectLayer(l);
	}

	public void setSelectLayer(int layerid2) {
		layerlist.setSelectLayer(layerid2);
	}

	public LayerHandler getSelectedLayer(){
		return layerlist.getSelectLayerHandler();
	}


	/**
	 * 渡されたレイヤーをグループ内で一番最初に移動させます。<br>
	 * (※レンダリングが最初にされるという意味。見た目上は下にあるように見えるだろう)
	 * @param l
	 */
	public void moveToFirst(LayerHandler l){
		layerlist.moveIntoTop(l.getElement(), l.getElement().getUnit());
	}

	/**
	 * 渡されたレイヤーをグループ内で一番最後に移動させます。<br>
	 * (※レンダリングが最後にされるという意味。見た目は上にあるように見えるだろう)
	 * @param l
	 */
	public void moveToLast(LayerHandler l){
		layerlist.moveIntoLast(l.getElement(), l.getElement().getUnit());
	}

	/**
	 * next「を」beforeの次へ移動させます。
	 * @param next
	 * @param before
	 */
	public void moveToNext(LayerHandler next,LayerHandler before){
		layerlist.moveToNext(next.getElement(), before.getElement());
	}

	/**
	 * before「を」nextの前へ移動させます
	 */
	public void moveToBefore(LayerHandler before,LayerHandler next){
		layerlist.moveToBefore(before.getElement(), next.getElement());
	}

	/**
	 * 可能ならば、グループ内で一つ次に移動させます。
	 * @param l
	 */
	public void moveToNext(LayerHandler l){
		layerlist.moveToNext(l);
	}
	/**
	 * 可能ならば、グループ内で一つ前に移動させます。
	 * @param l
	 */
	public void moveToBefore(LayerHandler l){
		layerlist.moveToBefore(l);
	}


	/**
	 * 選択レイヤーの次に新たなレイヤーを追加します。
	 * @param layername
	 * @return
	 */
	public LayerHandler createLayer(String layername){
		LayerHandler lh= makeLayer(makelayerid(), layername);
		layerlist.addElement(lh);
		layerlist.moveToSelectedLayerNext(lh);
		//TODO historys
		return lh;
	}

	public boolean canRemove(LayerHandler lh){
		return layerlist.canRemove(lh);
	}

	public void remove(LayerHandler lh){
		if(!canRemove(lh))return;
		layerlist.remove(lh.getElement());
		//TODO history
	}

	/**
	 * レイヤーの並びを、階層を　「-」で表し、レイヤーをレイヤーidで表した文字列を返します。
	 */
	public String getLayerLine(){
		return layerlist.toString();
	}

	private LayerHandler makeLayer(int id,String layername){
		Layer l = createLayer(id,layername);
		return l.getHandler();
	}
	private int layernumber=1;

	protected String makeLayerName(String name){
		if(name!=null)return name;
		String s=global.getProperty(Properties.PropertyName_newlayername,"newlayer");
		s = s+layernumber;
		layernumber++;
		return s;
	}

	//TODO レイヤーの追加の外部関数

	/**
	 * @param id 作成するレイヤーに設定するid
	 * @return
	 */
	protected abstract Layer createLayer(int id,String layername);

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



	//------------listener----------------------------------------

	private EventListenerList eventlistenerlist = new EventListenerList();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
		eventlistenerlist.add(PropertyChangeListener.class, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
	}

	public void firePropertyChange(String name, Object oldValue, Object newValue) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue,
				newValue);

		Object[] listeners = eventlistenerlist.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}



}
