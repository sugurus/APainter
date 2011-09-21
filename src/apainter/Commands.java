package apainter;

import static apainter.GlobalKey.*;
import static java.util.regex.Pattern.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import apainter.canvas.Canvas;
import apainter.canvas.CanvasHandler;
import apainter.canvas.layerdata.InnerLayerHandler;
import apainter.canvas.layerdata.LayerHandler;
import apainter.command.Command;
import apainter.command.CommandCenter;
import apainter.command.CommandDecoder;
import apainter.drawer.Drawer;
import apainter.drawer.painttool.Eraser;
import apainter.drawer.painttool.Pen;
import apainter.gui.CanvasMouseListener;
import apainter.gui.CanvasView;
import apainter.misc.Utility_PixelFunction;
import apainter.pen.PenFactoryCenter;
import apainter.pen.PenShape;
import apainter.rendering.ColorMode;

//名前の付け方は必ず最初に_を付けること。
class _GetDrawer implements CommandDecoder {
	private static final String name = "getdrawer";
	private static final String help = ":::";

	private static class Com extends Command {
		int i;

		Com(int i) {
			this.i = i;
		}

		@Override
		public Object execution(GlobalValue global) {
			@SuppressWarnings("unchecked")
			ArrayList<CanvasMouseListener> l = (ArrayList<CanvasMouseListener>) global.get(CanvasActionList);

			for(CanvasMouseListener c:l){
				if(c instanceof Drawer && ((Drawer) c).getID()==i){
					return c;
				}
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

		if (param.length == 0) {
			return null;
		} else {
			try{
				int i= Integer.parseInt(param[0]);
				return new Com(i);
			}catch(NumberFormatException e){
				return null;
			}
		}

	}
}


class _Debag_MinPenDensity implements CommandDecoder {
	private static final String name = "minpendens";
	private static final String help = "minpendens drawerid percent:::筆圧によるペンの濃度の変化の最小比率を設定します.drawerid=>0:ペン、1:消しゴム。0 <= percent <= 100.this is a debag command";

	private static class Com extends Command {
		int p;
		int id;

		Com(int s,int i) {
			p = s;
			id = i;
			if(p < 0) p = 0;
			else if(p > 100)p = 100;
		}

		@Override
		public Object execution(GlobalValue global) {
			@SuppressWarnings("unchecked")
			ArrayList<CanvasMouseListener> l = (ArrayList<CanvasMouseListener>) global.get(CanvasActionList);

			for(CanvasMouseListener c:l){
				if(c instanceof Drawer && ((Drawer) c).getID() == id){
					((Drawer)c).setMinDensity(p/100d);
				}
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

		if (param.length == 0) {
			return null;
		} else {
			try{
				int s = Integer.parseInt(param[1]);
				int i = Integer.parseInt(param[0]);
				return new Com(s,i);
			}catch (NumberFormatException e) {
				return null;
			}

		}

	}
}


class _Debag_MinPenSize implements CommandDecoder {
	private static final String name = "minpensize";
	private static final String help = "minpensize drawerid percent:::筆圧によるペンのサイズの変化の最小比率を設定します.drawerid=>0:ペン、1:消しゴム。0 <= percent <= 100.this is a debag command";

	private static class Com extends Command {
		int p;
		int id;

		Com(int s,int i) {
			p = s;
			id = i;
			if(p < 0) p = 0;
			else if(p > 100)p = 100;
		}

		@Override
		public Object execution(GlobalValue global) {
			@SuppressWarnings("unchecked")
			ArrayList<CanvasMouseListener> l = (ArrayList<CanvasMouseListener>) global.get(CanvasActionList);

			for(CanvasMouseListener c:l){
				if(c instanceof Drawer && ((Drawer) c).getID() == id){
					((Drawer)c).setMinSize(p/100d);
				}
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

		if (param.length != 2) {
			return null;
		} else {
			try{
				int s = Integer.parseInt(param[1]);
				int i = Integer.parseInt(param[0]);
				return new Com(s,i);
			}catch (NumberFormatException e) {
				return null;
			}

		}

	}
}

class _GetPenFactory implements CommandDecoder {
	private static final String name = "getpenf";
	private static final String help = ":::";

