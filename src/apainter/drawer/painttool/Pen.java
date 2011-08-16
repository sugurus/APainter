package apainter.drawer.painttool;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalValue;
import apainter.bind.annotation.BindProperty;
import apainter.rendering.ColorMode;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

import static apainter.PropertyChangeNames.*;
public class Pen extends BasicDrawer{

	Renderer cpu8bit = new PenCPUDefaultRendering();
	private ColorMode mode = ColorMode.Default;
	private static Renderer[] cpuren = new Renderer[]{
			new PenCPUDefaultRendering(),
			new PenCPUAddRendering(),
			new PenCPUSubtractiveRendering(),
			new PenCPUMultiplicationRendering(),
			new PenCPUScreenRendering(),
			new PenCPUOverlayRendering(),
			new PenCPUSoftlightRendering(),
			new PenCPUHardlightRendering(),
			new PenCPUDodgeRendering(),
			new PenCPUBurnRendering(),
			new PenCPUDarkenRendering(),
			new PenCPULightRendering(),
			new PenCPUDifferenceRendering(),
			new PenCPUExclusionRendering()
	};


	public Pen(GlobalValue global) {
		super(global);
//		Device d = getDevice();
//		if(d==Device.GPU){
//			//GPU
//		}
	}

	private static Renderer getCPURenderer(ColorMode mode){
		Renderer r = null;
		switch(mode){
		case Default:
			r=cpuren[0];
			break;
		case Add:
			r=cpuren[1];
			break;
		case Subtractive:
			r=cpuren[2];
			break;
		case Multiplication:
			r=cpuren[3];
			break;
		case Screen:
			r=cpuren[4];
			break;
		case Overlay:
			r=cpuren[5];
			break;
		case Softlight:
			r=cpuren[6];
			break;
		case Hardlight:
			r=cpuren[7];
			break;
		case Dodge:
			r=cpuren[8];
			break;
		case Burn:
			r=cpuren[9];
			break;
		case Darken:
			r=cpuren[10];
			break;
		case Light:
			r=cpuren[11];
			break;
		case Difference:
			r=cpuren[12];
			break;
		case Exclusion:
			r=cpuren[13];
			break;

		}
		return r;
	}

	@Override
	protected Renderer getRenderer(Device d) {
		if(d == Device.CPU){
			return getCPURenderer(mode);
		}
		return null;
	}

	@Override
	protected Device[] getUsableDevices() {
		//TODO GPU対応したいね。
		return CPUOnly();
	}

	@Override
	protected void setOption(RenderingOption option, PenTabletMouseEvent e) {

	}

	public ColorMode getColorMode() {
		return mode;
	}

	@BindProperty(PenModePropertyChange)
	public void setColorMode(ColorMode m){
		if(m==null|| m==ColorMode.AlphaDawn || m==ColorMode.AlphaPlus || m==ColorMode.Del || m==mode)return;
		ColorMode old = mode;
		mode = m;
		firePropertyChange(PenModePropertyChange, old, mode);
	}

	public void setColorMode(String colormode){
		ColorMode m = ColorMode.getColorMode(colormode);
		setColorMode(m);
	}

	// listener--------------------------------------

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
