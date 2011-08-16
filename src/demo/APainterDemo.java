package demo;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import apainter.APainter;
import apainter.Device;
import apainter.ExitListener;
import apainter.GlobalBindKey;
import apainter.canvas.CanvasHandler;

/**
 * ひとまずのGUI
 * @author nodamushi
 */
public class APainterDemo{

	public static void main(final String[] argv) {
		final APainterDemo apainter = new APainterDemo(argv);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				apainter.frame.setVisible(true);
			}
		});
	}


	private JFrame frame;
	private ColorChoose cchose=new ColorChoose();

	private APainter core;

	public APainterDemo(String[] argv) {
		frame = new JFrame("APainter");
		core = APainter.createAPainter(Device.CPU);
		core.addExitListener(new EL());
		CanvasHandler canvas = core.createNewCanvas(401, 401);
		frame.add(canvas.getComponent());
		frame.pack();
		frame.setDefaultCloseOperation(3);
		//core.debagON();
		core.bind(GlobalBindKey.FrontColorBIND, cchose.front);
		cchose.setVisible(true);
	}

	private static class EL implements ExitListener{
		@Override
		public boolean exiting(APainter apainter) {
			return true;
		}

		@Override
		public void exit(APainter apainter) {

		}

		@Override
		public void exited(APainter apainter) {
			System.exit(0);
		}
	}

}
