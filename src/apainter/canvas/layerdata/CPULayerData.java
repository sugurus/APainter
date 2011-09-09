package apainter.canvas.layerdata;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;

import apainter.GlobalValue;
import apainter.canvas.Canvas;
import apainter.canvas.cedt.cpu.CPUParallelWorkThread;
import apainter.data.PixelDataIntBuffer;
import apainter.hierarchy.Element;
import apainter.hierarchy.Unit;
import apainter.misc.Util;

public class CPULayerData extends LayerData{

	private PixelDataIntBuffer renderingbuffer;
	private BufferedImage renderingimage;
	private int core = Runtime.getRuntime().availableProcessors();

	//////////////////////////////FIXME debug

	public void endPaint(Object source) {};
	@Override
	public void startPaint(Object source) {
		// TODO 自動生成されたメソッド・スタブ

	}



	public CPULayerData(Canvas canvas,GlobalValue globalvalue) {
		super(canvas,globalvalue);
		init();
	}

	private void init(){
		int w = getWidth(),h=getHeight();
		renderingimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		int[] pixel =((DataBufferInt)renderingimage.getRaster().getDataBuffer()).getData();
		renderingbuffer = new PixelDataIntBuffer(w, h, pixel);
		renderingbuffer.setData(0xffffffff, rect());
		rendering();
	}

	public BufferedImage getImage(){
		return renderingimage;
	}

	@Override
	protected Layer createLayer(int id,String layername) {
		CPULayer l = new CPULayer(id, makeLayerName(layername), getWidth(), getHeight(),canvas,this);
		return l;
	}


	@Override
	public void rendering() {
		rendering(rect());
	}

	@Override
 	public void rendering(Rectangle clip) {
		if(clip==null)return;
		clip = rect().intersection(clip);
		if(clip.isEmpty())return;
		Rectangle[] rects;
		if(clip.width*clip.height<core*10){
			rects = new Rectangle[]{clip};
		}else{
			rects = Util.partition(clip, core);
		}
		ArrayList<Runnable> runs = new ArrayList<Runnable>(core);
		for(Rectangle r:rects){
			if(r.isEmpty())continue;
			runs.add(new _Rendering(r,0xffffffff));
		}

		CPUParallelWorkThread.exec(runs);
	}

	private class _Rendering implements Runnable{
		Rectangle clip;
		PixelDataIntBuffer renderingbuffer;
		int fill;
		_Rendering(Rectangle clip,int filldata) {
			this.clip = clip;
			fill = filldata;
			this.renderingbuffer = CPULayerData.this.renderingbuffer;
		}

		_Rendering(Rectangle clip,int filldata,PixelDataIntBuffer buffer) {
			this.clip = clip;
			fill = filldata;
			this.renderingbuffer = buffer;
		}

		public void run(){
			renderingbuffer.setData(fill, clip);
			Unit<InnerLayerHandler> unit =layerlist.getTopLevelUnit();

			ArrayList<Element<InnerLayerHandler>> elements = unit.getElements();


			for(Element<InnerLayerHandler> e:elements){
				Layer l = e.getProperty().getLayer();
				l.render(renderingbuffer, clip);
			}
		}
	}


	@Override
	public BufferedImage createImage() {
		Rectangle clip = rect();
		BufferedImage buf = new BufferedImage(clip.width, clip.height, BufferedImage.TYPE_INT_ARGB);
		int[] data = ((DataBufferInt)buf.getRaster().getDataBuffer()).getData();
		PixelDataIntBuffer ibuf = new PixelDataIntBuffer(clip.width, clip.height, data);
		Rectangle[] rects;
		if(clip.width*clip.height<core*10){
			rects = new Rectangle[]{clip};
		}else{
			rects = Util.partition(clip, core);
		}
		ArrayList<Runnable> runs = new ArrayList<Runnable>(core);
		for(Rectangle r:rects){
			if(r.isEmpty())continue;
			runs.add(new _Rendering(r,0,ibuf));
		}

		CPUParallelWorkThread.exec(runs);
		return buf;
	}


}
