package apainter;

import static apainter.GlobalKey.CanvasActionList;
import static apainter.GlobalKey.CanvasHeadAction;
import static apainter.GlobalKey.CanvasList;
import static apainter.GlobalKey.CanvasTailAction;
import static apainter.GlobalKey.CommandCenter;
import static apainter.GlobalKey.CommandErrorPrintStream;
import static apainter.GlobalKey.CommandPrintStream;
import static apainter.GlobalKey.OnDevice;
import static apainter.GlobalKey.PenFactoryCenter;
import static apainter.GlobalKey.Property;
import static apainter.misc.Util.nullCheack;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import apainter.annotation.Version;
import apainter.bind.BindObject;
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
 * jre1.6_10以上で動作します。<br>
 * APainterインスタンスはcreateAPainterメソッドを用いて作成します。<br>
 * 一度exitしたインスタンスは再利用できません。
 */
@Version("0.1.0")
public class APainter {


	private static final int maxSize = 2000;
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
			Pen pen = new Pen(global,0);
			Eraser era = new Eraser(global,1);
			PenShapeFactory f = p.getPenShapeFactory(0);
			try {
				f.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//FIXME
			pen.setPen(f.createPenShape(10,Device.CPU));
			era.setPen(f.createPenShape(10,Device.CPU));
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

	public void bind(BindKey bkey,BindObject obj){
		global.bind(bkey,obj);
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
	//コマンドの追加が面倒くさいので開発時はファイルシステムで
	//実際にコンパイルするときはfalseに設定。
	//trueの時addcommandlistコマンドが追加される。
	//addCommand(new .....の文字列が表示される。
	public static final boolean develop=true;

	private void initCommand(){
		addCommand(new Commands());

		if(!develop){
			addCommand(new _BackColor());
			addCommand(new _CreateCanvas());
			addCommand(new _CreateLayer());
			addCommand(new _Debag_FillLayer());
			addCommand(new _Exit());
			addCommand(new _FrontColor());
			addCommand(new _LayerColorMode());
			addCommand(new _LayerLine());
			addCommand(new _PenColorMode());
			addCommand(new _Rotation());
			addCommand(new _Selectedlayer());
			addCommand(new _Zoom());
		}else{

			StringBuffer sb = new StringBuffer();

			try {
				URI uri = APainter.class.getResource("Apainter.class").toURI();
				File apainter = new File(uri);
				File dir = apainter.getParentFile();
				File[] fs = dir.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						String name = pathname.getName();
						return name.startsWith("_") && name.endsWith("class") && !name.contains("$");
					}
				});

				for(File f:fs){
					String name = f.getName();
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


			} catch (Exception e1) {
				System.err.println("APainter.java のdevelopフラグをfalseにして下さい。");
			}

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
		if(width>maxSize || height > maxSize)throw new RuntimeException("size over");

		int id=(random.nextInt()&0xffff)<<16;
		id|=canvasid++;
		Canvas canvas= new Canvas(width, height, device, global,id,this);
		canvaslist.add(canvas);
		global.addCanvas(canvas);
		global.put(GlobalKey.CurrentCanvas, canvas);
		return canvas.getCanvasHandler();
	}

	public static int getMaxCanvasSize(){
		return maxSize;
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
