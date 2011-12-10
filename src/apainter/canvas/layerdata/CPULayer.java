package apainter.canvas.layerdata;


import java.awt.Point;
import java.awt.Rectangle;
import apainter.ColorType;
import apainter.canvas.Canvas;
import apainter.canvas.event.EventConstant;
import apainter.canvas.event.PaintEvent;
import apainter.canvas.event.PaintLastEvent;
import apainter.canvas.event.PaintStartEvent;
import apainter.data.OutBoundsException;
import apainter.data.PixelData15BitColor;
import apainter.data.PixelData;
import apainter.data.PixelDataContainer;
import apainter.data.PixelDataInt;
import apainter.drawer.event.DrawEvent;
import apainter.drawer.event.DrawLastEvent;
import apainter.drawer.event.DrawStartEvent;
import apainter.drawer.event.DrawerEvent;
import apainter.rendering.ColorMode;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;
import apainter.rendering.RenderingUtilities;

class CPULayer extends DefaultLayer{

	private PixelDataInt integerbuffer;
	private PixelDataContainer integercontainer = new PixelDataContainer() {

		@Override
		public int getWidth() {
			return integerbuffer.width;
		}

		@Override
		public PixelData getPixelData() {
			return integerbuffer;
		}

		@Override
		public int getHeight() {
			return integerbuffer.height;
		}
		@Override
		public void restore() {
			// TODO 特にすること無いんじゃね
		}
	};
	private PixelData15BitColor buffer;
	private CPUMask mask;
	private CPULayerHandler handler;

	private Object paintsource;
	private Rectangle paintrect;
	private PaintLayerHistory painthistory;

	public CPULayer(int id, String name,int width,int height,Canvas canvas,CPULayerData layerData) {
		super(id, name,canvas,layerData);
		buffer = new PixelData15BitColor(width, height);
		integerbuffer = buffer.getIntegerBuffer();
		mask = new CPUMask(width, height,canvas);
		handler = new CPULayerHandler(this,canvas);
	}

	@Override
	public PixelData getDataBuffer() {
		return buffer;
	}

	@Override
	public MaskHandler getMask(){
		return mask.getHandler();
	}
	@Override
	public boolean isMaskContainer() {
		return true;
	}

	@Override
	public MaskContainer getMaskContainer() {
		return this;
	}

	@Override
	public void dispose() {
		integerbuffer.dispose();
		mask.dispose();
		handler.dispose();
		integerbuffer=null;
		mask = null;

	}

