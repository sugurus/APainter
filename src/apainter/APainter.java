package apainter;

import static apainter.GlobalKey.*;
import static apainter.misc.Util.*;

import java.util.ArrayList;

import apainter.annotation.Version;
import apainter.canvas.Canvas;
import apainter.canvas.cedt.cpu.CPUParallelWorkThread;
import apainter.command.Command;
import apainter.command.CommandCenter;
import apainter.command.CommandDecoder;
import apainter.debug.DebugMain;
import apainter.drawer.painttool.Eraser;
import apainter.drawer.painttool.Pen;
import apainter.gui.canvas.CanvasMouseListener;
import apainter.pen.PenFactoryCenter;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;


@Version("0.1.0")
public class APainter {
	private static final Object
		DEBAGKEY = new Object();

	private GlobalValue global;
	private CommandCenter command;
	private Device device;
	private ArrayList<Canvas> canvaslist;



	public APainter(Device device) {
		//GPU いつか………
		if(device==Device.GPU)throw new Error("can't use GPU");
		this.device = nullCheack(device,"device is null!");
		global = new GlobalValue();
		canvaslist = new ArrayList<Canvas>();
		global.put(CanvasList,canvaslist);
		{
			PenFactoryCenter p = new PenFactoryCenter();
			global.put(PenFactoryCenter, p);
			Pen pen = new Pen(global);
			Eraser era = new Eraser(global);
			PenShapeFactory f = p.getPenShapeFactory(0);
			f.load();
			PenShape s = f.createPenShape(1, 1);
			pen.setPen(s);
			era.setPen(s);
			global.put(CanvasHeadAction, pen);
			global.put(CanvasTailAction,era);
			ArrayList<CanvasMouseListener> list = new  ArrayList<CanvasMouseListener>();
			list.add(pen);
			list.add(era);
			global.put(CanvasList, list);
		}
		command = new CommandCenter(global);
		global.put(CommandCenter,command);
		initCommand();

		//TODO 設定の外部化
		global.put(NEWLayerDefaultName,"新規レイヤー");


		if(device==Device.CPU){
			initCPU();
		}else{
			initGPU();
		}
	}


	private void initCPU(){
		CPUParallelWorkThread.use(this);
	}

	private void initGPU(){
		//GPU
	}

	/**
	 * 文字列を解析し、コマンドオブジェクトを発行します。実行はしません。<br>
	 * 実行するにはexecに返ってきたオブジェクトを渡してください。
	 * @see apainter.APainter#exec(Command)
	 * @param command コマンド文字列
	 * @return コマンドオブジェクト
	 */
	public Command decodeCommand(String command){
		return this.command.decode(command);
	}

	/**
	 * 渡されたコマンドを実行します。
	 * @see apainter.APainter#decodeCommand(String)
	 * @param com 実行するコマンド
	 */
	public void exec(Command com){
		if(com!=null)com.exec(global);
	}

	/**
	 * コマンドを解析し、実行します。
	 * @param command コマンド文字列
	 * @return コマンドオブジェクト
	 */
	public Command exec(String command){
		Command c = decodeCommand(command);
		exec(c);
		return c;
	}


	public void debagON(){
		DebugMain d =global.get(DEBAGKEY, DebugMain.class);
		if(d==null){
			d= new DebugMain(global);
			global.put(DEBAGKEY, d);
		}
		d.debug(true);
	}

	public void debagOff(){
		DebugMain d =global.get(DEBAGKEY, DebugMain.class);
		if(d!=null){
			d.debug(false);
		}
	}


	public void exit(){
		if(device==Device.CPU){
			exitCPU();
		}else{
			exitGPU();
		}

		for(Canvas c:canvaslist){
			c.shutDownCEDT();
		}

		System.out.println("APainter exit");
		//FIXME exit処理 今はとりあえず全部止めちゃってる。
		System.exit(0);
	}

	private void exitCPU(){
		CPUParallelWorkThread.stop(this);
	}

	private void exitGPU(){
		//GPU
	}




	//コマンド関連-------------------------------------------------


	private void initCommand(){
		command.addCommand(new Rotation());
		command.addCommand(new Zoom());
		command.addCommand(new Exit());
	}



	public CanvasHandler createNewCanvas(int width,int height){
		Canvas canvas= new Canvas(width, height, device, global);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);
		return new CanvasHandler(canvas,this);
	}

	public CanvasHandler createNewCanvas(int width,int height,GlobalValue globalvalue,
			String author,String canvasname,long makeDay,long workTime,long actionCount){
		Canvas canvas= new Canvas(width, height, device, globalvalue, author, canvasname, makeDay, workTime, actionCount);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);
		return new CanvasHandler(canvas,this);
	}


	private class Exit implements CommandDecoder{
		private final String name="exit";
		private final Command com = new Command() {
			public void exec(GlobalValue global) {
				exit();
			}
		};

		@Override
		public boolean isMatch(String commandname) {
			return name.equals(commandname);
		}

		@Override
		public Command decode(String param) {
			return com;
		}
	}
}
