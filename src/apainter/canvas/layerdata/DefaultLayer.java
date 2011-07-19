package apainter.canvas.layerdata;

import static apainter.canvas.CanvasConstant.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import apainter.bind.annotation.BindProperty;
import apainter.canvas.Canvas;
import apainter.drawer.DrawAccepter;
import apainter.rendering.ColorMode;
abstract class DefaultLayer implements Layer,PixelSetable,MaskContainer{

	private final int id;
	private String name;
	protected int transparent;
	protected boolean visible;
	protected ColorMode mode;
	protected final Canvas canvas;

	public DefaultLayer(int id,String name,Canvas canvas) {
		this.id = id;
		this.name = name;
		transparent = 256;
		visible = true;
		mode = ColorMode.Default;
		this.canvas = canvas;
	}

	@Override
	final public int getID() {
		return id;
	}

	@Override
	public Canvas getCanvas() {
		return canvas;
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
	public boolean isPixelSetable() {
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

	@Override
	public boolean isLayer() {
		return true;
	}
	@Override
	public boolean isGroup() {
		return false;
	}

	//propertychangelistener---------------------------------


	private EventListenerList listener = new EventListenerList();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listener.remove(PropertyChangeListener.class, l);
		listener.add(PropertyChangeListener.class, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listener.remove(PropertyChangeListener.class, l);
	}

	public void firePropertyChange(String name, Object oldValue, Object newValue) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue,
				newValue);

		Object[] listeners = this.listener.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}
}
