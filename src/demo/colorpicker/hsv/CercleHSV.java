package demo.colorpicker.hsv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class CercleHSV extends JComponent{

	CercleH h;
	SVIcon sv;
	int hwidth = 15;
	double H=0,S=1,V=1;

	ComponentListener lis = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			int w = getWidth(),he=getHeight();
			int k = w>he?he:w;
			int r = k/2-CercleH.outline-hwidth;
			int s = getSVSize(r);
			sv = new SVIcon(s);
			h = new CercleH(r, hwidth);
			repaint();
		}
	};


	public int getRGB(){
		return Color.HSBtoRGB((float)H, (float)S, (float)V);
	}

	public double[] getHSV(){
		return new double[]{H*360,S,V};
	}

	public void setRGB(int rgb){
		double[] old = {H,S,V};
		float[] hsv =Color.RGBtoHSB(rgb>>16&0xff, rgb>>8&0xff, rgb&0xff, null);
		H = hsv[0];
		S = hsv[1];
		V = hsv[2];
		h.setHSV(H,S,V);
		sv.setHSV(H,S, V);
		fireHSVPropertyChange(old);
	}

	public void setHSV(double h,double s,double v){
		h /=360;
		h %=1;
		if(h<0)h +=1;

		if(s<0)s =0;
		else if(s>1)s=1;

		if(v<0)v =0;
		else if(v>1)v=1;
		double[] old = {H,S,V};
		H = h;
		S = s;
		V = v;
		H = h;
		S = s;
		V = v;
		this.h.setHSV(H,S,V);
		sv.setHSV(H,S, V);
		fireHSVPropertyChange(old);
	}

	private static interface M{
		void dowork(MouseEvent e);
	}
	private final static M VOID =  new M(){public void dowork(MouseEvent e){}};

	MouseAdapter m = new MouseAdapter(){
		M
		haction = new M() {
			public void dowork(MouseEvent e) {
				int w = getWidth(),he=getHeight();
				int ww = h.getIconWidth(),hh = h.getIconHeight();
				int x = e.getX();
				int y = e.getY();
				double[] old = {H,S,V};
				H = h.getH(x-(w-ww>>1), y-(he-hh>>1));
				h.setHSV(H,S,V);
				sv.setHSV(H,S,V);
				repaint();
				fireHSVPropertyChange(old);
			}
		},
		svaction = new M(){
			public void dowork(MouseEvent e){
				int w = getWidth(),he=getHeight();
				int ww = sv.getIconWidth(),hh = sv.getIconHeight();
				int x = e.getX();
				int y = e.getY();
				double[] old = {H,S,V};
				sv.setSVformXY(x-(w-ww>>1), y-(he-hh>>1));
				S = sv.getS();
				V = sv.getV();
				h.setHSV(H,S, V);
				repaint();
				fireHSVPropertyChange(old);
			}
		},
		action = VOID;
		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();

			action = containH(p.x,p.y)? haction:
				containSV(p.x,p.y)? svaction:VOID;
			action.dowork(e);
		}

		public void mouseDragged(MouseEvent e) {
			action.dowork(e);
		}

		public void mouseReleased(MouseEvent e){
			action = VOID;
		}
	};

	public CercleHSV() {
		addComponentListener(lis);
		addMouseListener(m);
		addMouseMotionListener(m);
	}


	private int getSVSize(int r){
		double d = Math.sqrt(2*r*r);
		return (int) Math.ceil(d)-2;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200,200);
	}
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(140,140);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(h!=null){
			int w = getWidth(),he=getHeight();
			int ww = h.getIconWidth(),hh = h.getIconHeight();
			h.paintIcon(this, g, w-ww>>1, he-hh>>1);
			ww = sv.getIconWidth();
			hh = sv.getIconHeight();
			sv.paintIcon(this, g, w-ww>>1, he-hh>>1);
		}
	}

	private boolean containH(int x,int y){
		int w = getWidth(),he=getHeight();
		int ww = h.getIconWidth(),hh = h.getIconHeight();
		return h.contain(x-(w-ww>>1), y-(he-hh>>1));
	}

	private boolean containSV(int x,int y){
		int w = getWidth(),he=getHeight();
		int ww = sv.getIconWidth(),hh = sv.getIconHeight();
		Rectangle r = new Rectangle(w-ww>>1,he-hh>>1,ww,hh);
		return r.contains(x, y);
	}



	public static final String HSVPropertyChange ="HSV";
	private void fireHSVPropertyChange(double[] old){
		if(old[0] == H && old[1] == S&&old[2] == V)return;
		double[] hsv = {H,S,V};
		firePropertyChange(HSVPropertyChange, old, hsv);
	}

}
