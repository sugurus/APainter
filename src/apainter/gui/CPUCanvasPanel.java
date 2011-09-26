package apainter.gui;

import static apainter.misc.Util.*;
import static java.awt.RenderingHints.*;
import static java.lang.Math.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import apainter.misc.Angle;
import apainter.misc.Util;


public class CPUCanvasPanel extends JComponent implements CanvasViewRendering{

	//この画像はJavaで管理できない。
	private BufferedImage renderingImage=null;
	private AreaAvarageReducedImage smallimage = null;
//	private VolatileImage zoomImage;
	//ビデオメモリにrenderingImageを全部送るよりも、
	//Javaが管理するBufferedImageに書き込んで、ビデオメモリに送ってもらったほうが
	//速い気がする。
	private BufferedImage zoomImage;
	private boolean fastRendering=true;
	private VolatileImage rotImage;

	private CanvasView parent;
	private boolean initedVolatile=false;


	public CPUCanvasPanel(BufferedImage img) {
		renderingImage = img;
		smallimage =new AreaAvarageReducedImage(img);
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				init();
			}
			public void componentShown(ComponentEvent e) {
				init();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}

	@Override
	public void dispose() {
		zoomImage.flush();
		zoomImage = null;
		renderingImage.flush();
		renderingImage = null;
		rotImage.flush();
		rotImage = null;
		smallimage.flush();
		smallimage =null;
	}

	@Override
	public void qualityRendering(boolean b){
		b= !b;
		if(fastRendering!=b){
			fastRendering=b;
			if(SwingUtilities.isEventDispatchThread())renderingSmallImage(null);
			renderingZoomImage(null);
			repaint();
		}
	}

	public void setCanvasView(CanvasView v){
		parent = v;
		if(parent.getZoom()<1)
			smallimage.setZoom((float)parent.getZoom());
	}


	/**
	 * 全てをレンダリングし直します。
	 */
	public void rendering(){
		if(SwingUtilities.isEventDispatchThread())renderingSmallImage(null);
		renderingZoomImage(null);
		repaint();
	}

	/**
	 * 指定された範囲レンダリングします。
	 * @param r
	 */
	public void rendering(final Rectangle r){
		if(SwingUtilities.isEventDispatchThread())renderingSmallImage(null);
		renderingZoomImage(r);
		repaint();
	}


