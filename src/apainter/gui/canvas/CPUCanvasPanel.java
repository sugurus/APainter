package apainter.gui.canvas;


import static apainter.Util.*;
import static java.awt.RenderingHints.*;
import static java.lang.Math.*;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;

import apainter.Util;
import apainter.construct.Angle;


public class CPUCanvasPanel extends JComponent{

	private Image renderingImage=null;
	private VolatileImage zoomImage,rotImage;
	private CanvasView parent;
	private boolean initedVolatile=false;


	public CPUCanvasPanel(Image img) {
		renderingImage = img;
	}

	public void setCanvasView(CanvasView v){
		parent = v;
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,int h) {
		if(img!=renderingImage)return false;
		Rectangle r = new Rectangle(x,y,w,h);
		renderingZoomImage(r);
		repaint();
		return true;
	}
	/**
	 * 全てをレンダリングし直します。
	 */
	public void fullRendering(){
		renderingZoomImage(null);
	}

	/**
	 * 回転画像だけ際レンダリングします
	 */
	public void rotation(){
		renderingRotImage();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(!initedVolatile){
			initVolatile();
			if(!initedVolatile)return;
		}

		if(checkVImage(zoomImage)!=0)renderingZoomImage(null);
		else if(checkVImage(rotImage)!=0)renderingRotImage();
		g.drawImage(rotImage,0,0,null);
	}

	/**
	 * 画面のサイズが更新された、などと言ったときに呼び出してください。<br>
	 * メモリ割愛のため、画面更新を自動的に取得したりしません。
	 */
	public void init(){
		int width = getWidth(),height = getHeight();
		int l = (int)(floor(hypot(width,height)));

		if(!initedVolatile||zoomImage==null||
			zoomImage.getWidth()!=l||
			rotImage.getWidth()!=width||
			rotImage.getHeight()!=height){
			initVolatile();
		}
	}


	private void initVolatile(){
		int width = getWidth();
		int height = getHeight();
		int l = (int) floor(hypot(width,height));
		if(zoomImage!=null)zoomImage.flush();
		if(rotImage!=null)rotImage.flush();
		zoomImage = createVImage(l, l, true);
		rotImage = createVImage(width,height,true);
		if(zoomImage!=null){
			renderingZoomImage(null);
			initedVolatile = true;
		}else initedVolatile = false;
	}

	private void renderingZoomImage(Rectangle rect){
		int width = getWidth();
		int height = getHeight();
		int l = (int) floor(hypot(width,height));
		Dimension s = getImageSize(renderingImage);

		//zoomImageが内容を失っていないかチェック。
		switch(checkVImage(zoomImage)){
		case 2://※breakがないのは意図的。
			zoomImage.flush();
			zoomImage = createVImage(l, l, true);
		case 1:
			rect = null;
		}

		//サイズチェック
		Dimension d = getImageSize(zoomImage);
		if(d.width!=l && d.height!=l){
			zoomImage.flush();
			zoomImage = createVImage(l, l, true);
			rect = null;
		}
		do{

			//Graphics2D取得
			Graphics2D g =zoomImage.createGraphics();

			if(g==null){
				rect = null;
				while(g==null){
					zoomImage.flush();
					zoomImage = createVImage(l, l, true);
					g = zoomImage.createGraphics();
				}
			}

			//拡大、移動を取得設定
			Dimension size = parent.getSize();
			double zoom = parent.getZoom();
			AffineTransform af = AffineTransform.getTranslateInstance((l-width)/2d,(l-height)/2d);
			af.translate(parent.getDefaultCenterX()+size.width/2d, parent.getDefaultCenterY()+size.height/2d);
			af.scale(zoom, zoom);
			af.translate(-s.width/2d, -s.height/2d);

			//描画範囲設定
			if(rect!=null){
				g.setClip(af.createTransformedShape(rect));
			}

			//無色にする。
			{
				Graphics2D gg = (Graphics2D)g.create();
				gg.setComposite(AlphaComposite.Clear);
				gg.fillRect(0, 0, l, l);
				gg.dispose();
			}

			//画像の描画
			//描画　近傍補完
			g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.transform(af);
			g.drawImage(renderingImage,0,0,this);

			g.dispose();

		}while(zoomImage.contentsLost());
		renderingRotImage();
	}

	//面倒くさいので、常に全面更新。全部グラボ任せだから良いでしょう。
	private void renderingRotImage(){
		int width = getWidth(),height = getHeight();
		int l = (int)(floor(hypot(width,height)));

		//zoomImageが内容を失っていないかとサイズチェック。
		{
			Dimension d = getImageSize(zoomImage);
			if(checkVImage(zoomImage) !=0 || d.width!=l || d.height!=l){
				renderingZoomImage(null);
			}
		}

		if(checkVImage(rotImage) == 2)//画像が使えない
		{
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
			AffineTransform af = AffineTransform.getTranslateInstance(width/2d, height/2d);
			af.rotate(angle.radian);
			af.translate(-l/2d, -l/2d);

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
		GraphicsConfiguration g  = getGraphicsConfiguration();
		return g==null?null:
			g.createCompatibleVolatileImage(width, height,transparency?3:1);
	}

}
