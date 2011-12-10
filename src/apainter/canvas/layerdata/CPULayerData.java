package apainter.canvas.layerdata;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import apainter.GlobalValue;
import apainter.canvas.Canvas;
import apainter.canvas.cedt.cpu.CPUParallelWorkThread;
import apainter.data.PixelDataInt;
import apainter.hierarchy.Element;
import apainter.hierarchy.Unit;
import apainter.misc.Util;

public class CPULayerData extends LayerData{

	private PixelDataInt renderingbuffer;
	private BufferedImage renderingimage;
	private int core = Runtime.getRuntime().availableProcessors();

	public CPULayerData(Canvas canvas,GlobalValue globalvalue) {
		super(canvas,globalvalue);
		init();
	}

	@Override
	protected void _dispose() {
		renderingimage.flush();
		renderingbuffer=null;
	}

	private void init(){
		int w = getWidth(),h=getHeight();
		renderingbuffer = PixelDataInt.create(w, h);
		renderingimage = renderingbuffer.getDirectImage();
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
		PixelDataInt renderingbuffer;
		int fill;
		_Rendering(Rectangle clip,int filldata) {
			this.clip = clip;
			fill = filldata;
			this.renderingbuffer = CPULayerData.this.renderingbuffer;
		}

		_Rendering(Rectangle clip,int filldata,PixelDataInt buffer) {
			this.clip = clip;
			fill = filldata;
			this.renderingbuffer = buffer;
		}

		public void run(){
			if(renderingbuffer==null)return;
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
		PixelDataInt ibuf = new PixelDataInt(clip.width, clip.height, data);
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
