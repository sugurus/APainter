package apainter.gui;

import static apainter.misc.Utility_PixelFunction.*;
import static java.lang.Math.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import apainter.canvas.cedt.cpu.CPUParallelWorker;

/**
 * 面積平均法により画像を縮小します。<br>
 * このクラスが扱える画像は、TYPE_INT_RGBのBufferedImageのみです。<br>
 * このインスタンスは一度生成すると、画像の縮小率を変更しても
 * 新たなメモリーを取得する必要がありません。<br>
 * また、部分的なレンダリングが可能です。
 * @author nodamushi
 *
 */
public class AreaAvarageReducedImage {
	private static final int SHIFT = 12;
	private static final int length = 1<<SHIFT;


	private BufferedImage sourceimage,smallimage;
	private int[] source;
	private int[] xreduce;
	private int[] dst;
	private final int sw,sh;
	private final Rectangle rect;

	private int dw,dh;
	private float zoom;
	private int pixelSize;

	/**
	 * 与えられた画像の縮小画像を生成します。<br>
	 * 画像のタイプはTYPE_INT_RGBでなくてはなりません。
	 * @param img TYPE_INT_RGBのBufferedImage画像
	 * @throws IllegalArgumentException imgのタイプがTYPE_INT_RGBでない。
	 * @see {@link BufferedImage}
	 * @see {@link BufferedImage#TYPE_INT_RGB}
	 */
	public AreaAvarageReducedImage(BufferedImage img) throws IllegalArgumentException{
		zoom =1f;
		pixelSize =1024;
		sourceimage = img;
		sw = dw = img.getWidth();
		sh = dh = img.getHeight();
		xreduce = new int[sw*sh];
		smallimage = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = smallimage.createGraphics();
		g.drawImage(sourceimage,0,0,null);
		g.dispose();
		rect = new Rectangle(0,0,sw,sh);
		source = ((DataBufferInt)img. getRaster().getDataBuffer()).getData();
		dst = ((DataBufferInt)smallimage. getRaster().getDataBuffer()).getData();
	}

	public void flush(){
		smallimage.flush();
		source = xreduce =  dst = null;
		sourceimage = null;
	}


	/**
	 * 座標(x,y)に縮小画像を書きます。<br>
	 * getImageメソッドから縮小画像を取得し、書き込むよりも効率的に書き込みます。
	 * @param g 書き込み先のGraphicsオブジェクト
	 * @param x 座標x
	 * @param y 座標y
	 */
	public void drawImage(Graphics g,int x,int y){
		g.drawImage(smallimage,x,y,x+dw,y+dh,0,0,dw,dh,null);
	}
	
	public void drawImage(Graphics g,int dx,int dy,int dx2,int dy2,
			int sx,int sy,int sx2,int sy2){
		g.drawImage(smallimage,dx,dy,dx2,dy2,sx,sy,sx2,sy2,null);
	}

	/**
	 * 縮小結果を得ます。<br>
	 * その際、結果のコピーが渡されます。<br>
	 * 単純にGraphicsに書き込むならばdrawImageメソッドを利用して下さい。
	 * @return
	 */
	public BufferedImage getImage(){
		BufferedImage b = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = b.createGraphics();
		g.drawImage(smallimage,0,0,dw,dh,0,0,dw,dh,null);
		g.dispose();
		return b;
	}

	/**
	 * 縮小元画像を返します。
	 * @return
	 */
	public BufferedImage getOriginalImage(){
		return sourceimage;
	}

	/**
	 * 拡大率が同じかどうかを返します
	 * @param zoom 調べたい縮小率
	 * @return 値が一致しているか否か。
	 */
	public boolean isEqual(double zoom){
		return (float)zoom == this.zoom;
	}
	/**
	 * 拡大率が同じかどうかを返します
	 * @param zoom 調べたい縮小率
	 * @return 値が一致しているか否か。
	 */
	public boolean isEqual(float zoom){
		return zoom == this.zoom;
	}

	/**
	 * 拡大率を返します
	 * @return
	 */
	public float getZoom(){
		return zoom;
	}

	/**
	 * 拡大率を設定します。<br>
	 * 値が1以上の場合は1に設定されます。<br>
	 * 値が0.05以下の場合は0.05に設定されます。
	 * @param zoom
	 */
	public void setZoom(float zoom){
		if(this.zoom!=zoom){
			if(zoom>1f)zoom = 1f;
			else if(zoom < 0.05f)zoom = 0.05f;
			this.zoom = zoom;
			pixelSize = (int)(length*zoom);
			dw = (int)ceil(sw*zoom);
			dh =(int)ceil(sh*zoom);
			update();
		}
	}

	/**
	 * 全体を縮小し直します。
	 */
	public void update(){
		update(null);
	}

