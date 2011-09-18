package demo.colorpicker.hsv;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class CercleH implements HSelecterIcon{
	static final int outline = 6;
	static final float rot=.25f+.1666666666f;

	boolean visiblesv;
	double h,s,v;
	Color gray;
	Image img;
	Shape svShape;

	final int size;
	final int r;//内輪
	final int width;


	public CercleH(int r,int width) {
		if(r < 1)throw new IllegalArgumentException("r:"+r);
		if(width<1)throw new IllegalArgumentException("width:"+width);

		this.r = r;
		this.width = width;

		size = 2*r+2*width + 2*outline;

		h = 0;
		s =v = 1;
		gray  = new Color(0,true);
		visiblesv = true;
		createImage();
	}

	public boolean contain(int x,int y){
		double c = size /2d;
		double X = x-c,Y=y-c;
		double R = Math.hypot(X,Y);
		return r-2 <= R && r+width+2 >=R;
	}

	private float dgree(int x,int y){
		float d;
		double c = size /2d;
		double X = x-c,Y=y-c;
		if(X==0){
			d= Y>=0?0.25f:0.75f;
		}else{
			d=(float) (Math.atan(Y/X)/2/Math.PI)+(X>=0?0:0.5f);
		}
		return d+rot;
	}

	private void createImage(){
		Double out = new Double(outline, outline, size-2*outline, size-2*outline),
		mid = new Double(outline+width/2d,outline+width/2d,size-2*outline-width,size-2*outline-width),
		in = new Double(outline+width,outline+width,size-2*outline-2*width,size-2*outline-2*width);

		Area rect=new Area(new Rectangle(0,0,size,size)),
			outa = new Area(out),mida=new Area(mid),ina =new Area(in);
		outa.subtract(ina);
		mida.subtract(ina);
		rect.subtract(outa);

		svShape = mida;

		BufferedImage tmp = new BufferedImage(size,size,2),
			ret = new BufferedImage(size,size,2);
		int[] pix = ((DataBufferInt)tmp.getRaster().getDataBuffer()).getData();

		for(int y=0;y<size;y++){
			for(int x=0;x<size;x++){
				if(contain(x, y)){
					float dg = dgree(x,y);
					pix[x+y*size] = Color.HSBtoRGB(dg, 1, 1);
				}
			}
		}

		Graphics2D g = ret.createGraphics();
		g.drawImage(tmp,0,0,null);
		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		Graphics2D gg = (Graphics2D)g.create();
		gg.setComposite(AlphaComposite.Clear);
		gg.fill(rect);
		gg.dispose();

		//outline
		g.setColor(new Color(0x22000000,true));
		g.drawArc(outline-3,outline-3,size-2*outline+3*2-1,size-2*outline+3*2-1, 0, 360);
		GradientPaint p = new GradientPaint(0, 0, Color.white, size, size, Color.lightGray);
		g.setPaint(p);
		g.drawArc(outline-2,outline-2,size-2*outline+2*2-1,size-2*outline+2*2-1, 0, 360);
		g.drawArc(outline+width+1,outline+width+1,r*2-2,r*2-2,0,360);
		p = new GradientPaint(size, size, Color.white, 0, 0, Color.lightGray);
		g.setPaint(p);
		g.drawArc(outline-1,outline-1,size-2*outline+1*2-1,size-2*outline+1*2-1, 0, 360);
		g.drawArc(outline+width+2,outline+width+2,r*2-4,r*2-4,0,360);

		g.dispose();

		img = ret;

	}


	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 =(Graphics2D)g.create();
		g2.drawImage(img,x,y,null);

		g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		if(visiblesv){
			g2.setColor(gray);
			g2.fill(svShape);
		}

		double d = -h+rot;
		double rad = d*2*Math.PI;
		double xx = (r+width/2d)*Math.cos(rad)-width/2d+size/2d;
		double yy = -(r+width/2d)*Math.sin(rad)-width/2d+size/2d;
		g2.setColor(Color.black);
		g2.drawArc(x+(int)xx+2, y+(int)yy+2, width-3, width-3, 0, 360);
		g2.setColor(Color.white);
		g2.drawArc(x+(int)xx+3, y+(int)yy+3, width-5, width-5, 0, 360);

		g2.dispose();
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

	@Override
	public double getH() {
		return h;
	}

	@Override
	public double getH(int x, int y) {
		return dgree(x, y)*360;
	}


	@Override
	public void setHSV(double h,double s, double v) {
		h /=360;
		h %=1;
		if(h < 0)h+=1;
		this.h=h;
		if(s >1)s=1;
		else if(s<0)s=0;
		if(v >1)v=1;
		else if(v<0)v=0;
		this.s = s;
		this.v = v;
		double A = 1-v*s;
		double C;
		if(A==0){
			C=0;
		}
		else{
			C=(1-s)*v/A;
		}
		int a=(int)(255*A);
		int c=(int)(255*C);
		gray = new Color( a<<24|c<<16|c <<8|c , true);
	}

	@Override
	public void setVisibleSV(boolean b) {
		visiblesv = b;
	}

	@Override
	public boolean isVisibleSV() {
		return visiblesv;
	}

}
