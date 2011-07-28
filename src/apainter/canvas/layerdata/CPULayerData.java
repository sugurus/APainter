package apainter.canvas.layerdata;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;

import apainter.canvas.Canvas;
import apainter.canvas.cedt.cpu.CPUParallelWorkThread;
import apainter.data.PixelDataIntBuffer;
import apainter.gui.test.ImageFrame;
import apainter.gui.test.ImageView;
import apainter.hierarchy.Element;
import apainter.hierarchy.Unit;
import apainter.misc.Util;

public class CPULayerData extends LayerData{

	private PixelDataIntBuffer renderingbuffer;
	private Image renderingimage;
	private MemoryImageSource imagesource;
	private int core = Runtime.getRuntime().availableProcessors();

	//////////////////////////////FIXME debug
	private ImageFrame f;



	public CPULayerData(Canvas canvas) {
		super(canvas);
		init();
	}

	private void init(){
		int w = getWidth(),h=getHeight();
		renderingbuffer = PixelDataIntBuffer.create(w,h);
		renderingbuffer.setData(0xffffffff, rect());
		ColorModel m =
			new DirectColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), 32, 0xff0000, 0xff00, 0xff, 0xff000000, true, Transparency.OPAQUE);
		imagesource = new MemoryImageSource(w,h,m,
				renderingbuffer.getData(), 0, w);
		imagesource.setAnimated(true);
		renderingimage = Toolkit.getDefaultToolkit().createImage(imagesource);

		rendering();

		//FIXME debug
		f = new ImageFrame(renderingimage, "Debag Canvas");
		f.setLocation(500, 200);
		f.setVisible(true);
	}

	public Image getImage(){
		return renderingimage;
	}

	int i=0;
	@Override
	protected Layer createLayer(int id) {
		//TODO 名前決め
		CPULayer l = new CPULayer(id, "test", getWidth(), getHeight(),canvas,this);
		//TODO 削除
		if(i==0){
			int[] colors = new int[401*200];
			Arrays.fill(colors, 0xffff0000);
			l.setPixels(colors, 0, 0, 401, 200);
			i++;
		}else if(i==1){
			int[] colors = new int[401*200];
			Arrays.fill(colors, 0xff000000);
			l.setPixels(colors, 201, 0, 200, 401);
			i++;
		}
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
			runs.add(new _Rendering(r));
		}

		CPUParallelWorkThread.exec(runs);
		imagesource.newPixels(clip.x, clip.y, clip.width, clip.height);
	}

	private class _Rendering implements Runnable{
		Rectangle clip;
		_Rendering(Rectangle clip) {this.clip = clip;}
		public void run(){
			renderingbuffer.setData(0xffffffff, clip);
			Unit<LayerHandler> unit =layerlist.getTopLevelUnit();

			ArrayList<Element<LayerHandler>> elements = unit.getElements();


			for(Element<LayerHandler> e:elements){
				Layer l = e.getProperty().getLayer();
				l.render(renderingbuffer, clip);
			}

			imagesource.newPixels(clip.x, clip.y, clip.width, clip.height);
		}
	}


	@Override
	public JComponent testMethod_createViewPanel() {
		ImageView view = new ImageView(getImage());
		return view;
	}

}
