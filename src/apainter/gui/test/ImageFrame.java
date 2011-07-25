package apainter.gui.test;

import java.awt.Image;

import javax.swing.JFrame;

public class ImageFrame extends JFrame{

	public ImageFrame(Image img,String title) {
		super(title);
		ImageView v = new ImageView(img);
		add(v);
		pack();
	}

}
