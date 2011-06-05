package apainter.canvas.layerdata;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

import apainter.Device;
import apainter.color.Color;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.drawer.DrawAccepter;
import apainter.drawer.DrawEvent;
import apainter.rendering.ColorMode;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;
import apainter.rendering.RenderingUtilities;

class CPULayer extends DefaultLayer{

	private PixelDataIntBuffer buffer;
	private int[] pixel;
	private CPUMask mask;
	private CPULayerHandler handler = new CPULayerHandler(this);
	private MemoryImageSource imagesource;
	private Image img;

	public CPULayer(int id, String name,int width,int height) {
		super(id, name);
		buffer = PixelDataIntBuffer.create(width, height);
		pixel = buffer.getData();
		mask = new CPUMask(width, height);

		imagesource = new MemoryImageSource(width, height,ColorModel.getRGBdefault(), pixel, 0, width);
		imagesource.setAnimated(true);
		img =Toolkit.getDefaultToolkit().createImage(imagesource);
	}

	int[] getPixelData(){
		return pixel;
	}

	PixelDataIntBuffer getPixelDataBuffer(){
		return buffer;
	}

	CPUMask getMask(){
		return mask;
	}

	Image getImage(){
		return img;
	}

	@Override
	public void dispose() {
		buffer=null;
		pixel = null;
		mask = null;
	}

	@Override
	public LayerHandler getHandler() {
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
		imagesource.newPixels(r.x, r.y, r.width, r.height);
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
	public boolean paint(DrawEvent e) {
		if(!e.canUseDevice(Device.CPU))return false;
		Renderer r = e.getRenderer();
		Rectangle rect =RenderingUtilities.getEnableClipBounds(buffer,
				e.getMapData(), e.getBounds().getLocation(), null);
		r.rendering(buffer, e.getMapData(), e.getLocation(), rect, e.getOption());
		return true;
	}


	private static class CPULayerHandler extends LayerHandler{

		private final CPULayer h;

		CPULayerHandler(CPULayer c) {
			h = c;
		}

		@Override
		Image getImage() {
			return h.getImage();
		}

		@Override
		Layer getLayer() {
			return h;
		}

		@Override
		PixelDataBuffer getOriginalData() {
			return h.getPixelDataBuffer();
		}

		@Override
		Image getMaskImage() {
			return h.mask.getImage();
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
		public void setPixels(int[] colors, int x, int y, int width, int height) {
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

	}

}
