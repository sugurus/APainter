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
		if(name==null){
			name = "";
		}
		this.name = name;
	}

	@Override
	final public void setTransparent(int transparent) {
		if(transparent < 0) transparent = 0;
		else if(transparent > 256)transparent = 256;
		this.transparent = transparent;
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
		visible = b;
	}

	@Override
	final public ColorMode getRenderingMode() {
		return mode;
	}

	@Override
	final public void setRenderingMode(ColorMode mode) {
		//TODO モードを受け入れるか否か
		this.mode =mode;
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

}
