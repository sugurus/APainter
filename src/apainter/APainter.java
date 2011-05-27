package apainter;

//import javax.swing.JApplet;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import apainter.gui.splash.Splash;

public class APainter
//extends JApplet
{
	static private APainter apainter=null;
	static public JComponent getContentPane(){
		if(apainter.isApplet){
			//TODO
			return null;
		}else return (JComponent) apainter.frame.getContentPane();
	}
	public static synchronized void main(final String[] args) {
		if(apainter!=null)return;
		apainter = new APainter();
		apainter.isApplet = true;
		apainter.frame = new JFrame("APainter");
		final Splash sw = new Splash();
		sw.showSplashWindow();
		apainter.init(args);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				sw.closeSplashWindow();
				apainter.frame.setVisible(true);
			}
		});
	}

	public static URL getURL(String path){
		if(apainter.isApplet){
			//TODO applet
			return null;
		}else{
			File f = new File(path).getAbsoluteFile();
			try {
				return f.toURI().toURL();
			} catch (MalformedURLException e) {
				return null;
			}
		}
	}

	private boolean isApplet;
	private JFrame frame;


	public void init() {
		apainter = this;
		//TODO for JApplet
	}

	public void init(String[] args){
		//TODO
		frame.setDefaultCloseOperation(3);

	}


	public void start(){
		//TODO for JApplet
	}


	public void stop(){
		//TODO for JApplet
	}

	public void pack(){
		if(!isApplet)frame.pack();
	}

	public void setTitle(String title){
		if(!isApplet)frame.setTitle(title);
	}

}
