package apainter.canvas.layerdata;

import static apainter.PropertyChangeNames.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import apainter.bind.annotation.BindProperty;
import apainter.hierarchy.Element;
import apainter.hierarchy.Hierarchy;
import apainter.hierarchy.Unit;


public class LayerList extends Hierarchy<LayerHandler>{

	private Map<Integer, LayerHandler> alllayer = new HashMap<Integer, LayerHandler>();
	private LayerHandler selectedLayer;
	private boolean selectedMask=false;


	@BindProperty(SelectedLayerChangeProperty)
	public void setSelectLayer(LayerHandler l){
		if(l==null)return;
		if(selectedLayer !=l){
			selectedMask = false;
			LayerHandler old = selectedLayer;
			selectedLayer = l;
			if(l.getElement().isUnit()){
				setCurrentUnit((Unit<LayerHandler>)l.getElement());
			}else{
				setCurrentUnit(l.getElement().getUnit());
			}
			firePropertyChange(SelectedLayerChangeProperty, old, l);
		}
	}

	public ArrayList<LayerHandler> getAllLayerHandler(){
		return new ArrayList<LayerHandler>(alllayer.values());
	}

	public void setSelectLayer(int id){
		setSelectLayer(alllayer.get(id));
	}

	public LayerHandler getSelectLayerHandler(){
		return selectedLayer;
	}

	public boolean isSelectedMask(){
		return selectedMask;
	}

	@Override
	protected void add(Element<LayerHandler> e) {
		LayerHandler l = e.getProperty();
		alllayer.put(l.getID(), l);
		l.setElement(e);
		super.add(e);
	}

	public synchronized boolean canRemove(LayerHandler l){
		Unit<LayerHandler> unit = getTopLevelUnit();
		Element<LayerHandler> ll = l.getElement();
		int size = unit.size();
		for(int i=0;i<size;i++){
			Element<LayerHandler> lh = unit.getElement(i);
			if(ll==lh)continue;
			if(lh.isUnit()){
				if(_canRemove((Unit<LayerHandler>)lh, ll))
					return true;
			}
			else return true;
		}
		return false;
	}

	private boolean _canRemove(Unit<LayerHandler> unit,Element<LayerHandler> l){
		int size = unit.size();
		for(int i=0;i<size;i++){
			Element<LayerHandler> lh = unit.getElement(i);
			if(l==lh)continue;
			if(lh.isUnit()){
				if(_canRemove((Unit<LayerHandler>)lh, l))return true;
			}
			else return true;
		}
		return false;
	}



	@Override
	protected void readdAll(Collection<Element<LayerHandler>> e) {
		for(Element<LayerHandler> el:e){
			LayerHandler l = el.getProperty();
			alllayer.put(l.getID(), l);
		}
		super.readdAll(e);
	}

	@Override
	protected void remove(Element<LayerHandler> e) {
		LayerHandler l = e.getProperty();
		alllayer.remove(l.getID());
		super.remove(e);
	}

	@Override
	protected void removeAll(Collection<Element<LayerHandler>> e) {
		for(Element<LayerHandler> el:e){
			LayerHandler l = el.getProperty();
			alllayer.remove(l.getID());
		}
		super.removeAll(e);
	}


	@BindProperty(SelectedMaskChangeProperty)
	public void setSelectedMask(boolean b){
		if(b!=selectedMask){
			if( ( b && selectedLayer.isEnableMask()) || !b){
				selectedMask = b;
				firePropertyChange(SelectedMaskChangeProperty, !b, b);
			}
		}
	}


	public void moveToNext(LayerHandler l){
		Element<LayerHandler> e = l.getElement();
		Unit<LayerHandler> u = e.getUnit();
		int pos = e.getIndex()+1;
		int size = u.size();
		if(pos==size)return;
		Element<LayerHandler> before = u.getElement(pos);
		moveToNext(e, before);
	}

	public void moveToBefore(LayerHandler l){
		Element<LayerHandler> e = l.getElement();
		Unit<LayerHandler> u = e.getUnit();
		int pos = e.getIndex()-1;
		if(pos<0)return;
		Element<LayerHandler> next = u.getElement(pos);
		moveToBefore(e, next);
	}

	public void moveToSelectedLayerNext(LayerHandler l){
		if(l==selectedLayer)return;
		moveToNext(l.getElement(), selectedLayer.getElement());
	}

	public void moveToSelectedLayerBefore(LayerHandler l){
		if(l==selectedLayer)return;
		moveToBefore(l.getElement(), selectedLayer.getElement());
	}


	@Override
	public String toString() {
		Unit<LayerHandler> l = getTopLevelUnit();
		int s = l.size();
		StringBuilder str = new StringBuilder();
		for(int i=0;i<s;i++){
			Element<LayerHandler> ll = l.getElement(i);
			str.append(ll.getProperty().getID()).append("\n");
			if(ll.isUnit()){
				str.append(_toString("-", (Unit<LayerHandler>)ll));
			}
		}
		return str.toString();
	}

	private String _toString(String st,Unit<LayerHandler> l){
		StringBuilder str = new StringBuilder();
		int s = l.size();
		for(int i=0;i<s;i++){
			Element<LayerHandler> ll = l.getElement(i);
			str.append(st).append(ll.getProperty().getID()).append("\n");
			if(ll.isUnit()){
				str.append(_toString(st+"-", (Unit<LayerHandler>)ll)).append("\n");
			}
		}
		return str.toString();
	}


}