	private static class Com extends Command {
		int id;

		Com(int i) {
			id = i;
		}

		@Override
		public Object execution(GlobalValue global) {
			return global.get(PenFactoryCenter,PenFactoryCenter.class).getPenShapeFactory(id);
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

		if (param.length >= 1) {
			try{
				int i = Integer.parseInt(param[0]);
				return new Com(i);
			}catch (NumberFormatException e) {
				return null;
			}
		}
		return null;

	}
}

class _Debag_SetPenSize implements CommandDecoder {
	private static final String name = "pensize";
	private static final String help = "pensize drawerid size:::set pen size.this is a debug command.";

	private static class Com extends Command {
		int size;
		int id;

		Com(int s,int i) {
			size = s;
			id = i;
		}

		@Override
		public Object execution(GlobalValue global) {
			@SuppressWarnings("unchecked")
			ArrayList<CanvasMouseListener> l = (ArrayList<CanvasMouseListener>) global.get(CanvasActionList);

			for(CanvasMouseListener c:l){
				if(c instanceof Drawer && ((Drawer) c).getID()==id){
					PenShape p=global.get(PenFactoryCenter,PenFactoryCenter.class).getPenShapeFactory(0).createPenShape(size, Device.CPU);
					((Drawer) c).setPen(p);
				}
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

		if (param.length != 2) {
			return null;
		} else {
			try{
				int i = Integer.parseInt(param[0]);
				int s = Integer.parseInt(param[1]);
				return new Com(s*10,i);
			}catch (NumberFormatException e) {
				return null;
			}

		}

	}
}

class _Debag_SaveCanvasImage implements CommandDecoder {
	private static final String name = "saveci";
	private static final String help = "saveci filepath filetype:::save the current canvas image as filetype.this is a debug command.filetype -> jpg,jpeg,gif,png,png24,bmp. png24 is a png file that does't have alpha channels.";

	private static class Com extends Command {
		File path;
		String type;
		Com(String[] parameta) {
			path = new File(parameta[0]);
			type = parameta[1].toLowerCase();
		}

		@Override
		public Object execution(GlobalValue global) {
			Canvas c = global.get(CurrentCanvas, Canvas.class);
			if(c!=null){
				BufferedImage b = c.createSaveImage();
				if("jpeg".equals(type)||"jpg".equals(type)||"png24".equals(type)||"bmp".equals(type)){
					int w,h;
					BufferedImage bb = new BufferedImage(w=b.getWidth(), h=b.getHeight(), BufferedImage.TYPE_INT_RGB);
					Graphics2D g = bb.createGraphics();
					g.setColor(Color.white);
					g.fillRect(0, 0, w, h);
					g.drawImage(b,0,0,w,h,null);
					g.dispose();
					b = bb;
				}
				String type = this.type;
				if("png24".equals(type)){
					type = "png";
				}
				try {
					ImageIO.write(b, type, path);
				} catch (IOException e) {
					e.printStackTrace(global.getCommandPrintStream());
				}
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

		if (param.length != 2) {
			return null;
		} else {
			return new Com(param);
		}

	}
}

class _CreateCanvasImage implements CommandDecoder {
	private static final String name = "canvasi";
	private static final String help = "canvasi [canvasid]:::render the canvas which id is canvasid,and return a BufferedImage.if canvasid does't be defined,the rendered canvas is the current canvas." +
			"type of the BufferedImage is type_int_argb.if you will save this image as a jpeg file,you must convert the image to a image which type doesn't have alpha values.";

	private static class Com extends Command {
		Integer id;

