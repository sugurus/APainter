package demo.colorpicker.hsv;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class BoxH implements HSelecterIcon{

	int size;
	int width;
	Image img;

	boolean visiblesv;
	double h=0,s=1,v=1;
	Color gray=new Color(0,true);

	public BoxH(int size,int width) {
		if(size < 1)throw new IllegalArgumentException("size:"+size);
		if(width <1)throw new IllegalArgumentException("width:"+width);
		this.size = size;
		this.width = width;
		createImage();
	}

	private void createImage(){
		BufferedImage i1 = new BufferedImage(1,size,1),i2 = new BufferedImage(1,size,1);
		int[] p = ((DataBufferInt)i1.getRaster().getDataBuffer()).getData();
		for(int y=0;y<size;y++){
			float h = (float)y/(size-1);
			p[y] = Color.HSBtoRGB(h, 1, 1);
		}
		Graphics2D g = i2.createGraphics();
		g.drawImage(i1,0,0,null);
		g.dispose();
		i1.flush();
		img = i2;
	}


	@Override
	public void paintIcon(Component c, Graphics gra, int x, int y) {
		Graphics2D g = (Graphics2D)gra.create(x,y,width,size);
		g.drawImage(img,0,0,width,size,0,0,1,size,null);
		if(visiblesv){
			g.setColor(gray);
			g.fillRect((width>>1), 0, width>>1, size);
		}

		double yy = (size-1)*h - width/2d;
		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.drawArc(1, (int)yy, width-3, width-3, 0, 360);
		g.setColor(Color.white);
		g.drawArc(2, (int)yy, width-5, width-5, 0, 360);

		g.dispose();
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

	@Override
	public double getH() {
		return h*360;
	}

	@Override
	public double getH(int x, int y) {
		double h= (double)y/(size-1);
		if(h<0)h=0;
		else if(h > 1)h = 1;
		return h*360;
	}

	@Override
	public void setH(double h) {
		h /=360;
		h %=1;
		if(h < 0)h+=1;
		this.h=h;
	}

	@Override
	public void setSV(double s, double v) {
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
