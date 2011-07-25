package apainter.gui.test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

/**
 * とりあえず、画像を表示するどぉ～
 * @author nodamushi
 *
 */
public class ImageView extends JComponent{

	private Image img;

	public ImageView(Image img) {
		this.img = img;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(img.getWidth(this),img.getHeight(this));
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(img,0,0, this);
	}


	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
			int h) {
		repaint();
		return true;
	}

}