	@Override
	public InnerLayerHandler getHandler() {
		return handler;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public boolean isEnableMask() {
		return mask.isEnable();
	}

	@Override
	public void setEnableMask(boolean b) {
		mask.setEnable(b);
	}

	@Override
	public void createMask() {
		if(isEnableMask())throw new RuntimeException("mask enable");

		mask.init();
		mask.setEnable(true);

	}

	@Override
	public void render(PixelData destination, Rectangle r) {
		Point p = new Point();
		r =RenderingUtilities.getEnableClipBounds(destination, integerbuffer, p, r);
		Renderer render = mode.getCPURenderer();
		RenderingOption option = getRenderingOption();
		render.rendering(destination, integercontainer, p, r, option);
	}

	@Override
	public RenderingOption getRenderingOption(){
		RenderingOption option;
		if(mask.isEnable()){
			option = new RenderingOption(mask.getDataBuffer(), transparent);
		}else{
			option = new RenderingOption(transparent);
		}
		return option;
	}

	@Override
	public int getPixel(int x, int y) throws OutBoundsException{
		return integerbuffer.getData(x, y);
	}

	@Override
	public int[] getPixels(int x, int y, int width, int height)throws OutBoundsException{
		return copyPixels(x, y, width, height, new int[width*height]);
	}

	@Override
	public int[] copyPixels(int x, int y, int width, int height,
			int[] distenation) throws OutBoundsException,ArrayIndexOutOfBoundsException{
		for(int xx,yy=0;yy<height;yy++){
			for(xx=0;xx<width;xx++){
				distenation[xx+width*yy] =
						integerbuffer.getData(xx, yy);
			}
		}
		return distenation;
	}

	@Override
	public ColorType getColorType() {
		return ColorType.ARGB;
	}


	@Override
	public boolean paint(PaintEvent e) {
		//TODO CPUかどうかの判定って必要かな？まぁ、実際にGPU作り始めてからでいっか。
		Object source = e.getSource();
		if(source!=paintsource)return false;
		Renderer r = e.getRenderer();
		Rectangle rect;
		r.rendering(buffer, e.getMapData(), e.getLocation(),
				rect= e.getBounds(), e.getOption());
		if(paintrect==null){
			paintrect=rect;
		}else{
			paintrect = paintrect.union(rect);
		}
		return true;
	}

	public void endPaint(Object o){
		painthistory.finishPaint(paintrect);
		canvas.addHistory(painthistory);
		paintsource=null;
		paintrect=null;
		painthistory = null;
	}


	@Override
	public void startPaint(Object source) {
		paintsource=source;
		painthistory = new PaintLayerHistory(canvas, handler);
	}
	@Override
	public boolean isPaintable() {
		return true;
	}


	private static class CPULayerHandler extends InnerLayerHandler{

		private CPULayer h;
		private Canvas canvas;

		CPULayerHandler(CPULayer c,Canvas ca) {
			canvas = ca;
			h = c;
		}
		private void clear(){
			h =null; canvas = null;
		}

		@Override
		public MaskContainer getMaskContainer() {
			return this;
		}

		@Override
		public boolean isMaskContainer() {
			return true;
		}

		@Override
		public MaskHandler getMask() {
			return h.getMask();
		}
		@Override
		public String getHandlerName() {
			return "layerhandler cpu";
		}

		@Override
		public void startPaint(Object source) {
			h.startPaint(source);
		}

		@Override
		public void endPaint(Object source) {
			h.endPaint(source);
		}

		@Override
		public ColorType getColorType() {
			return h.getColorType();
		}


		@Override
		public String getLayerTypeName() {
			return "Layer";
		}

		@Override
		public String getDrawTargetName() {
			return "cpulayer int argb 8";
		}

		public Canvas getCanvas() {
			return canvas;
		}


		@Override
		Layer getLayer() {
			return h;
		}


		@Override
		PixelData getOriginalData() {
			return h.getDataBuffer();
		}


		@Override
		PixelData getMaskOriginalData() {
			return h.mask.getPixelDataBuffer();
		}

		@Override
		public int getID() {
			return h.getID();
		}

		@Override
		public String getName() {
			return h.getName();
		}

		@Override
		public void setName(String name) {
			h.setName(name);
		}

		@Override
		public void setTransparent(int transparent) {
			h.setTransparent(transparent);
		}

		@Override
		public int getTransparent() {
			return h.getTransparent();
		}

		@Override
		public boolean isVisible() {
			return h.isVisible();
		}

		@Override
		public void setVisible(boolean b) {
			h.setVisible(b);
		}

		@Override
		public boolean isDrawable() {
			return h.isDrawable();
		}

		@Override
		public boolean isEnableMask() {
			return h.isEnableMask();
		}

		@Override
		public void setEnableMask(boolean b) {
			h.setEnableMask(b);
		}

		@Override
		public void createMask() {
			h.createMask();
		}

		@Override
		public boolean isPixelContainer() {
			return h.isPixelContainer();
		}


		@Override
		public ColorMode getRenderingMode() {
			return h.getRenderingMode();
		}

		@Override
		public void setRenderingMode(ColorMode mode) {
			h.setRenderingMode(mode);
		}

		@Override
		public ColorMode[] getUsableModes() {
			return h.getUsableModes();
		}


		@Override
		public int getPixel(int x, int y) {
			return h.getPixel(x, y);
		}

		@Override
		public int[] getPixels(int x, int y, int width, int height) {
			return h.getPixels(x, y, width, height);
		}

		@Override
		public int[] copyPixels(int x, int y, int width, int height,
				int[] distenation) {
			return h.copyPixels(x, y, width, height, distenation);
		}

		@Override
		public boolean isGroup() {
			return h.isGroup();
		}
		@Override
		public boolean isLayer() {
			return h.isLayer();
		}


		@Override
		public void acceptEvent(DrawerEvent e) {
			if(e instanceof DrawEvent){
				canvas.dispatchEvent(PaintEvent.convert((DrawEvent)e, this,
						EventConstant.ID_PAINT_LAYER));
			}else if (e instanceof DrawStartEvent){
				canvas.dispatchEvent(new PaintStartEvent( e.getDrawer(), this));
			}else if(e instanceof DrawLastEvent){
				canvas.dispatchEvent(new PaintLastEvent(e.getDrawer(), this));
			}
		}

		@Override
		public boolean isPaintable() {
			return h.isPaintable();
		}

		@Override
		public boolean paint(PaintEvent e) {
			return h.paint(e);
		}
	}//レイヤーハンドラー

}
