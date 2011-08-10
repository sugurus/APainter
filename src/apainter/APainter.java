package apainter;

import static apainter.GlobalKey.*;
import static apainter.misc.Util.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import apainter.annotation.Version;
import apainter.canvas.Canvas;
import apainter.canvas.CanvasHandler;
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


/**
 * jre1.6以上で動作します。<br>
 * APainterインスタンスはcreateAPainterメソッドを用いて作成します。<br>
 * 一度exitしたインスタンスは再利用できません。
 */
@Version("0.1.0")
public class APainter {


	private static AtomicInteger apainterid = new AtomicInteger();
	/**
	 * 新しいAPainterを起動します。
	 * @param device
	 * @return
	 */
	public static APainter createAPainter(Device device){
		runtimeCheack();
		APainter ap = new APainter(device,"");
		ap.init();
		return ap;
	}

	/**
	 * 新しいAPainterを起動します。
	 * @param device
	 * @param property APainterのプロパティー
	 * @return
	 */
	public static APainter createAPainter(Device device,String property){
		runtimeCheack();
		APainter ap = new APainter(device,property);
		ap.init();
		return ap;
	}

	private static void runtimeCheack(){
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

	//end static
	//---------------------------------------------------------------------------

	private static final Object
		DEBAGKEY = new Object();

	private final GlobalValue global;
	private final CommandCenter command;
	private final Device device;
	private final CanvasList canvaslist;
	private final Properties properties;
	private final int id;


	private APainter(Device device,String property) {
		//GPU いつか………
		if(device==Device.GPU)throw new Error("can't use GPU");
		this.device = nullCheack(device,"device is null!");
		properties = Properties.decode(property);
		global = new GlobalValue(properties);
		canvaslist = new CanvasList();
		command = new CommandCenter(global);
		id = apainterid.incrementAndGet();
	}


	private void init(){
		global.setAPainter(this);
		global.put(OnDevice, device);
		global.put(CommandCenter,command);
		global.put(CanvasList,canvaslist);
		global.put(Property, properties);
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


		if(device==Device.CPU){
			initCPU();
		}else{
			initGPU();
		}

		initCommand();
	}


	private void initCPU(){
		CPUParallelWorkThread.use(this);
	}

	private void initGPU(){
		//GPU
	}


	public int getID(){
		return id;
	}

	public void bind(GlobalBindKey bkey,Object obj){
		global.getBind(bkey).bind(obj);
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
		return this.command.decode(command);
	}

	/**
	 * 渡されたコマンドを実行します。
	 * @see apainter.APainter#decodeCommand(String)
	 * @param com 実行するコマンド
	 */
	public Object exec(Command com){
		if(com!=null)return com.exe(global);
		return null;
	}

	/**
	 * コマンドを解析し、実行します。
	 * @param command コマンド文字列
	 * @return 実行結果
	 * @throws NotFoundCommandException コマンドが見つからなかった場合投げられます。
	 */
	public Object exec(String command)throws NotFoundCommandException{
		Command c = decodeCommand(command);
		return exec(c);
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


	private void stopThreadCPU(){
		CPUParallelWorkThread.stop(this);
	}

	private void stopThreadGPU(){
		//GPU
	}





	//コマンド関連-------------------------------------------------
	//コマンドの追加が面倒くさいので開発時はJavaFileManagerで
	//実際にコンパイルするときはfalseに設定。
	//trueの時addcommandlistコマンドが追加される。
	//addCommand(new .....の文字列が表示される。
	private final boolean develop=true;

	private void initCommand(){
		addCommand(new Commands());

		if(!develop){
			addCommand(new _BackColor());
			addCommand(new _CreateCanvas());
			addCommand(new _CreateLayer());
			addCommand(new _Exit());
			addCommand(new _FrontColor());
			addCommand(new _LayerLine());
			addCommand(new _PenColorMode());
			addCommand(new _Rotation());
			addCommand(new _Selectedlayer());
			addCommand(new _Zoom());
		}else{

			StringBuffer sb = new StringBuffer();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			JavaFileManager fm = compiler.getStandardFileManager(
					new DiagnosticCollector<JavaFileObject>(), null, null);

			Set<JavaFileObject.Kind> kind = new HashSet<JavaFileObject.Kind>();
			kind.add(JavaFileObject.Kind.CLASS);
			try {
				for(JavaFileObject f:fm.list(StandardLocation.CLASS_PATH, "apainter", kind, false)){
					String name = f.getName();
					if(name.startsWith("_") && name.endsWith("class") && !name.contains("$")){
						String clname = name.substring(0,name.length()-6);
						try {
							Class<?> claz = Class.forName("apainter."+clname);
							if(claz!=null && CommandDecoder.class.isAssignableFrom(claz)){
								CommandDecoder cd = (CommandDecoder) claz.newInstance();
								addCommand(cd);
								sb.append("addCommand(new ").append(clname).append("());\n");
							}
						} catch (ClassNotFoundException e) {
						} catch (InstantiationException e) {
						} catch (IllegalAccessException e) {
						}
					}
				}
			} catch (IOException e) {}
			final String str = sb.toString();
			addCommand(new CommandDecoder() {

				@Override
				public String help() {
					return "addcommandlist:::make addCommands list";
				}

				@Override
				public String getCommandName() {
					return "addcommandlist";
				}

				@Override
				public Command decode(String[] params) {
					return new Command() {
						@Override
						protected Object execution(GlobalValue global) {
							global.commandPrint(str);
							return str;
						}
					};
				}
			});
		}

	}

	/**
	 * コマンドを追加します。
	 * @param d コマンド解析器
	 * @throws ExistCommandNameException 既に同じコマンド名が存在している場合投げられます。
	 */
	public void addCommand(CommandDecoder d) throws ExistCommandNameException{
		command.addCommand(d);
	}

	public void setCommandPrintStream(PrintStream ps){
		global.put(CommandPrintStream, ps);
	}

	public void setCommandErrorPrintStream(PrintStream ps){
		global.put(CommandErrorPrintStream,ps);
	}

	/**
	 * コマンドを実行する際に、文字列を出力するかどうかのフラグ。<br>
	 * falseに設定すると、コマンドを実行しても文字列は出力されません。
	 * @param b
	 */
	public void setCommandPrintFlag(boolean b){
		global.setCommandPrintFlag(b);
	}


	private int canvasid=0;
	private Random random = new Random(System.currentTimeMillis());

	public synchronized CanvasHandler createNewCanvas(int width,int height){
		int id=(random.nextInt()&0xffff)<<16;
		id|=canvasid++;
		Canvas canvas= new Canvas(width, height, device, global,id,this);
		canvaslist.add(canvas);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);
		return canvas.getCanvasHandler();
	}

	public synchronized CanvasHandler createNewCanvas(int width,int height,
			String author,String canvasname,long makeDay,long workTime,long actionCount){
		int id=random.nextInt()&0xffff0000;
		id|=canvasid++;
		Canvas canvas= new Canvas(width, height, device, global,
				author, canvasname, makeDay, workTime, actionCount,id,this);
		canvaslist.add(canvas);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);
		return canvas.getCanvasHandler();
	}




	//------EventListener---------------------------------------------------------

	private EventListenerList exitlistenerlist = new EventListenerList();

	public void addExitListener(ExitListener l) {
		exitlistenerlist.remove(ExitListener.class, l);
		exitlistenerlist.add(ExitListener.class, l);
	}

	public void removeExitListener(ExitListener l) {
		exitlistenerlist.remove(ExitListener.class, l);
	}

	//----exit-----------------------------------------------------------
	public void exit(){
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
