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
import apainter.data.PixelDataIntBuffer;
import apainter.drawer.DrawEvent;
import apainter.gui.test.TestImageView;
import apainter.hierarchy.Element;
import apainter.hierarchy.Unit;

public class CPULayerData extends LayerData{

	private PixelDataIntBuffer renderingbuffer;
	private Image renderingimage;
	private MemoryImageSource imagesource;


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
		renderingimage = Toolkit.getDefaultToolkit().createImage(imagesource);
		rendering();
	}

	public Image getImage(){
		return renderingimage;
	}

	private Rectangle rect(){
		return new Rectangle(0,0,getWidth(),getHeight());
	}

	int i=0;
	@Override
	protected Layer createLayer() {
		//TODO 名前決め
		CPULayer l = new CPULayer(getNextLayerID(), "test", getWidth(), getHeight());
		if(i==0){
			int[] colors = new int[400*200];
			Arrays.fill(colors, 0xffff0000);
			l.setPixels(colors, 0, 0, 400, 200);
			i++;
		}else if(i==1){
			System.out.println("koko");
			int[] colors = new int[400*200];
			Arrays.fill(colors, 0xff00ff00);
			l.setPixels(colors, 200, 0, 200, 400);
			i++;
		}
		return l;
	}

	@Override
	void draw(DrawEvent e) {
		LayerHandler l = e.getTarget();
		if(!layerlist.contains(l) || !l.isDrawable())return;
		Layer layer = l.getLayer();
		layer.paint(e);
	}

	@Override
	void rendering() {
		rendering(rect());
	}

	@Override
	void rendering(Rectangle clip) {
		if(clip==null)return;
		clip = rect().intersection(clip);
		if(clip.isEmpty())return;
		renderingbuffer.setData(0xffffffff, clip);
		Unit<LayerHandler> unit =layerlist.getTopLevelUnit();

		ArrayList<Element<LayerHandler>> elements = unit.getElements();




		for(Element<LayerHandler> e:elements){
			Layer l = e.getProperty().getLayer();
			l.render(renderingbuffer, clip);
		}


		imagesource.newPixels(clip.x, clip.y, clip.width, clip.height);

	}


	@Override
	public JComponent testMethod_createViewPanel() {
		TestImageView view = new TestImageView(getImage());
		return view;
	}

}