	/**
	 * 指定された領域を縮小し直します。
	 * @param clip 縮小し直す領域。座標系は縮小される「前」の元画像での座標系です。
	 * @return 縮小したかどうか。falseが返ったときはclipの範囲がすべて範囲外の時です。
	 */
	public boolean update(Rectangle clip){
		if(clip!=null){
			clip = rect.intersection(clip);
			if(clip.isEmpty())return false;
		}else{
			clip = rect;
		}
		int temp;
		final int dstStartx = (int)floor(clip.x*zoom);
		temp= (int)ceil((clip.x+clip.width)*zoom);
		final int dstEndx=temp>dw?dw:temp;

		final int dstStarty = (int)floor(clip.y*zoom);
		temp= (int)ceil((clip.y+clip.height)*zoom);
		final int dstEndy =temp>dh?dh:temp;

		final float sxfloat = dstStartx/zoom;
		final float syfloat = dstStarty/zoom;
		final float exfloat = dstEndx/zoom;
		final float eyfloat = dstEndy/zoom;

		final int sx = (int)floor(sxfloat);
		final int sy = (int)floor(syfloat);
		temp= (int)ceil(eyfloat);
		final int ey =temp>sh?sh:temp;
		temp= (int)ceil(exfloat);
		final int ex = temp>sw?sw:temp;


		final int ll = (int) ((1f- (sxfloat -sx))*pixelSize);

		boolean single = (ex-sx)*(ey-sy)<100;
		//x軸方向に縮小
		new CPUParallelWorker(single){protected void task(int i, int tsize) {
			final int ssy = sy+(ey-sy)*i/tsize;
			final int eey = sy+(ey-sy)*(i+1)/tsize;
			LOOP:for(int y=ssy;y<eey;y++) {
				int xpos = dstStartx;
				int rgb = pixel(source, sx, y, sw);
				int r=r(rgb)*ll,g=g(rgb)*ll,b=b(rgb)*ll,l=length-ll;
				int x =sx+1;
				for(;x<ex;x++){
					rgb = pixel(source, x, y, sw);
					if(l<=pixelSize){
						set(xreduce, argb(255,
								(r+r(rgb)*l)>>SHIFT,
								(g+g(rgb)*l)>>SHIFT,
								(b+b(rgb)*l)>>SHIFT),
								xpos++, y, sw);
						r=r(rgb)*(pixelSize-l);
						g=g(rgb)*(pixelSize-l);
						b=b(rgb)*(pixelSize-l);
						l=length-pixelSize+l;
					}else{
						r+=r(rgb)*pixelSize;
						g+=g(rgb)*pixelSize;
						b+=b(rgb)*pixelSize;
						l-=pixelSize;
					}
				}
				if(xpos<dstEndx){
					while(x<sw){
						rgb = pixel(source, x, y, sw);
						if(l<=pixelSize){
							set(xreduce,argb(255,
									(r+r(rgb)*l)>>SHIFT,
									(g+g(rgb)*l)>>SHIFT,
									(b+b(rgb)*l)>>SHIFT),
									xpos, y, sw);
							continue LOOP;
						}else{
							r+=r(rgb)*pixelSize;
							g+=g(rgb)*pixelSize;
							b+=b(rgb)*pixelSize;
							l-=pixelSize;
						}
						x++;
					}
					set(xreduce,argb(
							l*255>>SHIFT,
							r/(length-l),
							g/(length-l),
							b/(length-l)),
							xpos,y,sw);
				}
			}//end for y
		}/*end task*/}.start();
		

		//y軸方向に縮小
		final int ll2 = (int) ((1f-(syfloat-sy))*pixelSize);
		new CPUParallelWorker(single) {protected void task(int i, int tsize) {
			final int ssx = dstStartx+(dstEndx-dstStartx)*i/tsize;
			final int eex = dstStartx+(dstEndx-dstStartx)*(i+1)/tsize;
			LOOP:for(int x=ssx;x<eex;x++){
				int ypos=dstStarty;
				int rgb = pixel(xreduce, x, sy, sw);
				int r=r(rgb)*ll2,g=g(rgb)*ll2,b=b(rgb)*ll2,l=length-ll2;
				int y = sy+1;
				for(;y<ey;y++){
					rgb = pixel(xreduce, x, y, sw);
					if(l<=pixelSize){
						set(dst, argb(255,
								(r+r(rgb)*l)>>SHIFT,
								(g+g(rgb)*l)>>SHIFT,
								(b+b(rgb)*l)>>SHIFT),
								x, ypos++, sw);
						r=r(rgb)*(pixelSize-l);
						g=g(rgb)*(pixelSize-l);
						b=b(rgb)*(pixelSize-l);
						l=length-pixelSize+l;
					}else{
						r+=r(rgb)*pixelSize;
						g+=g(rgb)*pixelSize;
						b+=b(rgb)*pixelSize;
						l-=pixelSize;
					}
				}
				if(ypos<dstEndy){
					while(y<sh){
						rgb = pixel(xreduce, x, y, sw);
						if(l<=pixelSize){
							set(dst, argb(255,
									(r+r(rgb)*l)>>SHIFT,
									(g+g(rgb)*l)>>SHIFT,
									(b+b(rgb)*l)>>SHIFT),
									x, ypos, sw);
							continue LOOP;
						}else{
							r+=r(rgb)*pixelSize;
							g+=g(rgb)*pixelSize;
							b+=b(rgb)*pixelSize;
							l-=pixelSize;
						}
						y++;
					}
					set(dst,argb(
							l*255>>>SHIFT,
							r/(length-l),
							g/(length-l),
							b/(length-l)),
							x,ypos,sw);
				}
			}//end for x
		}/*end task*/}.start();


		return true;
	}

}
