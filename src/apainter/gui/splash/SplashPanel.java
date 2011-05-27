package apainter.gui.splash;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import static apainter.Util.*;
public class SplashPanel extends JComponent{

	private Image im;
	private ImageObserver ob;
	private Rectangle r;
	private String text;

	public SplashPanel(Image img){
		this(img,null);
	}
	public SplashPanel(Image img,ImageObserver observer) {
		if(img == null )throw new NullPointerException("img");
		im = img;
		if(observer==null)ob = this;
		else ob = observer;
	}

	public void setText(String s){
		text = s;
		repaint();
	}

	public void setTextBounds(Rectangle r){
		this.r = r;
	}

	@Override
	public Dimension getPreferredSize() {
		int w = im.getWidth(ob),h = im.getHeight(ob);
		return new Dimension(w,h);
	}

	private Rectangle r1=new Rectangle(),r2=new Rectangle();
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(im, 0, 0, ob);
		if(text!=null&&r!=null){
			FontMetrics f = getFontMetrics(getFont());
			String s = SwingUtilities.layoutCompoundLabel
				(this, f, text, null,
				0, 0,
				SwingConstants.LEFT, 2,
				r,r1,r2, 0);
			g.drawString(s, r.x, r.y+10);
		}
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,int h) {
		if(bitFlagsOr(infoflags, ALLBITS,FRAMEBITS)){
			repaint();
		}else if(bitFlagsOr(infoflags, WIDTH,HEIGHT)){
			try{
				Container c = getParent().getParent().getParent().getParent();

				if(c instanceof JDialog){
					JDialog d = (JDialog)c;
					d.pack();
					d.setLocationRelativeTo(null);
				}else
					revalidate();
			}catch (NullPointerException e) {
			}
		}
		return  true;
	}

}
