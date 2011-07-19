package apainter.canvas.layerdata;

import static apainter.Util.*;

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
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.JFrame;

import apainter.Util;
import apainter.canvas.Canvas;
import apainter.data.PixelDataIntBuffer;
import apainter.drawer.DrawEvent;
import apainter.gui.test.TestImageView;
import apainter.hierarchy.Element;
import apainter.hierarchy.Unit;

public class CPULayerData extends LayerData{

	private PixelDataIntBuffer renderingbuffer;
	private Image renderingimage;
	private MemoryImageSource imagesource;
	private int core = Runtime.getRuntime().availableProcessors();
	private ExecutorService pool = Executors.newFixedThreadPool(core);

	//////////////////////////////FIXME debug
	private JFrame f = new JFrame("Debug キャンバスの通常状態");
	private JComponent j = new JComponent() {
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
			repaint();
			return true;
		}

		protected void paintComponent(java.awt.Graphics g) {
			g.drawImage(renderingimage,0,0,this);
		}
		public java.awt.Dimension getPreferredSize() {
			return getImageSize(renderingimage);
		}
	};



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
		f.add(j);
		f.pack();
		f.setLocation(500, 200);
		f.setVisible(true);
	}

	public Image getImage(){
		return renderingimage;
	}

	private Rectangle rect(){
		return new Rectangle(0,0,getWidth(),getHeight());
	}

	int i=0;
	@Override
	protected Layer createLayer(int id) {
		//TODO 名前決め
		CPULayer l = new CPULayer(id, "test", getWidth(), getHeight(),canvas);
		//TODO 削除
		if(i==0){
			int[] colors = new int[400*200];
			Arrays.fill(colors, 0xffff0000);
			l.setPixels(colors, 0, 0, 400, 200);
			i++;
		}else if(i==1){
			int[] colors = new int[400*200];
			Arrays.fill(colors, 0xff000000);
			l.setPixels(colors, 200, 0, 200, 400);
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
		Rectangle[] rects = Util.partition(clip, core);
		ArrayList<Runnable> runs = new ArrayList<Runnable>(core);
		for(Rectangle r:rects){
			if(r.isEmpty())continue;
			runs.add(new _Rendering(r));
		}

		exec(runs);
		imagesource.newPixels(clip.x, clip.y, clip.width, clip.height);
	}

	private void exec(Collection<Runnable> run){
		Future<?>[] fs = new Future[run.size()];
		int i=0;
		for(Runnable r:run){
			fs[i++] = pool.submit(r);
		}
		for(Future<?> f:fs){
			try {
				f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
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
		TestImageView view = new TestImageView(getImage());
		return view;
	}

}
