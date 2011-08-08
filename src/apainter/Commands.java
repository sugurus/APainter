package apainter;

import static apainter.GlobalKey.*;
import static java.util.regex.Pattern.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apainter.canvas.Canvas;
import apainter.canvas.layerdata.LayerHandler;
import apainter.command.Command;
import apainter.command.CommandCenter;
import apainter.command.CommandDecoder;
import apainter.gui.canvas.CanvasView;

class FrontColor implements CommandDecoder {
	private static final String name = "frontcolor";
	private static final String help = "frontcolor [r,[g,b]]:::set front color.if only r defined,g and b values equal r value.if no option,show front color.";

	private static class Com extends Command {
		int r,g,b;
		Com(int r,int g,int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		@Override
		public String execution(GlobalValue global) {
			if(r!=-1){
				global.getFrontColor().setARGB(255, r, g, b);
			}
			global.commandPrintln(global.getFrontColor().toString());
			return global.getFrontColor().toString();
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
		int r,g,b;
		if (param.length == 0) {
			r = g=b=-1;
		} else if(param.length ==1) {
			try{
				r = Integer.parseInt(param[0]);
				g=b=r;
			}catch(NumberFormatException e){
				e.printStackTrace();
				return null;
			}
		}else if(param.length==3){
			try{
				r = Integer.parseInt(param[0]);
				g = Integer.parseInt(param[1]);
				b = Integer.parseInt(param[2]);
			}catch(NumberFormatException e){
				e.printStackTrace();
				return null;
			}
		}else return null;
		return new Com(r, g, b);
	}
}

class BackColor implements CommandDecoder {
	private static final String name = "backcolor";
	private static final String help = "backcolor [r,[g,b]]:::set back color.if only r defined,g and b values equal r value.if no option,show back color.";

	private static class Com extends Command {
		int r,g,b;
		Com(int r,int g,int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		@Override
		public String execution(GlobalValue global) {
			if(r!=-1){
				global.getBackColor().setARGB(255, r, g, b);
			}
			global.commandPrintln(global.getBackColor().toString());
			return global.getBackColor().toString();
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
		int r,g,b;
		if (param.length == 0) {
			r = g=b=-1;
		} else if(param.length ==1) {
			try{
				r = Integer.parseInt(param[0]);
				g=b=r;
			}catch(NumberFormatException e){
				e.printStackTrace();
				return null;
			}
		}else if(param.length==3){
			try{
				r = Integer.parseInt(param[0]);
				g = Integer.parseInt(param[1]);
				b = Integer.parseInt(param[2]);
			}catch(NumberFormatException e){
				e.printStackTrace();
				return null;
			}
		}else return null;
		return new Com(r, g, b);
	}
}
class CreateCanvas implements CommandDecoder {
	private static final String name = "createcanvas";
	private static final String help = "createcanvas width height [author,canvasname,makeDay,workTime,actionCount]:::call APainter#createCanvas.and return a CanvasHandler instance.";

	private static class Com extends Command {
		int p;
		int width;
		int height;
		String author;
		String canvasname;
		long makeDay;
		long workTime;
		long actionCount;
		Com(int psize,int width,int height,	String author,String canvasname,long makeDay,long workTime,long actionCount) {
			p = psize;
			this.width = width;
			this.height = height;
			this.author = author;
			this.canvasname = canvasname;
			this.makeDay = makeDay;
			this.workTime = workTime;
			this.actionCount = actionCount;
		}

		@Override
		public CanvasHandler execution(GlobalValue global) {
			CanvasHandler c;
			if(p==2){
				c= getTargetAPainter().createNewCanvas(width, height);
			}else{
				c= getTargetAPainter().createNewCanvas(width, height, author,
						canvasname, makeDay, workTime, actionCount);
			}
			global.commandPrint(c);
			return c;
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

		try{
			int psize = param.length;
			int width;
			int height;
			String author=null;
			String canvasname=null;
			long makeDay=0;
			long workTime=0;
			long actionCount=0;
			switch(param.length){
			case 7:
				author=param[2];
				canvasname=param[3];
				makeDay = Long.parseLong(param[4]);
				workTime= Long.parseLong(param[5]);
				actionCount = Long.parseLong(param[6]);
			case 2:
				width = Integer.parseInt(param[0]);
				height = Integer.parseInt(param[0]);
				return new Com(psize, width, height, author, canvasname, makeDay, workTime, actionCount);
			default:
				return null;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			return null;
		}


	}
}
class CreateLayer implements CommandDecoder{
	private static String name="createlayer";
	private static String help="createlayer [layername]:::create new layer.and return a LayerHandler instance.";

	private  static class Com extends Command{
		String s;
		Com(String name){
			s = name;
		}
		@Override
		public LayerHandler execution(GlobalValue global) {
			Canvas c=(Canvas) global.get(GlobalKey.CurrentCanvas);
			LayerHandler l= c.createNewLayer(s);
			global.commandPrintln(l);
			return l;
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
	private static final String help="selectlayer [layerid]:::show and return selectedlayer.if defined layerid, set it selected layer.";

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
			global.commandPrintln(l.toString());
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
	private static final String help ="layerline:::show and return layer line strnig.";

	private static class Com extends Command {

		@Override
		public String execution(GlobalValue global) {
			Canvas c = global.get(CurrentCanvas, Canvas.class);
			if(c!=null){
				String s=c.getLayerLine();
				global.commandPrintln(s);
				return s;
			}
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
	private static String help="rot [degree]:::set rotation of the selected canvas view.and return the rotation value(Double).";
	private static Pattern reg = compile("^(-?[0-9]+(\\.[0-9]*)?)");
	private static class Com extends Command{
		Double r;
		Com(Double rot){
			r = rot;
		}

		@Override
		public Double execution(GlobalValue global) {
			Canvas canvas =(Canvas)global.get(GlobalKey.CurrentCanvas);
			CanvasView cv=canvas.getCanvasView();
			if(r==null){
				global.commandPrintln(cv.getAngle().degree);
				return cv.getAngle().degree;
			}
			cv.setAngle(r);
			cv.rendering();
			return r;
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
			return new Com(null);
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
	private static final String help ="zoom [zoom]:::set zoom of the selected canvas view,and return the zoom value(Double).";
	private static Pattern reg = compile("^(-?[0-9]+(\\.[0-9]*)?)");
	private static class Com extends Command{
		Double r;
		Com(Double rot){
			r = rot;
		}
		@Override
		public Double execution(GlobalValue global) {
			Canvas canvas =(Canvas)global.get(GlobalKey.CurrentCanvas);
			CanvasView cv=canvas.getCanvasView();
			if(r==null){
				global.commandPrintln(cv.getZoom());
				return cv.getZoom();
			}
			cv.setZoom(r);
			cv.rendering();
			return r;
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
			return new  Com(null);
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
			global.commandPrintln(str);
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
