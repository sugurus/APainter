package apainter.gui.canvas;

import static apainter.misc.Utility_PixelFunction.*;
import static java.lang.Math.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import apainter.canvas.cedt.cpu.CPUParallelWorkThread;

public class SmallImage {
	private static final int SHIFT = 12;
	private static final int length = 1<<SHIFT;


	private final BufferedImage sourceimage,smallimage;
	private final int[] source;
	private final int[] obj;
	private final int[] dst;
	private final int sw,sh;
	private final Rectangle rect;

	private int dw,dh;
	private float zoom;
	private int pixelSize;

	public SmallImage(BufferedImage img) {
		if(img.getType()!= BufferedImage.TYPE_INT_RGB){
			throw new RuntimeException("image type is not TYPE INT RGB");
		}
		zoom =1f;
		pixelSize =1024;
		sourceimage = img;
		sw = dw = img.getWidth();
		sh = dh = img.getHeight();
		obj = new int[sw*sh];
		smallimage = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = smallimage.createGraphics();
		g.drawImage(sourceimage,0,0,null);
		g.dispose();
		rect = new Rectangle(0,0,sw,sh);
		source = ((DataBufferInt)img. getRaster().getDataBuffer()).getData();
		dst = ((DataBufferInt)smallimage. getRaster().getDataBuffer()).getData();
	}


	public void drawImage(Graphics g,int x,int y){
		g.drawImage(smallimage,x,y,x+dw,y+dh,0,0,dw,dh,null);
	}

	public BufferedImage getSmallImage(){
		BufferedImage b = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = b.createGraphics();
		g.drawImage(smallimage,0,0,dw,dh,0,0,dw,dh,null);
		g.dispose();
		return b;
	}

	public boolean isEqual(double zoom){
		return (float)zoom == this.zoom;
	}

	public boolean isEqual(float zoom){
		return zoom == this.zoom;
	}

	public float getZoom(){
		return zoom;
	}

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

	public void update(){
		update(null);
	}

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


		int tsize = CPUParallelWorkThread.getThreadSize();
		Runnable[] runs = new Runnable[tsize];
		final int ll = (int) ((1f- (sxfloat -sx))*pixelSize);

		for(int i=0;i<tsize;i++){
			final int ssy = sy+(ey-sy)*i/tsize;
			final int eey = sy+(ey-sy)*(i+1)/tsize;
			runs[i] = new Runnable() {
				public void run() {
					for(int y=ssy;y<eey;y++){
						int xpos = dstStartx;
						int rgb = pixel(source, sx, y, sw);
						int r=r(rgb)*ll,g=g(rgb)*ll,b=b(rgb)*ll,l=length-ll;
						int x =sx+1;
						for(;x<ex;x++){
							rgb = pixel(source, x, y, sw);
							if(l<=pixelSize){
								set(obj, argb(255, (r+r(rgb)*l)>>SHIFT,
										(g+g(rgb)*l)>>SHIFT, (b+b(rgb)*l)>>SHIFT), xpos++, y, sw);
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
						Label:if(xpos<dstEndx){
							while(x<sw){
								rgb = pixel(source, x, y, sw);
								if(l<=pixelSize){
									set(obj,
											argb(255, (r+r(rgb)*l)>>SHIFT,
											(g+g(rgb)*l)>>SHIFT, (b+b(rgb)*l)>>SHIFT), xpos, y, sw);
									break Label;
								}else{
									r+=r(rgb)*pixelSize;
									g+=g(rgb)*pixelSize;
									b+=b(rgb)*pixelSize;
									l-=pixelSize;
								}
								x++;
							}
							set(obj,argb(l*255>>SHIFT,r/(length-l),g/(length-l),b/(length-l)),xpos,y,sw);
						}
					}
				}
			};
		}

		CPUParallelWorkThread.exec(runs);
		final int ll2 = (int) ((1f-(syfloat-sy))*pixelSize);

		for(int i=0;i<tsize;i++){
			final int ssx = dstStartx+(dstEndx-dstStartx)*i/tsize;
			final int eex = dstStartx+(dstEndx-dstStartx)*(i+1)/tsize;
			runs[i] = new Runnable() {
				public void run() {
					for(int x=ssx;x<eex;x++){
						int ypos=dstStarty;
						int rgb = pixel(obj, x, sy, sw);
						int r=r(rgb)*ll2,g=g(rgb)*ll2,b=b(rgb)*ll2,l=length-ll2;
						int y = sy+1;
						for(;y<ey;y++){
							rgb = pixel(obj, x, y, sw);
							if(l<=pixelSize){
								set(dst, argb(255, (r+r(rgb)*l)>>SHIFT,
										(g+g(rgb)*l)>>SHIFT, (b+b(rgb)*l)>>SHIFT), x, ypos++, sw);
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
						Label:if(ypos<dstEndy){
							while(y<sh){
								rgb = pixel(obj, x, y, sw);
								if(l<=pixelSize){
									set(dst, argb(255, (r+r(rgb)*l)>>SHIFT,
											(g+g(rgb)*l)>>SHIFT, (b+b(rgb)*l)>>SHIFT), x, ypos, sw);
									break Label;
								}else{
									r+=r(rgb)*pixelSize;
									g+=g(rgb)*pixelSize;
									b+=b(rgb)*pixelSize;
									l-=pixelSize;
								}
								y++;
							}
							set(dst,argb(l*255>>>SHIFT,r/(length-l),g/(length-l),b/(length-l)),x,ypos,sw);
						}
					}
				}
			};
		}

		CPUParallelWorkThread.exec(runs);

		return true;
	}

}
