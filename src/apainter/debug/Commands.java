package apainter.debug;

import static java.util.regex.Pattern.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.canvas.Canvas;
import apainter.gui.canvas.CanvasView;

class Exit implements CommandDecoder{

	private static Pattern reg = compile("^\\s*exit\\s*$");
	private static Command com = new Command() {
		public void exec(GlobalValue global) {
			System.out.println("APainter exit");
			System.exit(0);
		}
	};

	@Override
	public Command decode(String command) {
		Matcher m = reg.matcher(command);
		if(!m.find())return null;
		return com;
	}
}


class Rotation implements CommandDecoder{
	private static Pattern reg = compile("^rot +(-?[0-9]+(\\.[0-9]*)?)");
	private static class Com implements Command{
		double r;
		Com(double rot){
			r = rot;
		}
		@Override
		public void exec(GlobalValue global) {
			//TODO
			Canvas canvas =(Canvas)global.get(GlobalKey.CurrentCanvas);
			CanvasView cv=canvas.getCanvasView();
			cv.setAngle(r);
			cv.rendering();
		}
	}
	@Override
	public Command decode(String command) {
		Matcher m = reg.matcher(command);
		if(!m.find())return null;
		String s=m.group(1);
		Command com=null;
		try{
			double t=Double.parseDouble(s);
			com = new Com(t);
		}catch(Exception e){}
		return com;
	}
}

class Zoom implements CommandDecoder{
	private static Pattern reg = compile("^zoom +(-?[0-9]+(\\.[0-9]*)?)");
	private static class Com implements Command{
		double r;
		Com(double rot){
			r = rot;
		}
		@Override
		public void exec(GlobalValue global) {
			//TODO
			Canvas canvas =(Canvas)global.get(GlobalKey.CurrentCanvas);
			CanvasView cv=canvas.getCanvasView();
			cv.setZoom(r);
			cv.rendering();
		}
	}
	@Override
	public Command decode(String command) {
		Matcher m = reg.matcher(command);
		if(!m.find())return null;
		String s=m.group(1);
		Command com=null;
		try{
			double t=Double.parseDouble(s);
			com = new Com(t);
		}catch(Exception e){}
		return com;
	}
}