		Com(Integer id) {
			this.id = id;
		}

		@Override
		public Object execution(GlobalValue global) {
			Canvas c=null;
			if(id==null){
				c =global.get(CurrentCanvas, Canvas.class);
			}else{
				CanvasList list = global.get(CanvasList,CanvasList.class);
				for(Canvas ca:list){
					if(ca.getID() == id){
						c = ca;
					}
				}
				if(c==null){
					c = global.get(CurrentCanvas, Canvas.class);
				}
			}
			if(c!=null){
				return c.createSaveImage();
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

		if (param.length == 0) {
			return new Com(null);
		} else {
			try{
				return new Com(Integer.parseInt(param[0]));
			}catch(NumberFormatException e){
				return new Com(null);
			}
		}

	}
}
class _LayerColorMode implements CommandDecoder {
	private static final String name = "layercolormode";
	private static final String help = "layercolormode [modename [layerid]]:::set a color mode of a layer that the current canvas contains.when undefined layerid,target layer is selected layer.";

	private static class Com extends Command {
		ColorMode mode;
		int id;

		Com(ColorMode m,int i) {
			mode = m;
			id = i;
		}

		@Override
		public ColorMode execution(GlobalValue global) {
			if(mode==null){
				ColorMode cm= getCurrentCanvas(global).getSelectedLayer().getRenderingMode();
				global.commandPrintln(cm);
				return cm;
			}
			InnerLayerHandler lh;
			if(id>=0){
				lh = getCurrentCanvas(global).getLayer(id);
			}else{
				lh = getSelectedLayer(global);
			}
			lh.setRenderingMode(mode);
			Canvas c = getCurrentCanvas(global);
			c.rendering(new Rectangle(c.getWidth(), c.getHeight()));
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
		if (param.length == 0) {
			return new Com(null, -1);
		} else{

			int i=-1;
			if(param.length>=2){
				try{
					i = Integer.parseInt(param[1]);
				}catch(NumberFormatException e){}
			}
			ColorMode m = ColorMode.getColorMode(param[0]);
			if(m==null){
				System.err.println("mode name isn't correct! "+param[0]);
				return null;
			}
			return  new Com(m, i);
		}

	}
}

class _PenColorMode implements CommandDecoder {
	private static final String name = "pencolormode";
	private static final String help = "pencolormode [modename]:::set a color mode of pen.(default,add,overlay...,see ColorMode.txt).if set no option,show pen color mode.";

	private static class Com extends Command {
		String parameta;

		Com(String parameta) {
			this.parameta = parameta;
		}

		@Override
		public ColorMode execution(GlobalValue global) {
			Pen p=null;
			ArrayList<?> a = global.get(CanvasActionList,ArrayList.class);
			if(a==null)return null;
			for(Object o:a){
				if (o instanceof Pen) {
					p = (Pen) o;
				}
			}
			if(p==null)return null;

			if(parameta==null){

				ColorMode m=p.getColorMode();
				global.commandPrintln(m);
				return m;
			}
			p.setColorMode(parameta);
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

		if (param.length == 0) {
			return new Com(null);
		} else {
			return new Com(param[0]);
		}
	}
}

class _FrontColor implements CommandDecoder {
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

class _BackColor implements CommandDecoder {
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
class _CreateCanvas implements CommandDecoder {
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
class _CreateLayer implements CommandDecoder{
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
			InnerLayerHandler l= c.createNewLayer(s);
			global.commandPrintln(l);
			return l.getLayerHandler();
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

class _Selectedlayer implements CommandDecoder {
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

			InnerLayerHandler l = c.getSelectedLayer();
			global.commandPrintln(l.toString());
			return l.getLayerHandler();
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

class _LayerLine implements CommandDecoder {
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

class _Rotation implements CommandDecoder{
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

class _Zoom implements CommandDecoder{
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

class _Exit implements CommandDecoder{
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
				str.append("\n\n");
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
