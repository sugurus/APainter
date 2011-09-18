package apainter.canvas.layerdata;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

import apainter.Color;
import apainter.canvas.Canvas;
import apainter.canvas.event.EventConstant;
import apainter.canvas.event.PaintEvent;
import apainter.canvas.event.PaintLastEvent;
import apainter.canvas.event.PaintStartEvent;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.drawer.event.DrawEvent;
import apainter.drawer.event.DrawLastEvent;
import apainter.drawer.event.DrawStartEvent;
import apainter.drawer.event.DrawerEvent;
import apainter.rendering.ColorMode;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;
import apainter.rendering.RenderingUtilities;

class CPULayer extends DefaultLayer{

	private PixelDataIntBuffer buffer;
	private int[] pixel;
	private CPUMask mask;
	private CPULayerHandler handler;

	private Object paintsource;
	private Rectangle paintrect;
	private PaintLayerHistory painthistory;

	public CPULayer(int id, String name,int width,int height,Canvas canvas,CPULayerData layerData) {
		super(id, name,canvas,layerData);
		buffer = PixelDataIntBuffer.create(width, height);
		pixel = buffer.getData();
		mask = new CPUMask(width, height,canvas);
		handler = new CPULayerHandler(this,canvas);
	}

	int[] getPixelData(){
		return pixel;
	}

	@Override
	public PixelDataBuffer getDataBuffer() {
		return buffer;
	}

	public CPUMask getMask(){
		return mask;
	}


	@Override
	public void dispose() {
		buffer.dispose();
		mask.dispose();
		handler.dispose();
		buffer=null;
		pixel = null;
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

		mask.clear();
		mask.setEnable(true);

	}

	@Override
	public void render(PixelDataBuffer destination, Rectangle r) {
		Point p = new Point();
		r =RenderingUtilities.getEnableClipBounds(destination, buffer, p, r);
		Renderer render = mode.getCPURenderer();
		RenderingOption option = getRenderingOption();
		render.rendering(destination, buffer, p, r, option);
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
	public void setPixel(int color, int x, int y) {
		if(color>>>24==0)color = 0;
		buffer.setData(color, x, y);
	}

	@Override
	public void setPixels(int[] colors, int x, int y, int width, int height) {
		Rectangle r = buffer.intersection(new Rectangle(x,y,width,height));
		if(r.isEmpty())return;
		int ex = r.width+r.x;
		int ey = r.height+r.y;
		int add= width-r.width;
		int i= (r.y-y)*width-add+r.x-x;
		for(int X,Y=r.y;Y<ey;Y++){
			i+=add;
			for(X = r.x;X<ex ;X++,i++){
				int c = colors[i];
				if((c>>>24)==0){
					c = 0;
				}
				buffer.setData(c, X,Y);
			}
		}
	}

	@Override
	public int getPixel(int x, int y) {
		return buffer.contains(x,y)?pixel[x+y*buffer.width]:Color.NotColor;
	}

	@Override
	public int[] getPixels(int x, int y, int width, int height) {
		return copyPixels(x, y, width, height, new int[width*height]);
	}

	@Override
	public int[] copyPixels(int x, int y, int width, int height,
			int[] distenation) {
		for(int xx,yy=0;yy<height;yy++){
			for(xx=0;xx<width;xx++){
				if(!buffer.contains(xx+x,yy+y)){
					distenation[xx+width*yy]=Color.NotColor;
					continue;
				}
				int c = buffer.getData(xx, yy);
				if((c>>>24)==0 && c!=0){
					buffer.setData(0, xx, yy);
					c =0;
				}
				distenation[xx+width*yy] = c;
			}
		}
		return distenation;
	}

	@Override
	public void clear() {
		Arrays.fill(pixel,0);
	}

	@Override
	public void clear(Rectangle r) {
		buffer.setData(0, r);
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

		private final CPULayer h;
		private final Canvas canvas;

		CPULayerHandler(CPULayer c,Canvas ca) {
			canvas = ca;
			h = c;
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
		PixelDataBuffer getOriginalData() {
			return h.getDataBuffer();
		}


		@Override
		PixelDataBuffer getMaskOriginalData() {
			return h.mask.getPixelDataBuffer();
		}

		@Override
		PixelSetable getDrawable() {
			return h;
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
		public boolean isPixelSetable() {
			return h.isPixelSetable();
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
		public void setPixel(int color, int x, int y) {
			h.setPixel(color, x, y);
		}

		@Override
		public void setPixels(int[] colors, int x, int y,
				int width, int height) {
			h.setPixels(colors, x, y, width, height);
		}

		@Override
		public void clear() {
			h.clear();
		}

		@Override
		public void clear(Rectangle r) {
			h.clear(r);
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
