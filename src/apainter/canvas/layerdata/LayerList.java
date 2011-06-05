package apainter.canvas.layerdata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import apainter.bind.annotation.BindProperty;
import apainter.hierarchy.Element;
import apainter.hierarchy.Hierarchy;


public class LayerList extends Hierarchy<LayerHandler>{
	public static final String
		SelectedLayerChangeProperty = "selectedLayer",
		SelectedMaskChangeProperty = "selectedMask";

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
			firePropertyChange(SelectedLayerChangeProperty, old, l);
		}
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
		super.add(e);
	}

	@Override
	protected void addAll(Collection<Element<LayerHandler>> e) {
		for(Element<LayerHandler> el:e){
			LayerHandler l = el.getProperty();
			alllayer.put(l.getID(), l);
		}
		super.addAll(e);
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



}
