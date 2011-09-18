package apainter.canvas.layerdata;

import static apainter.misc.Util.*;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import apainter.GlobalValue;
import apainter.Properties;
import apainter.canvas.Canvas;
import apainter.canvas.event.PaintEvent;
import apainter.canvas.event.PaintEventAccepter;
import apainter.drawer.DrawTarget;
import apainter.drawer.event.DrawEvent;

abstract public class LayerData{
	protected final Canvas canvas;
	protected final GlobalValue global;
	protected LayerList layerlist = new LayerList();
	protected Mask mask;
	protected boolean hasSelectedArea = false;
	protected int layerid=0;
	protected boolean drawMask=false;


	//デフォルト
	public LayerData(Canvas canvas,GlobalValue globalvalue) {
		this.canvas = nullCheack(canvas, "canvas is null");
		global = nullCheack(globalvalue, "globalvalue is null");
		layerlist = new LayerList();
		InnerLayerHandler lh;
		layerlist.addElement(lh=makeLayer(makelayerid(),null));
		layerlist.setSelectLayer(lh);
	}

	private int makelayerid(){
		return layerid++;
	}

	public boolean paint(PaintEvent e){
		PaintEventAccepter l = e.getTarget();
		if(!l.isPaintable())return false;
		if (l instanceof InnerLayerHandler) {
			InnerLayerHandler lh = (InnerLayerHandler) l;
			if(!layerlist.contains(lh))return false;
			Layer layer = lh.getLayer();
			if(hasSelectedArea){
				e.getOption().destinationmask=mask.getDataBuffer();
			}
			return layer.paint(e);
		}

		//TODO mask
		if(l instanceof MaskHandler){
			MaskHandler m =(MaskHandler)l;
			if(hasSelectedArea){
				e.getOption().destinationmask=mask.getDataBuffer();
			}
			return m.getMask().paint(e);
		}

		return false;
	}

	public InnerLayerHandler getSelectedLayerHandler(){
		return layerlist.getSelectLayerHandler();
	}

	public ArrayList<InnerLayerHandler> getAllInnerLayerHandlers(){
		return layerlist.getAllLayerHandler();
	}

	public LayerHandler[] getAllLayerHandlers(){
		ArrayList<InnerLayerHandler> a = getAllInnerLayerHandlers();
		LayerHandler[] ret = new LayerHandler[a.size()];
		int i=0;
		for(InnerLayerHandler l:a){
			ret[i++] = l.getLayerHandler();
		}
		return ret;
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

	public void setSelectLayer(InnerLayerHandler l){
		setSelectLayer(l,false);
	}

	public void setSelectLayer(int layerid) {
		setSelectLayer(layerlist.getLayer(layerid));
	}

	//メインsetSelectLayer
	public void setSelectLayer(InnerLayerHandler l,boolean drawMask){
		if(l==null)return;
		InnerLayerHandler old = layerlist.getSelectLayerHandler();
		if(l==old&&drawMask==this.drawMask)return;
		//TODO マスクを持っているかどうかの判定が必要。
		this.drawMask = drawMask;
		layerlist.setSelectLayer(l);
	}

	public InnerLayerHandler getSelectedLayer(){
		return layerlist.getSelectLayerHandler();
	}

	public DrawTarget getDrawTarget(){
		InnerLayerHandler l = getSelectedLayer();
		if(!l.isDrawable() && !drawMask){
			return null;
		}
		if(drawMask){
			Layer layer = l.getLayer();
			Mask mask = layer.getMask();
			return mask.getHandler();
		}
		return l;

	}



	/**
	 * 渡されたレイヤーをグループ内で一番最初に移動させます。<br>
	 * (※レンダリングが最初にされるという意味。見た目上は下にあるように見えるだろう)
	 * @param l
	 */
	public void moveToFirst(InnerLayerHandler l){
		layerlist.moveIntoTop(l.getElement(), l.getElement().getUnit());
	}

	/**
	 * 渡されたレイヤーをグループ内で一番最後に移動させます。<br>
	 * (※レンダリングが最後にされるという意味。見た目は上にあるように見えるだろう)
	 * @param l
	 */
	public void moveToLast(InnerLayerHandler l){
		layerlist.moveIntoLast(l.getElement(), l.getElement().getUnit());
	}

	/**
	 * next「を」beforeの次へ移動させます。
	 * @param next
	 * @param before
	 */
	public void moveToNext(InnerLayerHandler next,InnerLayerHandler before){
		layerlist.moveToNext(next.getElement(), before.getElement());
	}

	/**
	 * before「を」nextの前へ移動させます
	 */
	public void moveToBefore(InnerLayerHandler before,InnerLayerHandler next){
		layerlist.moveToBefore(before.getElement(), next.getElement());
	}

	/**
	 * 可能ならば、グループ内で一つ次に移動させます。
	 * @param l
	 */
	public void moveToNext(InnerLayerHandler l){
		layerlist.moveToNext(l);
	}
	/**
	 * 可能ならば、グループ内で一つ前に移動させます。
	 * @param l
	 */
	public void moveToBefore(InnerLayerHandler l){
		layerlist.moveToBefore(l);
	}


	/**
	 * 選択レイヤーの次に新たなレイヤーを追加します。
	 * @param layername
	 * @return
	 */
	public InnerLayerHandler createLayer(String layername){
		InnerLayerHandler lh= makeLayer(makelayerid(), layername);
		layerlist.addElement(lh);
		layerlist.moveToSelectedLayerNext(lh);
		//TODO historys
		return lh;
	}

	public boolean canRemove(InnerLayerHandler lh){
		return layerlist.canRemove(lh);
	}

	public void remove(InnerLayerHandler lh){
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

	private InnerLayerHandler makeLayer(int id,String layername){
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


	/**
	 * 保存用のBufferedImage作成、レンダリングし、返します
	 * @return
	 */
	public abstract BufferedImage createImage();



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

	public InnerLayerHandler getLayer(int id) {
		return layerlist.getLayer(id);
	}

	public void dispose(){
		for(InnerLayerHandler l:getAllInnerLayerHandlers()){
			l.getLayer().dispose();
		}
	}



}
