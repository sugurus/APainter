import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import apainter.Device;
import apainter.ExitListener;
import apainter.canvas.CanvasHandler;

/**
 * ひとまずのGUI
 * @author nodamushi
 */
public class APainter{

	public static void main(final String[] argv) {
		final APainter apainter = new APainter(argv);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				apainter.frame.setVisible(true);
			}
		});
	}


	private JFrame frame;

	private apainter.APainter core;

	public APainter(String[] argv) {
		frame = new JFrame("APainter");
		core = apainter.APainter.createAPainter(Device.CPU);
		core.addExitListener(new EL());
		CanvasHandler canvas = core.createNewCanvas(401, 401);
		frame.add(canvas.getComponent());
		frame.pack();
		frame.setDefaultCloseOperation(3);
		core.debagON();
	}

	private static class EL implements ExitListener{
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

}
