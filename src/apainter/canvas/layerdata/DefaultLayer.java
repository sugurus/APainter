package apainter.canvas.layerdata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import apainter.bind.annotation.BindProperty;
import apainter.rendering.ColorMode;

import static apainter.canvas.CanvasConstant.*;
abstract class DefaultLayer implements Layer,PixelDrawable,MaskContainer{

	private final int id;
	private String name;
	protected int transparent;
	protected boolean visible;
	protected ColorMode mode;

	public DefaultLayer(int id,String name) {
		this.id = id;
		this.name = name;
		transparent = 256;
		visible = true;
		mode = ColorMode.Default;
	}

	@Override
	final public int getID() {
		return id;
	}

	@Override
	final public String getName() {
		return name;
	}

	@BindProperty(NameProperty)
	@Override
	final public void setName(String name) {
		String old = name;
		if(name==null)name="";
		this.name = name;
		if(!old.equals(this.name)){
			firePropertyChange(NameProperty, old, this.name);
		}
	}

	@BindProperty(TransparentProperty)
	@Override
	final public void setTransparent(int transparent) {
		int old = transparent;
		if(transparent < 0 )transparent = 0;
		else if(transparent > 256)transparent= 256;
		this.transparent = transparent;
		if(old!=this.transparent){
			firePropertyChange(TransparentProperty, old, this.transparent);
		}
	}

	@Override
	final public int getTransparent() {
		return transparent;
	}

	@Override
	public boolean isPixelContainer() {
		return true;
	}

	@Override
	public boolean isPixelDrawable() {
		return true;
	}

	@Override
	final public boolean isVisible() {
		return visible;
	}

	@BindProperty(VisibleProperty)
	@Override
	public void setVisible(boolean b) {
		if(b!=visible){
			visible = b;
			firePropertyChange(VisibleProperty, !b, b);
		}
	}

	@Override
	final public ColorMode getRenderingMode() {
		return mode;
	}

	@BindProperty(RenderModeProperty)
	@Override
	final public void setRenderingMode(ColorMode mode) {
		if(this.mode !=mode){
			ColorMode old = this.mode;
			this.mode = mode;
			firePropertyChange(RenderModeProperty, old, this.mode);
		}
	}

	@Override
	public ColorMode[] getUsableModes() {
		return new ColorMode[]{
				ColorMode.Default

		};
	}

	//propertychangelistener---------------------------------

	private ArrayList<PropertyChangeListener> propertylistener = new ArrayList<PropertyChangeListener>();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (!propertylistener.contains(l))
			propertylistener.add(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertylistener.remove(l);
	}

	protected void firePropertyChange(String name, Object oldValue, Object newValue) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue,
				newValue);
		for (PropertyChangeListener l : propertylistener) {
			l.propertyChange(e);
		}
	}
}