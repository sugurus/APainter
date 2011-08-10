package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import apainter.bind.annotation.BindProperty;
import static apainter.PropertyChangeNames.*;
public class ColorChoose extends JFrame{


	JPanel front = new ColorPanel(Color.black),back = new ColorPanel(Color.white);
	public ColorChoose() {
		add(front,BorderLayout.NORTH);
		add(back,BorderLayout.SOUTH);
		pack();
	}

	public class ColorPanel extends JPanel{
		public ColorPanel(Color c) {
			super.setBackground(c);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Color c =JColorChooser.showDialog(ColorPanel.this, "色選択", ColorPanel.this.getBackground());
							setBackground(c);
						}
					});
				}
			});
			setPreferredSize(new Dimension(100, 100));
			setBorder(BorderFactory.createLineBorder(Color.black));
		}

		@BindProperty(ColorPropertyChange)
		public void setBackground(int bg){
			Color c = new Color(bg);
			this.setBackground(c);
		}

		@Override
		public void setBackground(Color bg) {
			if(bg==null)return;
			Color c = getBackground();
			if(bg.equals(c))return;
			super.setBackground(bg);
			if(c==null)return;
			firePropertyChange(ColorPropertyChange, c.getRGB(), bg.getRGB());
		}

	}

}
