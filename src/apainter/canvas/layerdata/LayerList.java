package apainter.canvas.layerdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import apainter.bind.Bind;
import apainter.bind.BindObject;
import apainter.hierarchy.Element;
import apainter.hierarchy.Hierarchy;
import apainter.hierarchy.Unit;


public class LayerList extends Hierarchy<InnerLayerHandler>{

	private Map<Integer, InnerLayerHandler> alllayer = new HashMap<Integer, InnerLayerHandler>();
	private InnerLayerHandler selectedLayer;
	private boolean selectedMask=false;
	private final BindObject selectMaskBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			selectedMask = (Boolean)value;
		}

		@Override
		public Object get() {
			return selectedMask;
		}

		public boolean isSettable(Object obj) {
			if( obj instanceof Boolean){
				boolean b = (Boolean)obj;
				return b!=selectedMask && (( b && selectedLayer.isEnableMask()) || !b);
			}else return false;
		}
	};
	private final Bind selectMaskBind = new Bind(selectMaskBindObject);
	public void addselectMaskBindObject(BindObject b) {selectMaskBind.add(b);}
	public void removeselectMaskBindObject(BindObject b) {selectMaskBind.remove(b);}

	private final BindObject selectedLayerBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			selectedLayer = (InnerLayerHandler)value;
			if(selectedLayer.getElement().isUnit()){
				setCurrentUnit((Unit<InnerLayerHandler>)selectedLayer.getElement());
			}else{
				setCurrentUnit(selectedLayer.getElement().getUnit());
			}
		}

		@Override
		public Object get() {
			return selectedLayer;
		}

		public boolean isSettable(Object obj) {
			return obj instanceof InnerLayerHandler;
		}
	};
	private final Bind selectedLayerBind = new Bind(selectedLayerBindObject);
	public void addselectedLayerBindObject(BindObject b) {selectedLayerBind.add(b);}
	public void removeselectedLayerBindObject(BindObject b) {selectedLayerBind.remove(b);}


	public void setSelectLayer(InnerLayerHandler l){
		selectedLayerBindObject.set(l);
	}

	public ArrayList<InnerLayerHandler> getAllLayerHandler(){
		return new ArrayList<InnerLayerHandler>(alllayer.values());
	}

	public InnerLayerHandler getLayer(int id){
		return alllayer.get(id);
	}

	public void setSelectLayer(int id){
		setSelectLayer(alllayer.get(id));
	}

	public InnerLayerHandler getSelectLayerHandler(){
		return selectedLayer;
	}

	public boolean isSelectedMask(){
		return selectedMask;
	}

	@Override
	protected void add(Element<InnerLayerHandler> e) {
		InnerLayerHandler l = e.getProperty();
		alllayer.put(l.getID(), l);
		l.setElement(e);
		super.add(e);
	}

	public synchronized boolean canRemove(InnerLayerHandler l){
		Unit<InnerLayerHandler> unit = getTopLevelUnit();
		Element<InnerLayerHandler> ll = l.getElement();
		int size = unit.size();
		for(int i=0;i<size;i++){
			Element<InnerLayerHandler> lh = unit.getElement(i);
			if(ll==lh)continue;
			if(lh.isUnit()){
				if(_canRemove((Unit<InnerLayerHandler>)lh, ll))
					return true;
			}
			else return true;
		}
		return false;
	}

	private boolean _canRemove(Unit<InnerLayerHandler> unit,Element<InnerLayerHandler> l){
		int size = unit.size();
		for(int i=0;i<size;i++){
			Element<InnerLayerHandler> lh = unit.getElement(i);
			if(l==lh)continue;
			if(lh.isUnit()){
				if(_canRemove((Unit<InnerLayerHandler>)lh, l))return true;
			}
			else return true;
		}
		return false;
	}



	@Override
	protected void readdAll(Collection<Element<InnerLayerHandler>> e) {
		for(Element<InnerLayerHandler> el:e){
			InnerLayerHandler l = el.getProperty();
			alllayer.put(l.getID(), l);
		}
		super.readdAll(e);
	}

	@Override
	protected void remove(Element<InnerLayerHandler> e) {
		InnerLayerHandler l = e.getProperty();
		alllayer.remove(l.getID());
		super.remove(e);
	}

	@Override
	protected void removeAll(Collection<Element<InnerLayerHandler>> e) {
		for(Element<InnerLayerHandler> el:e){
			InnerLayerHandler l = el.getProperty();
			alllayer.remove(l.getID());
		}
		super.removeAll(e);
	}


	public void setSelectedMask(boolean b){
		selectedLayerBindObject.set(b);
	}



	public void moveToNext(InnerLayerHandler l){
		Element<InnerLayerHandler> e = l.getElement();
		Unit<InnerLayerHandler> u = e.getUnit();
		int pos = e.getIndex()+1;
		int size = u.size();
		if(pos==size)return;
		Element<InnerLayerHandler> before = u.getElement(pos);
		moveToNext(e, before);
	}

	public void moveToBefore(InnerLayerHandler l){
		Element<InnerLayerHandler> e = l.getElement();
		Unit<InnerLayerHandler> u = e.getUnit();
		int pos = e.getIndex()-1;
		if(pos<0)return;
		Element<InnerLayerHandler> next = u.getElement(pos);
		moveToBefore(e, next);
	}

	public void moveToSelectedLayerNext(InnerLayerHandler l){
		if(l==selectedLayer)return;
		moveToNext(l.getElement(), selectedLayer.getElement());
	}

	public void moveToSelectedLayerBefore(InnerLayerHandler l){
		if(l==selectedLayer)return;
		moveToBefore(l.getElement(), selectedLayer.getElement());
	}


	@Override
	public String toString() {
		Unit<InnerLayerHandler> l = getTopLevelUnit();
		int s = l.size();
		StringBuilder str = new StringBuilder();
		for(int i=0;i<s;i++){
			Element<InnerLayerHandler> ll = l.getElement(i);
			str.append(ll.getProperty().getID()).append("\n");
			if(ll.isUnit()){
				str.append(_toString("-", (Unit<InnerLayerHandler>)ll));
			}
		}
		return str.toString();
	}

	private String _toString(String st,Unit<InnerLayerHandler> l){
		StringBuilder str = new StringBuilder();
		int s = l.size();
		for(int i=0;i<s;i++){
			Element<InnerLayerHandler> ll = l.getElement(i);
			str.append(st).append(ll.getProperty().getID()).append("\n");
			if(ll.isUnit()){
				str.append(_toString(st+"-", (Unit<InnerLayerHandler>)ll)).append("\n");
			}
		}
		return str.toString();
	}


}
