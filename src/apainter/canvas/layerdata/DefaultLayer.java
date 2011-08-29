package apainter.canvas.layerdata;

import static apainter.misc.Util.*;
import apainter.bind.Bind;
import apainter.bind.BindObject;
import apainter.canvas.Canvas;
import apainter.rendering.ColorMode;
abstract class DefaultLayer implements Layer,PixelSetable,MaskContainer{


	public DefaultLayer(int id,String name,Canvas canvas,LayerData layerdata) {
		this.id = id;
		this.name = name;
		transparent = 256;
		visible = true;
		mode = ColorMode.Default;
		this.canvas = nullCheack(canvas, "canvas is null");
		this.layerdata = nullCheack(layerdata, "layerdata is null");
	}

	@Override
	final public int getID() {
		return id;
	}

	public Canvas getCanvas() {
		return canvas;
	}



	@Override
	final public String getName() {
		return name;
	}

	@Override
	final public void setName(String name) {
		nameBindObject.set(name);
	}

	@Override
	final public void setTransparent(int transparent) {
		transparentBindObject.set(transparent);
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

	@Override
	public void setVisible(boolean b) {
		visibleBindObject.set(b);
	}

	@Override
	final public ColorMode getRenderingMode() {
		return mode;
	}

	@Override
	final public void setRenderingMode(ColorMode mode) {
		modeBindObject.set(mode);
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


	//field----------------------------------------------------------------------
	private final int id;
	private String name;
	protected int transparent;
	protected boolean visible;
	protected ColorMode mode;
	protected final Canvas canvas;
	protected final LayerData layerdata;
	//Bind----------
	private final BindObject modeBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			mode = (ColorMode)value;
		}

		@Override
		public Object get() {
			return mode;
		}

		public boolean isSettable(Object obj) {
			if(obj instanceof ColorMode){
				//TODO モードを受け入れるか否か
				return true;
			}else return false;
		}
	};
	private final Bind modeBind = new Bind(modeBindObject);
	public void addmodeBindObject(BindObject b) {modeBind.add(b);}
	public void removemodeBindObject(BindObject b) {modeBind.remove(b);}
	private final BindObject visibleBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			visible = (Boolean)value;
		}

		@Override
		public Object get() {
			return visible;
		}

		public boolean isSettable(Object obj) {
			return obj instanceof Boolean;
		}
	};
	private final Bind visibleBind = new Bind(visibleBindObject);
	public void addvisibleBindObject(BindObject b) {visibleBind.add(b);}
	public void removevisibleBindObject(BindObject b) {visibleBind.remove(b);}

	private final BindObject transparentBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			Integer i = (Integer)value;
			if(i < 0) i = 0;
			else if(i > 256)i = 256;
			transparent = i;
		}

		@Override
		public Object get() {
			return transparent;
		}

		public boolean isSettable(Object obj) {
			return obj instanceof Integer;
		}
	};
	private final Bind transparentBind  = new Bind(transparentBindObject);
	public void addTransparentBindObject(BindObject b){transparentBind.add(b);}
	public void removeTransparentBindObject(BindObject b){transparentBind.remove(b);}

	private final BindObject nameBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			String s = value!=null?(String)value:"";
			name = s;
		}

		@Override
		public Object get() {
			return name;
		}

		public boolean isSettable(Object obj) {
			return obj==null || obj instanceof String;
		}
	};
	private final Bind nameBind  = new Bind(nameBindObject);
	public void addNameBindObject(BindObject b){nameBind.add(b);}
	public void removeNameBindObject(BindObject b){nameBind.remove(b);}
	//-------------------end bind------------------------------------------------
}
