package apainter.canvas.layerdata;

import java.awt.Rectangle;
import java.util.Arrays;

import apainter.Color;
import apainter.canvas.Canvas;
import apainter.canvas.event.EventConstant;
import apainter.canvas.event.PaintEvent;
import apainter.canvas.event.PaintEventAccepter;
import apainter.canvas.event.PaintLastEvent;
import apainter.canvas.event.PaintStartEvent;
import apainter.data.PixelDataByteBuffer;
import apainter.drawer.event.DrawEvent;
import apainter.drawer.event.DrawLastEvent;
import apainter.drawer.event.DrawStartEvent;
import apainter.drawer.event.DrawerEvent;
import apainter.rendering.Renderer;

class CPUMask extends Mask {


	private PixelDataByteBuffer buffer;
	private byte[] pixel;
	private CPUMauseHandler handler = new CPUMauseHandler();

	public CPUMask(int width,int height,Canvas canvas) {
		super(canvas);
		buffer = PixelDataByteBuffer.create(width, height);
		pixel = buffer.getData();
	}

	@Override
	public MaskHandler getHandler() {
		return handler;
	}

	byte[] getPixel(){
		return pixel;
	}

	PixelDataByteBuffer getPixelDataBuffer(){
		return buffer;
	}

	@Override
	public void setPixel(int color, int x, int y) {
		int ycrcb = Color.RGB2YCrCb(color);
		int Y = ycrcb>>16&0xff;
		buffer.setData((byte)Y, x, y);
	}

	@Override
	public void setPixels(int[] colors,final int x,final int y,final int width,final int height) {
		Rectangle r = buffer.intersection(new Rectangle(x,y,width,height));
		if(r.isEmpty())return;
		int ex = r.width+r.x;
		int ey = r.height+r.y;
		int add= width-r.width;
		int i= (r.y-y)*width-add+r.x-x;
		for(int X,Y=r.y;Y<ey;Y++){
			i+=add;
			for(X = r.x;X<ex ;X++,i++){
				int yrb = Color.RGB2YCrCb(colors[i]);
				byte b = (byte) ((yrb>>>24)*(yrb>>16&0xff)/255);
				buffer.setData(b, X,Y);
			}
		}
	}

	@Override
	public int getPixel(int x, int y) {
		return buffer.contains(x, y)?Color.NotColor:pixel[x+y*buffer.width];
	}

	@Override
	public int[] getPixels(int x, int y, int width, int height) {
		return copyPixels(x, y, width, height, null);
	}

	@Override
	public int[] copyPixels(int x, int y, int width, int height,
			int[] distination) {
		return buffer.copy(distination, new Rectangle(x,y,width,height));
	}

	@Override
	PixelDataByteBuffer getDataBuffer() {
		return buffer;
	}

	@Override
	public void clear() {
		Arrays.fill(pixel, (byte)0);
	}
	@Override
	public void clear(Rectangle r) {
		buffer.setData((byte)0, r);
	}

	public void dispose(){
		buffer.dispose();
		pixel = null;
	}


	@Override
	public boolean isPaintable() {
		//TODO どうしよ？
		return true;
	}




	private Object paintsource;
	private Rectangle paintrect;
	@Override
	public void startPaint(Object obj) {
		if(paintsource==null){
			paintsource = obj;
		}
		//TODO init hisotry
	}
	@Override
	public boolean paint(PaintEvent e) {
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
		return false;
	}

	@Override
	public void endPaint(Object obj) {
		paintsource = null;
		//TODO hisotry
	}



	public class CPUMauseHandler implements MaskHandler{


		CPUMask m = CPUMask.this;

		@Override
		public Mask getMask() {
			return m;
		}

		@Override
		public Canvas getCanvas() {
			return m.getCanvas();
		}

		@Override
		public String getHandlerName() {
			return "maskhandler cpu";
		}

		@Override
		public String getName() {
			return "MASK";
		}
		@Override
		public String getDrawTargetName() {
			return "cpumask byte gray 8";
		}

		@Override
		public void acceptEvent(DrawerEvent e) {
			if(e instanceof DrawEvent){
				canvas.dispatchEvent(PaintEvent.convert((DrawEvent)e, this,
						EventConstant.ID_PAINT_LAYERMASK));
			}else if (e instanceof DrawStartEvent){
				canvas.dispatchEvent(new PaintStartEvent( e.getDrawer(), this));
			}else if(e instanceof DrawLastEvent){
				canvas.dispatchEvent(new PaintLastEvent(e.getDrawer(), this));
			}
		}

		@Override
		public boolean paint(PaintEvent e) {
			return m.paint(e);
		}

		@Override
		public void startPaint(Object obj) {
			m.startPaint(obj);
		}

		@Override
		public void endPaint(Object obj) {
			m.endPaint(obj);
		}

		@Override
		public boolean isPaintable() {
			return m.isPaintable();
		}

	}



}
