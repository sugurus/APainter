package apainter.gui.splash;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.UIManager;

import apainter.APainter;
public class Splash{
	public static String SplashWindowImageURLKey ="SplashWindowImageURLKey";
	public static URL defaultImage = Splash.class.getResource("splashImage.png");


	private JDialog window;
	private Image img;
	private SplashPanel iip;


	@SuppressWarnings("restriction")
	public Splash() {
		window = new JDialog();
		window.setUndecorated(true);
		JComponent c = (JComponent) window.getContentPane();
		String s =UIManager.getString(SplashWindowImageURLKey);
		com.sun.awt.AWTUtilities.setWindowOpaque(window, false);
		URL u;
		if(s==null)u = defaultImage;
		else{
			u =APainter.getURL(s);
			if(u==null)u = defaultImage;
		}
		img = Toolkit.getDefaultToolkit().createImage(u);
		iip = new SplashPanel(img);
		c.add(iip);

	}

	public SplashPanel getSplashPanel(){
		return iip;
	}

	public void setText(String s){
		iip.setText(s);
	}
	public void setTextBounds(Rectangle r){
		iip.setTextBounds(r);
	}


	public synchronized void showSplashWindow(){
		if(EventQueue.isDispatchThread()){
			_show();
		}else{
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					_show();
				}
			});

		}
	}

	private void _show(){
		window.setSize(iip.getPreferredSize());
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public synchronized void closeSplashWindow(){
		if(EventQueue.isDispatchThread()){
			_close();
		}else{
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					_close();
				}
			});
		}
	}

	private void _close(){
		window.setVisible(false);
	}


}
