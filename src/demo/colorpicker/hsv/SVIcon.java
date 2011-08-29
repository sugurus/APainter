package demo.colorpicker.hsv;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

import javax.swing.Icon;

public class SVIcon implements Icon{

	private int size;
	private Image sv;
	private double h=0;
	private double s=1,v=1;
	private Color rgb = Color.red;

	public SVIcon(int size) {
		if(size < 1)throw new IllegalArgumentException("size:"+size);
		this.size = size;
		grayImage();
	}

	private void grayImage(){
		int[] map = new int[size*size];
		int l = size-1;
		for(int v=l,y=0;y<size;v--,y++){
			double V = (double)v/l;
			for(int s=0;s<size;s++){
				double S = (double)s/l;
				double A = 1-V*S;
				double C;
				if(A==0){
					C=0;
				}
				else{
					C=(1-S)*V/A;
				}
				int a=(int)(255*A);
				int c=(int)(255*C);
				map[y*size+s] = a<<24 | c<<16 | c <<8 | c;
			}
		}
		MemoryImageSource s = new MemoryImageSource(size, size, map, 0, size);
		sv = Toolkit.getDefaultToolkit().createImage(s);
	}

	public void setSVformXY(int x,int y){
		if(x<0)x = 0;
		else if(x >=size)x = size-1;
		if(y<0)y=0;
		else if(y>=size)y = size-1;
		y = size-1-y;
		s = (double)x/(size-1);
		v = (double)y/(size-1);
	}

	public void setSV(double s,double v){
		if(s < 0)s=0;
		else if(s>1)s = 1;
		if(v < 0)v=0;
		else if(v>1)v=1;
		this.s = s;
		this.v = v;
	}

	public double getS(){
		return s;
	}

	public double getV(){
		return v;
	}

	public Image getSVImage(){
		return sv;
	}

	//速度は必要ないから適当
	public void setH(double h){
		while(h<=0){
			h+=360;
		}
		h%=360;
		h/=360;
		this.h=h;
		int c = Color.HSBtoRGB((float)h, 1, 1);
		rgb = new Color(c);
	}

	public double getH(){
		return h*360;
	}

	public int getRGB(double s,double v){
		return Color.HSBtoRGB((float)h,(float)s,(float)v);
	}

	@Override
	public void paintIcon(Component c, Graphics gra, int x, int y) {
		Graphics2D g = (Graphics2D)gra.create(x,y,size,size);

		g.setColor(rgb);

		g.fillRect(0, 0, size, size);
		g.drawImage(sv, 0, 0, null);

		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		int xx = (int) (s *(size-1))-5;
		int yy = size-1-(int)(v*(size-1))-5;
		g.drawArc(xx, yy, 10, 10, 0, 360);
		g.setColor(Color.white);
		g.drawArc(xx+1, yy+1, 8, 8, 0, 360);


		g.dispose();
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}
}
