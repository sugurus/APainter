package apainter;

import static apainter.GlobalKey.*;
import static apainter.misc.Util.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import apainter.annotation.Version;
import apainter.canvas.Canvas;
import apainter.canvas.cedt.cpu.CPUParallelWorkThread;
import apainter.command.Command;
import apainter.command.CommandCenter;
import apainter.command.CommandDecoder;
import apainter.command.ExistCommandNameException;
import apainter.command.NotFoundCommandException;
import apainter.debug.DebugMain;
import apainter.drawer.painttool.Eraser;
import apainter.drawer.painttool.Pen;
import apainter.gui.canvas.CanvasMouseListener;
import apainter.pen.PenFactoryCenter;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;


@Version("0.1.0")
/**
 * jre1.6以上で動作します。<br>
 * インスタンスの作成後、init関数を呼び出してください。<br>
 * 一度exitしたインスタンスは再利用できません。
 */
public class APainter {
	private static final Object
		DEBAGKEY = new Object();

	private final GlobalValue global;
	private final CommandCenter command;
	private final Device device;
	private final CanvasList canvaslist;
	private boolean inited=false;


	/**
	 * APainterを作成します。
	 * @param device 動作させる演算デバイス。現在CPUのみ。
	 */
	public APainter(Device device) {
		runtimeCheack();
		//GPU いつか………
		if(device==Device.GPU)throw new Error("can't use GPU");
		this.device = nullCheack(device,"device is null!");
		global = new GlobalValue();
		canvaslist = new CanvasList();
		command = new CommandCenter(global);

	}

	private void runtimeCheack(){
		String v = System.getProperty("java.version");
		Pattern p = Pattern.compile("\\d\\.(\\d)\\.\\d+_\\d+");
		Matcher  m = p.matcher(v);
		if(m.find()){
			int i = Integer.parseInt(m.group(1));
			if(i<6){
				throw new RuntimeException("jre version:"+v);
			}
		}
	}

	/**
	 * 初期化します。
	 */
	public synchronized void init(){
		if(inited)return;
		global.put(APainter, this);//これの為に初期化をコンストラクターから行えない。
		global.put(CommandCenter,command);
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
			global.put(CanvasActionList, list);
		}

		//TODO 設定の外部化
		global.put(NEWLayerDefaultName,"新規レイヤー");


		if(device==Device.CPU){
			initCPU();
		}else{
			initGPU();
		}

		inited=true;
		initCommand();
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
	 * @throws NotFoundCommandException コマンドが見つからなかった場合投げられます。
	 */
	public Command decodeCommand(String command)throws NotFoundCommandException{
		isInit();
		return this.command.decode(command);
	}

	/**
	 * 渡されたコマンドを実行します。
	 * @see apainter.APainter#decodeCommand(String)
	 * @param com 実行するコマンド
	 */
	public void exec(Command com){
		isInit();
		if(com!=null)com.exe(global);
	}

	/**
	 * コマンドを解析し、実行します。
	 * @param command コマンド文字列
	 * @return コマンドオブジェクト
	 * @throws NotFoundCommandException コマンドが見つからなかった場合投げられます。
	 */
	public Command exec(String command)throws NotFoundCommandException{
		isInit();
		Command c = decodeCommand(command);
		exec(c);
		return c;
	}


	public void debagON(){
		isInit();
		DebugMain d =global.get(DEBAGKEY, DebugMain.class);
		if(d==null){
			d= new DebugMain(global);
			global.put(DEBAGKEY, d);
		}
		d.debug(true);
	}

	public void debagOff(){
		isInit();
		DebugMain d =global.get(DEBAGKEY, DebugMain.class);
		if(d!=null){
			d.debug(false);
		}
	}


	public void exit(){
		if(!inited)return;
		if(!canExit())return;

		//threadの終了
		if(device==Device.CPU){
			stopThreadCPU();
		}else{
			stopThreadGPU();
		}

		for(Canvas c:canvaslist){
			c.shutDownCEDT();
		}
		debagOff();

		//thread処理終わり

		exitnow();

		//何かやらないといけないことあったらここに追加かな

		exited();
	}

	private void stopThreadCPU(){
		CPUParallelWorkThread.stop(this);
	}

	private void stopThreadGPU(){
		//GPU
	}

	private void isInit(){
		if(!inited)throw new RuntimeException("APainter don't init!");
	}




	//コマンド関連-------------------------------------------------


	private void initCommand(){
		isInit();
		addCommand(new Rotation());
		addCommand(new Zoom());
		addCommand(new Exit());
		addCommand(new CreateLayer());
		addCommand(new LayerLine());
		addCommand(new Commands());
		addCommand(new Selectedlayer());
	}

	/**
	 * コマンドを追加します。
	 * @param d コマンド解析器
	 * @throws ExistCommandNameException 既に同じコマンド名が存在している場合投げられます。
	 */
	public void addCommand(CommandDecoder d) throws ExistCommandNameException{
		command.addCommand(d);
	}


	private int canvasid=0;
	private Random random = new Random(System.currentTimeMillis());

	public synchronized CanvasHandler createNewCanvas(int width,int height){
		isInit();
		int id=(random.nextInt()&0xffff)<<16;
		id|=canvasid++;
		Canvas canvas= new Canvas(width, height, device, global,id);
		canvaslist.add(canvas);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);
		return new CanvasHandler(canvas,this);
	}

	public synchronized CanvasHandler createNewCanvas(int width,int height,GlobalValue globalvalue,
			String author,String canvasname,long makeDay,long workTime,long actionCount){
		isInit();
		int id=random.nextInt()&0xffff0000;
		id|=canvasid++;
		Canvas canvas= new Canvas(width, height, device, globalvalue,
				author, canvasname, makeDay, workTime, actionCount,id);
		canvaslist.add(canvas);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);
		return new CanvasHandler(canvas,this);
	}



	private EventListenerList exitlistenerlist = new EventListenerList();

	public void addExitListener(ExitListener l) {
		exitlistenerlist.remove(ExitListener.class, l);
		exitlistenerlist.add(ExitListener.class, l);
	}

	public void removeExitListener(ExitListener l) {
		exitlistenerlist.remove(ExitListener.class, l);
	}

	private boolean canExit() {

		Object[] listeners = exitlistenerlist.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ExitListener.class) {
				if(!((ExitListener) listeners[i + 1]).exiting(this)){
					return false;
				}
			}
		}
		return true;
	}
	private void exited() {
		Object[] listeners = exitlistenerlist.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ExitListener.class) {
				((ExitListener) listeners[i + 1]).exited(this);
			}
		}
	}

	private void exitnow() {
		Object[] listeners = exitlistenerlist.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ExitListener.class) {
				((ExitListener) listeners[i + 1]).exit(this);
			}
		}
	}

}
