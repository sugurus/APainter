//import javax.swing.JApplet;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

import apainter.CanvasHandler;
import apainter.Device;
import apainter.ExitListener;
import apainter.annotation.Version;
import apainter.canvas.Canvas;


@Version("0.1.0")
public class APainter implements ExitListener
//extends JApplet
{
	static private APainter instance=null;


	static public JComponent getContentPane(){
		if(instance.isApplet){
			//TODO applet
			return null;
		}else return (JComponent) instance.frame.getContentPane();
	}
	public static synchronized void main(final String[] args) {
		if(instance!=null)return;
		instance = new APainter();
		instance.isApplet = false;
		instance.frame = new JFrame("APainter");
		instance.init(args);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				instance.frame.setVisible(true);
			}
		});
	}

	public static URL getURL(String path){
		if(instance.isApplet){
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
		core = apainter.APainter.createAPainter(Device.CPU);
		core.addExitListener(this);
		CanvasHandler canvas = core.createNewCanvas(401, 401);
		frame.add(canvas.getComponent());
		frame.pack();
		frame.setDefaultCloseOperation(3);
		core.debagON();

	}

	public void init() {
		if(instance==null){
			instance = this;
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


	@Override
	public boolean exiting(apainter.APainter apainter) {
		return true;
	}

	@Override
	public void exit(apainter.APainter apainter) {

	}

	@Override
	public void exited(apainter.APainter apainter) {
		System.exit(0);
	}

}
