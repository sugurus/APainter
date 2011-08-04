//import javax.swing.JApplet;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import apainter.CanvasHandler;
import apainter.Device;
import apainter.annotation.Version;
import apainter.canvas.Canvas;


@Version("0.1.0")
public class APainter
//extends JApplet
{
	static private APainter apainter=null;
	static private Thread initThread;

	static public boolean isInitThread(Thread t){
		return initThread==null?false:initThread == t;
	}

	static public JComponent getContentPane(){
		if(apainter.isApplet){
			//TODO applet
			return null;
		}else return (JComponent) apainter.frame.getContentPane();
	}
	public static synchronized void main(final String[] args) {
		if(apainter!=null)return;
		initThread = Thread.currentThread();
		apainter = new APainter();
		apainter.isApplet = false;
		apainter.frame = new JFrame("APainter");
		apainter.init(args);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
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

	private apainter.APainter core;


	public void init(String[] args){
		//TODO init
		core = new apainter.APainter(Device.CPU);
		CanvasHandler canvas = core.createNewCanvas(401, 401);
		frame.add(canvas.getComponent());
		frame.pack();
		frame.setDefaultCloseOperation(3);
		core.debagON();

	}

	public void init() {
		if(apainter==null){
			apainter = this;
			initThread = Thread.currentThread();
		//TODO for JApplet
		}
	}

	public void start(){
		//TODO for JApplet
	}


	public void stop(){
		//TODO for JApplet
	}

	public void destroy(){
		//TODO for JApplet
	}

	public void pack(){
		if(!isApplet)frame.pack();
	}

	public void setTitle(String title){
		if(!isApplet)frame.setTitle(title);
	}

}