	/**
	 * 回転画像だけ際レンダリングします
	 */
	public void rotation(){
		if(!SwingUtilities.isEventDispatchThread()){
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					rotation();
				}
			});
		}else{
			renderingRotImage();
			repaint();
		}
	}
	boolean moveflag = false;
	@Override
	public void repaintMove() {
		moveflag = true;
		renderingZoomImage(null);
		moveflag = false;
		repaint();
	}


	@Override
	protected void paintComponent(Graphics g) {
		if(!initedVolatile){
			initVolatile();
			if(!initedVolatile)return;
		}
//		if(checkVImage(zoomImage)!=0){
//			renderingZoomImage(null);
//		}
		else if(checkVImage(rotImage)!=0)renderingRotImage();
		g.drawImage(rotImage,0,0,null);
	}


	public void init(){
		int width = getWidth(),height = getHeight();
		int l = (int)(floor(hypot(width,height)));

		if(!initedVolatile||zoomImage==null||
			zoomImage.getWidth(null)!=l||
			rotImage.getWidth()!=width||
			rotImage.getHeight()!=height){
			initVolatile();
			repaint();
		}
	}


	private void initVolatile(){
		int width = getWidth();
		int height = getHeight();
		int l = (int) floor(hypot(width,height));
		if(rotImage!=null)rotImage.flush();
		if(zoomImage!=null)zoomImage.flush();
		if(getWidth()==0||getHeight()==0)return;
		rotImage = createVImage(width,height,true);
//		zoomImage = createVImage(l, l, true);
		GraphicsConfiguration gc = getGraphicsConfiguration();
		if(gc!=null)
			zoomImage = gc.createCompatibleImage(l, l, Transparency.TRANSLUCENT);
		else
			zoomImage = null;
		if(zoomImage!=null){
			renderingZoomImage(null);
			initedVolatile = true;
		}else initedVolatile = false;
	}


	private void renderingZoomImage(final Rectangle rect){
		if(getWidth()==0||getHeight()==0){
			return;
		}
		if(!SwingUtilities.isEventDispatchThread()){
			renderingSmallImage(rect);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					renderingZoomImage(rect);
				}
			});
		}else{
			if(fastRendering){
				_renderingZoomImageVolatile(rect);
			}else{
				_renderingZoomImageBuffered(rect);
			}
			renderingRotImage();
		}
	}

	private void renderingSmallImage(Rectangle rect){
		double zoom = parent.getZoom();
		if(!fastRendering&&zoom < 1){
			if(smallimage.isEqual(zoom)){
				Dimension s = getImageSize(renderingImage);
				if(!moveflag){
					Rectangle clip;
					if(rect==null){
						clip = new Rectangle(s);
					}else{
						clip = new Rectangle(s).intersection(rect);
					}
					if(!clip.isEmpty()){
						smallimage.update(clip);
					}
				}
			}else{
				smallimage.setZoom((float)zoom);
			}
		}
	}

	private void _renderingZoomImageBuffered(Rectangle rect){

		double zoom = parent.getZoom();
		if(zoom>=1){//拡大はVolatileImageに任せる
			_renderingZoomImageVolatile(rect);
			return;
		}

		Dimension s = getImageSize(renderingImage);


		//ZoomImageに書き込み。
		int width = getWidth();
		int height = getHeight();
		int l = (int) floor(hypot(width,height));
		Dimension size = parent.getSize();
		AffineTransform af = AffineTransform.getTranslateInstance(
				(int)((l-width)/2d+parent.getDefaultCenterX()+size.width/2d),
				(int)((l-height)/2d+parent.getDefaultCenterY()+size.height/2d)
		);
		af.scale(zoom, zoom);
		af.translate((int)(-s.width/2d), (int)(-s.height/2d));
		Point2D.Double pd = new Point2D.Double();
		af.transform(pd, pd);
//		do{
			//サイズチェック
//			switch(checkVImage(zoomImage)){
//			case 2://※breakがないのは意図的。
//				if(zoomImage!=null)
//					zoomImage.flush();
//				zoomImage = createVImage(l, l, true);
//			case 1:
//				rect = null;
//			}
			Dimension d = getImageSize(zoomImage);
			if(d.width!=l && d.height!=l){
				zoomImage.flush();
//				zoomImage = createVImage(l, l, true);
				GraphicsConfiguration gc = getGraphicsConfiguration();
				if(gc!=null)
					zoomImage = gc.createCompatibleImage(l, l, Transparency.TRANSLUCENT);
				else
					zoomImage = new BufferedImage(l, l, BufferedImage.TYPE_INT_ARGB);
				rect = null;
			}

			//Graphics2D取得
			Graphics2D g =zoomImage.createGraphics();

			if(g==null){
				rect = null;
				while(g==null){
					zoomImage.flush();
//					zoomImage = createVImage(l, l, true);
					GraphicsConfiguration gc = getGraphicsConfiguration();
					if(gc!=null)
						zoomImage = gc.createCompatibleImage(l, l, Transparency.TRANSLUCENT);
					else
						zoomImage = new BufferedImage(l, l, BufferedImage.TYPE_INT_ARGB);
					g = zoomImage.createGraphics();
				}
			}

			if(rect!=null){
				rect.x-=1;
				rect.y-=1;
				rect.width+=2;
				rect.height+=2;
				g.setClip(af.createTransformedShape(rect));
			}

			g.setBackground(new Color(0,true));
			g.clearRect(0, 0, l, l);
			smallimage.drawImage(g, (int) pd.x, (int) pd.y);
			g.dispose();

//		}while(zoomImage.contentsLost());

	}


	private void _renderingZoomImageVolatile(Rectangle rect){
		int width = getWidth();
		int height = getHeight();
		int l = (int) floor(hypot(width,height));
		Dimension s = getImageSize(renderingImage);

//		do{
			//サイズチェック
//			switch(checkVImage(zoomImage)){
//			case 2://※breakがないのは意図的。
//				if(zoomImage!=null)
//					zoomImage.flush();
//				zoomImage = createVImage(l, l, true);
//			case 1:
//				rect = null;
//			}
			Dimension d = getImageSize(zoomImage);
			if(d.width!=l && d.height!=l){
				zoomImage.flush();
//				zoomImage = createVImage(l, l, true);
				GraphicsConfiguration gc = getGraphicsConfiguration();
				if(gc!=null)
					zoomImage = gc.createCompatibleImage(l, l, Transparency.TRANSLUCENT);
				else
					zoomImage = new BufferedImage(l, l, BufferedImage.TYPE_INT_ARGB);
				rect = null;
			}

			//Graphics2D取得
			Graphics2D g =zoomImage.createGraphics();

			if(g==null){
				rect = null;
				while(g==null){
					zoomImage.flush();
//					zoomImage = createVImage(l, l, true);
					GraphicsConfiguration gc = getGraphicsConfiguration();
					if(gc!=null)
						zoomImage = gc.createCompatibleImage(l, l, Transparency.TRANSLUCENT);
					else
						zoomImage = new BufferedImage(l, l, BufferedImage.TYPE_INT_ARGB);
					g = zoomImage.createGraphics();
				}
			}

			//拡大、移動を取得設定
			Dimension size = parent.getSize();
			double zoom = parent.getZoom();
			AffineTransform af = AffineTransform.getTranslateInstance(
					(int)((l-width)/2d+parent.getDefaultCenterX()+size.width/2d),
					(int)((l-height)/2d+parent.getDefaultCenterY()+size.height/2d)
			);
			af.scale(zoom, zoom);
			af.translate((int)(-s.width/2d), (int)(-s.height/2d));


			//無色にする。
			{
				Graphics2D gg = (Graphics2D)g.create();
				gg.setComposite(AlphaComposite.Clear);
				if(rect!=null){
					rect.x-=1;
					rect.y-=1;
					rect.width+=2;
					rect.height+=2;
					gg.setClip(af.createTransformedShape(rect));
				}
				gg.fillRect(0, 0, l, l);
				gg.dispose();
			}

			//画像の描画
			//描画　近傍補完
			if(zoom>1){
				g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			}else if(zoom < 1){
				g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
			}
			g.transform(af);
			//描画範囲設定
			if(rect!=null)
				g.setClip(rect);
			g.drawImage(renderingImage,0,0,null);

			g.dispose();

//		}while(zoomImage.contentsLost());
	}


	private void renderingRotImage(){
		if(getWidth()==0||getHeight()==0)return;
		int width = getWidth(),height = getHeight();
		int l = (int)(floor(hypot(width,height)));

		//zoomImageが内容を失っていないかとサイズチェック。
//		{
//			Dimension d = getImageSize(zoomImage);
//			if((checkVImage(zoomImage) !=0) || d.width!=l || d.height!=l){
//				renderingZoomImage(null);
//			}
//		}

		if(checkVImage(rotImage) == 2)//画像が使えない
		{
			if(rotImage!=null)
				rotImage.flush();
			rotImage = createVImage(l, l, true);
		}

		Dimension d = getImageSize(rotImage);
		//サイズチェック
		if(d.width!=width && d.height!=height){
			rotImage.flush();
			rotImage = createVImage(l, l, true);
		}

		do{
			Angle angle = parent.getAngle();
			AffineTransform af = AffineTransform.getTranslateInstance((width>>1), (height>>1));
			af.rotate(angle.radian);
			af.translate(-(l>>1), -(l>>1));
			if(parent.isReverse()){
				af.scale(-1, 1);
				af.translate(width, 0);
			}

			Graphics2D g = rotImage.createGraphics();
			if(g==null){
				while(g==null){
					rotImage.flush();
					rotImage = createVImage(width, height, true);
					g = rotImage.createGraphics();
				}
			}

			//無色にする。
			{
				Graphics2D gg = (Graphics2D)g.create();
				gg.setComposite(AlphaComposite.Clear);
				gg.fillRect(0, 0, l, l);
				gg.dispose();
			}

			g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
			g.transform(af);
			g.drawImage(zoomImage,0,0,null);
			g.dispose();

		}while(rotImage.contentsLost());
	}

	//0再生必要なし。　
	//1画像を作り直す必要はないが、内容は作り直す必要あり
	//2画像そのものを作り直す必要あり
	private int checkVImage(VolatileImage img){
		if(img==null)return 2;
		GraphicsConfiguration gc = getGraphicsConfiguration();
		int t;
		if((t=img.validate(gc))==VolatileImage.IMAGE_INCOMPATIBLE)
				return 2;
		else if(t==VolatileImage.IMAGE_RESTORED)
			return 1;
		return 0;
	}

	@Override
	public Dimension getPreferredSize() {
		return Util.getImageSize(renderingImage);
	}

	private VolatileImage createVImage(int width,int height,boolean transparency){
		if(width==0 || height ==0)return null;
		GraphicsConfiguration g  = getGraphicsConfiguration();
		if(g==null){
			g = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		}
		return g.createCompatibleVolatileImage(width, height,transparency?3:1);
	}

}
