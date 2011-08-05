package apainter;

import static apainter.GlobalKey.*;
import static java.util.regex.Pattern.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apainter.canvas.Canvas;
import apainter.canvas.layerdata.LayerHandler;
import apainter.command.Command;
import apainter.command.CommandCenter;
import apainter.command.CommandDecoder;
import apainter.gui.canvas.CanvasView;

class CreateLayer implements CommandDecoder{
	private static String name="createlayer";
	private static String help="createlayer [layername]:::create new layer";

	private  static class Com extends Command{
		String s;
		Com(String name){
			s = name;
		}
		@Override
		public Void execution(GlobalValue global) {
			Canvas c=(Canvas) global.get(GlobalKey.CurrentCanvas);
			c.createNewLayer(s);
			return null;
		}
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String help() {
		return help;
	}

	@Override
	public Command decode(String[] param) {
		String s;
		if(param.length==0)s=null;
		else s = param[0];
		return new Com(s);
	}
}

class Selectedlayer implements CommandDecoder {
	private static final String name = "selectlayer";
	private static final String help="selectlayer [layerid]:::show selectedlayer.and set selected layer.";

	private static class Com extends Command {
		int i;
		public Com(int t) {
			i = t;
		}

		@Override
		public LayerHandler execution(GlobalValue global) {
			Canvas c=(Canvas) global.get(GlobalKey.CurrentCanvas);
			if(i!=-1){
				c.setSelectedLayer(i);
			}

			LayerHandler l = c.getSelectedLayer();
			System.out.println(l.toString());
			return l;
		}
	}

	@Override
	public String help() {
		return help;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public Command decode(String[] param) {
		String s;
		if(param.length!=0){
			s=param[0];
		}else s="-1";
		int i;
		try{
			i = Integer.parseInt(s);
		}catch(NumberFormatException e){
			i=-1;
		}
		return new Com(i);
	}
}

class LayerLine implements CommandDecoder {
	private static final String name = "layerline";
	private static final String help ="layerline:::show layer line.";

	private static class Com extends Command {

		@Override
		public Void execution(GlobalValue global) {
			Canvas c = global.get(CurrentCanvas, Canvas.class);
			if(c!=null)	System.out.println(c.getLayerLine());
			return null;
		}
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String help() {
		return help;
	}

	@Override
	public Command decode(String[] param) {
		return new Com();
	}
}

class Rotation implements CommandDecoder{
	private static String name = "rot";
	private static String help="rot [degree]:::set rotation of the selected canvas view.if degree undefined,set rotation 0.";
	private static Pattern reg = compile("^(-?[0-9]+(\\.[0-9]*)?)");
	private static class Com extends Command{
		double r;
		Com(double rot){
			r = rot;
		}

		@Override
		public Void execution(GlobalValue global) {
			Canvas canvas =(Canvas)global.get(GlobalKey.CurrentCanvas);
			CanvasView cv=canvas.getCanvasView();
			cv.setAngle(r);
			cv.rendering();
			return null;
		}
	}

	@Override
	public String help() {
		return help;
	}

	@Override
	public String getCommandName() {
		return name;
	}
	@Override
	public Command decode(String[] params) {
		String param;
		if(params.length==0){
			param="0";
		}else{
			param = params[0];
		}
		Matcher m = reg.matcher(param);
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
	private static String name="zoom";
	private static final String help ="zoom [zoom]:::set zoom of the selected canvas view.if zoom undefined,set zoom 1.";
	private static Pattern reg = compile("^(-?[0-9]+(\\.[0-9]*)?)");
	private static class Com extends Command{
		double r;
		Com(double rot){
			r = rot;
		}
		@Override
		public Void execution(GlobalValue global) {
			//TODO
			Canvas canvas =(Canvas)global.get(GlobalKey.CurrentCanvas);
			CanvasView cv=canvas.getCanvasView();
			cv.setZoom(r);
			cv.rendering();
			return null;
		}
	}

	@Override
	public String help() {
		return help;
	}

	@Override
	public String getCommandName() {
		return name;
	}
	@Override
	public Command decode(String[] params) {
		String param;
		if(params.length==0){
			param="1";
		}else{
			param = params[0];
		}
		Matcher m = reg.matcher(param);
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

class Exit implements CommandDecoder{
	private final String name="exit";
	private final String help ="exit:::exit apainter";
	private final Command com = new Command() {
		public Void execution(GlobalValue global) {
			getTargetAPainter().exit();
			return null;
		}
	};

	@Override
	public String help() {
		return help;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public Command decode(String[] param) {
		return com;
	}
}

class Commands implements CommandDecoder {
	private static final String name = "help";
	private static final String help="help:::show all commands";
	private static final Comparator<CommandDecoder> c = new Comparator<CommandDecoder>() {

		@Override
		public int compare(CommandDecoder o1, CommandDecoder o2) {
			String s1 = o1.getCommandName(),s2=o2.getCommandName();
			return s1.compareTo(s2);
		}
	};
	private static class Com extends Command {

		@Override
		public Object execution(GlobalValue global) {
			CommandDecoder[] d=global.get(CommandCenter,CommandCenter.class).getAllDecoder();
			Arrays.sort(d, c);
			StringBuilder str = new StringBuilder();
			for(CommandDecoder c:d){
				String name = c.getCommandName();
				String[] help = c.help().split(":::",2);
				str.append(name).append(":\t").append(help[0]);
				if(help.length==2){
					str.append("\n  ").append(help[1]);
				}
				str.append("\n");
			}
			System.out.println(str.toString());
			return null;
		}
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String help() {
		return help;
	}

	@Override
	public Command decode(String[] param) {

		return new Com();
	}
}
