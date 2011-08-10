package nodamushi.pentablet;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.LinkedList;

import nodamushi.pentablet.PenTabletMouseEvent.ButtonType;
import nodamushi.pentablet.PenTabletMouseEvent.CursorDevice;
import nodamushi.pentablet.PenTabletMouseEvent.State;

import jpen.PButton;
import jpen.PButton.Type;
import jpen.PButtonEvent;
import jpen.PKind;
import jpen.PKindEvent;
import jpen.PLevel;
import jpen.PLevelEvent;
import jpen.PScroll;
import jpen.PScrollEvent;
import jpen.Pen;
import jpen.PenDevice;
import jpen.PenManager;
import jpen.event.PenListener;


/**
 * コンポーネントにタブレットの情報を受け取れるようにする為のリスナークラスです。<br>
 * リスナーはこのクラスを拡張してください。<br><br>
 * TabletLintenerはjpen2をラップするクラスになります。jpen2がインストールされていない環境では通常のマウスリスナーとして振る舞います
 * 。<br>
 * ユーザーはjpen2の存在や使い方と言った事を意識する必要はありません。<br>
 * なお、TabletListenerと通常のマウスイベントリスナーとの共存は動作を保証しません。<br>
 * コンストラクターは、イベントを受け取るコンポーネントを登録する為に、TabletListnerのコンストラクターを
 * 呼び出してください。<br>
 * @see PenTabletMouseEvent
 * @author nodamushi
 * @version 1.1.0
 */
public abstract class PenTabletRecognizer{

	/**
	 * PenTabletRecognizerのバージョン情報　"x_y_z"
	 */
	public static final String Version = "1_1_0";


	/**
	 *
	 * @param target イベントを受け取るコンポーネント
	 * @throws IllegalArgumentException targetがnullの時
	 */
	public PenTabletRecognizer(Component target) throws IllegalArgumentException{
		if(target == null)throw new IllegalArgumentException("target component is null!");
		this.target = target;
		ujpen = enablejpen?true:searchJPen();
		if(ujpen){
			listener=new JPenRecognizer(this, target);
		}else{
			listener=new MouseRecognizer(this);
		}
	}

	/**
	 * このインスタンスが通常のマウスリスナーとして動作している場合、JPenのリスナーとして動くようにします。<br>
	 * searchJPenによりjpen2の利用可能状態が利用不可から可に変化したときに呼び出してください。
	 * @return このインスタンスがJPenが利用可能になったかどうか。
	 */
	public final synchronized boolean reInstall(){
		if(enablejpen && !ujpen){
			removeListener();
			listener = new JPenRecognizer(this, target);
			ujpen = true;
			setListener();
		}
		return ujpen;
	}


	/**
	 * このインスタンスがJPenを利用しているかどうかを返します
	 * @return このインスタンスがJPenを利用しているかどうか
	 */
	public final boolean isUseJPen(){
		return ujpen;
	}

	/**
	 * ターゲットコンポーネントからイベントを受け取らないようにします
	 */
	public final void removeListener(){
		listener.remove();
	}

	/**
	 * コンストラクターで指定したターゲットコンポーネントからイベントを受け取れるようにします。<br>
	 * removeListenerで受け取らなくした後、再び受け取りたい場合は呼び出してください
	 */
	public final void setListener(){
		listener.set();
	}

	/**
	 * タブレットが有効なときenterイベントおよび、exitイベントはイベントが起こるスレッドが異なります。<br>
	 * そのため、この二つは他のpressイベントなどと同時に起こる可能性があります。<br>
	 * それを同期するかどうかを設定します。<br>
	 * デフォルトではfalseになっています。<br><br>タブレットが使えないときは意味はありません。
	 * @param b 同期するかどうか。
	 */
	public final void setSyncronized(boolean b){
		listener.setSyncronized(b);
	}

	/**
	 * タブレットが有効なときenterイベントおよび、exitイベントはイベントが起こるスレッドが異なります。<br>
	 * そのため、この二つは他のpressイベントなどと同時に起こる可能性があります。<br>
	 * それを同期するかどうかを設定をかえします。<br><br>タブレットが使えないときは、常にtrueが返ります。
	 * @return
	 */
	public final boolean isSyncronized(){
		return listener.isSyncronized();
	}

	/**
	 * JPen2がきちんと動作しているか確認をします。<br>
	 * ただし、ここで0が返ってきた場合でもタブレットをまだ動作させていないだけの可能性があります。<br>
	 * タブレットを動かした後も0が返る場合は読みこまれてないかも
	 * @return 0:ネイティブライブラリが読み込まれていない可能性があります<br>
	 * 			1:利用可能です。<br>
	 * 			-1:jpen2が使えません。
	 */
	public final int isjpenloadnative(){
		return listener.canusetablet();
	}

	////////////////////////////////////////////////////////メンバ
	/**
	 * リスナーの対象となるコンポーネント
	 */
	protected final Component target;
	private Listener listener;
	private boolean ujpen;

	////////////////////////////////////////////////////////static
	static private ButtonType finaltablettype= ButtonType.HEAD;
	static private final int[] JPenVersion = {2};
	static void setFinaltype(ButtonType b){finaltablettype = b;}
	static public ButtonType getFinalTabletType(){return finaltablettype;}

	static private boolean enablejpen;
	static{
		searchJPen();
	}

	/**
	 * JPenが使えるかどうか調べます。<br>
	 * JPenが使えない状態から使える状態に変化しても、これまでに作成したPenTabletRecognizerのインスタンスはペンタブレットのイベントを受け取りません。<br>
	 * これまでに生成したインスタンスも受け取れるようにするにはreInstallメソッドを呼び出してください。
	 * @return
	 */
	static public boolean searchJPen(){
		boolean b = false;
		try {
			Class.forName("jpen.PenManager");
			int v = Integer.parseInt(PenManager.getJPenFullVersion().split("-")[0]) ;
			for(int jv:JPenVersion){
				if(jv==v){
					b= true;
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			b =false;
		}
		enablejpen = b;
		return b;
	}

	public static boolean canUseJPen() {
		return enablejpen;
	}

	public static String getJPenFullVersion(){
		if(enablejpen)return PenManager.getJPenFullVersion();
		else return "";
	}

	/**
	 * このPenTabletRecognizerが対応しているJPenのバージョンを返します
	 * @return
	 */
	public static int[] getCompatibleJPenVersion(){
		return JPenVersion.clone();
	}


	//////////////////////////継承用の関数///////////////////////////////////////

	/**
	 * ドラッグが起こると呼び出される関数です。
	 *
	 * @param e
	 *            マウスイベント
	 */
	public abstract void onDragged(PenTabletMouseEvent e);

	/**
	 * ボタンが押されていない、もしくは筆圧が無い状態でマウスの移動が起こると呼び出される関数です。
	 *
	 * @param e
	 */
	public abstract void onMove(PenTabletMouseEvent e);

	/**
	 * ボタンが押された、筆圧を感知した時に呼び出される関数です。
	 *
	 * @param e
	 */
	public abstract void onPressed(PenTabletMouseEvent e);

	/**
	 * ボタンが離された、筆圧がなくなった時に呼び出される関数です。
	 *
	 * @param e
	 */
	public abstract void onReleased(PenTabletMouseEvent e);

	/**
	 * スクロールされた時に呼び出される関数です。
	 *
	 * @param e
	 *            スクロールイベント
	 */
	public abstract void onScroll(MouseWheelEvent e);

	/**
	 * ペン先から消しゴムやマウスとかにカーソルを操作するものが変わったときに呼び出される。
	 *
	 * @param e
	 */
	public abstract void operatorChanged(PenTabletMouseEvent e);

	/**
	 * マウスが領域に入ったときに起こるイベントです。
	 *
	 * @param e
	 */
	public abstract void onEnter(PenTabletMouseEvent e);

	/**
	 * マウスが領域から出たときに起こるイベントです。
	 *
	 * @param e
	 */
	public abstract void onExit(PenTabletMouseEvent e);

}
interface Listener{
	void set();
	void remove();
	boolean isSyncronized();
	void setSyncronized(boolean b);
	int canusetablet();
}

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
//PenListener
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
final class JPenRecognizer implements PenListener,Listener{

	private static int modify=0;

	private static final KeyEventDispatcher dispatch  = new KeyEventDispatcher(){
		private int t = InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK|InputEvent.META_DOWN_MASK|InputEvent.CTRL_DOWN_MASK;
		public boolean dispatchKeyEvent(KeyEvent e) {
			modify =e.getModifiers()&t;
			return false;
		}
	};
	static{
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatch);
	}

	/**
	 * JPen2がきちんと動作しているか確認をします。<br>
	 * ただし、ここで0が返ってきた場合でもタブレットをまだ動作させていないだけの可能性があります。<br>
	 * タブレットを動かした後も0が返る場合は読みこまれてないかも
	 * @return 0:ネイティブライブラリが読み込まれていない可能性があります<br>
	 * 			1:利用可能です。<br>
	 */
	public int canusetablet(){
		Collection<PenDevice> c = pm.getDevices();
		if(c.size()==2)return 0;
		for(PenDevice p:c){
			if(!pm.isSystemMouseDevice(p)){
				if(p.getProvider().getConstructor().getNativeVersion() != -1)return 1;
			}
		}
		return 0;
	}

	private final PenTabletRecognizer t;
	private final Component c;
	private final PenManager pm;
	private boolean set;
	private boolean release = true;
	private final LinkedList<ButtonType> mousetype=new LinkedList<ButtonType>();
	private long waittime=0;
	private long time;
	private boolean ansync = true;
	private MouseAdapter enter_exitListener = new MouseAdapter() {

		@Override
		public void mouseEntered(MouseEvent e) {
			PenTabletMouseEvent ae = PenTabletMouseEvent.wrapEvent(e);
			dump(ae);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			PenTabletMouseEvent ae = PenTabletMouseEvent.wrapEvent(e);
			dump(ae);
		}
	};


	public void setSyncronized(boolean b){ansync = !b;}
	public boolean isSyncronized(){return !ansync;}

	private void dump(PenTabletMouseEvent e){
		if(ansync) {
			switch (e.getState()) {
			case DRAGGED:
				t.onDragged(e);
				break;
			case MOVE:
				t.onMove(e);
				break;
			case PRESSED:
				t.onPressed(e);
				break;
			case RELEASED:
				t.onReleased(e);
				break;
			case ENTERED:
				t.onEnter(e);
				break;
			case EXITED:
				t.onExit(e);
				break;
			}
		}else synchronized(this){//同期設定
			switch (e.getState()) {
			case DRAGGED:
				t.onDragged(e);
				break;
			case MOVE:
				t.onMove(e);
				break;
			case PRESSED:
				t.onPressed(e);
				break;
			case RELEASED:
				t.onReleased(e);
				break;
			case ENTERED:
				t.onEnter(e);
				break;
			case EXITED:
				t.onExit(e);
				break;
			}
		}
	}

	void dump(MouseWheelEvent e){
		if(ansync)t.onScroll(e);
		else synchronized (this) {
			t.onScroll(e);
		}
	}


	JPenRecognizer(PenTabletRecognizer obj,Component target){
		t = obj;
		c=target;
		pm = new PenManager(target);
		set();
		mousetype.push(ButtonType.NULL);
	}

	public void remove(){
		if(set){
			c.removeMouseListener(enter_exitListener);
			pm.pen.removeListener(this);
			set=false;
		}
	}
	public void set(){
		if(!set){
			c.addMouseListener(enter_exitListener);
			pm.pen.addListener(this);
			set=true;
		}
	}

	final public void penButtonEvent(PButtonEvent e) {
		Pen pen = e.pen;
		float x,y,p,r,tx,ty;
		PKind k = pen.getKind();
		boolean b =  pen.hasPressedButtons();
		ButtonType btype;
		CursorDevice ctype;
		State state = State.NULL;
		switch(k.getType()){
		case ERASER:
			btype = ButtonType.TAIL;
			ctype = CursorDevice.TABLET;
			break;
		case STYLUS:
			btype =ButtonType.HEAD;
			ctype = CursorDevice.TABLET;
			break;
		case CUSTOM:
			return;
		case CURSOR:
			state = State.PRESSED;
			ctype = CursorDevice.MOUSE;
			release = false;
			if(e.button.getType() == Type.CENTER)btype = ButtonType.BUTTON2;
			else if(e.button.getType() == Type.RIGHT)btype=ButtonType.BUTTON3;
			else if(e.button.getType() == Type.LEFT)btype = ButtonType.BUTTON1;
			else btype = ButtonType.NULL;
			if(b){
				if(!e.button.value){
					state = State.RELEASED;
					mousetype.remove(btype);
				}else{
					mousetype.push(btype);
				}
			}
			else {
				mousetype.clear();
				mousetype.add(ButtonType.NULL);
			}
			break;
		default:
			state = State.PRESSED;
			release = false;
			ctype = CursorDevice.MOUSE;
			btype = ButtonType.NULL;
		}
		time=0;
		if(!b&&!release){
			state = State.RELEASED;
			memberinit();
		}

		x = pen.getLevelValue(PLevel.Type.X);//x取得
		y = pen.getLevelValue(PLevel.Type.Y);//y取得
		p = pen.getLevelValue(PLevel.Type.PRESSURE);//筆圧取得
		r = pen.getLevelValue(PLevel.Type.ROTATION);//rotation
		tx= pen.getLevelValue(PLevel.Type.TILT_X);//x傾き
		ty= pen.getLevelValue(PLevel.Type.TILT_Y);//y傾き
		PenTabletMouseEvent ev= PenTabletMouseEvent.createEvent(c, e.getTime(), modify, x, y, pen.getPressedButtonsCount(), btype,
				ctype, state, p, r, tx, ty);
		dump(ev);
	}


	public void penKindEvent(PKindEvent e) {
		Pen pen = e.pen;
		float x,y,r,tx,ty;

		PKind k = pen.getKind();
		ButtonType btype;
		CursorDevice ctype;
		State state = State.CURSORTYPECHANGE;
		switch(k.getType()){
		case ERASER:
			btype = ButtonType.TAIL;
			PenTabletRecognizer.setFinaltype(ButtonType.TAIL);
			ctype = CursorDevice.TABLET;
			break;
		case STYLUS:
			btype =ButtonType.HEAD;
			PenTabletRecognizer.setFinaltype(ButtonType.HEAD);
			ctype = CursorDevice.TABLET;
			break;
		case CUSTOM:
			return;
		default:
			ctype = CursorDevice.MOUSE;
			PenTabletRecognizer.setFinaltype(ButtonType.HEAD);
			btype = ButtonType.NULL;
		}

		x = pen.getLevelValue(PLevel.Type.X);//x取得
		y = pen.getLevelValue(PLevel.Type.Y);//y取得
		r = pen.getLevelValue(PLevel.Type.ROTATION);//rotation
		tx= pen.getLevelValue(PLevel.Type.TILT_X);//x傾き
		ty= pen.getLevelValue(PLevel.Type.TILT_Y);//y傾き
		PenTabletMouseEvent ev= PenTabletMouseEvent.createEvent(c, e.getTime(), modify, x, y, pen.getPressedButtonsCount(), btype,
				ctype, state, 0, r, tx, ty);
		t.operatorChanged(ev);
	}

	public void penLevelEvent(PLevelEvent e) {
		if(waittime > time)return;
		else time -=waittime;
		Pen pen = e.pen;
		float x,y,p,r,tx,ty;

		x = pen.getLevelValue(PLevel.Type.X);//x取得
		y = pen.getLevelValue(PLevel.Type.Y);//y取得
		p = pen.getLevelValue(PLevel.Type.PRESSURE);//筆圧取得
		r = pen.getLevelValue(PLevel.Type.ROTATION);//rotation
		tx= pen.getLevelValue(PLevel.Type.TILT_X);//x傾き
		ty= pen.getLevelValue(PLevel.Type.TILT_Y);//y傾き

		PKind k = pen.getKind();
		boolean b=pen.hasPressedButtons();
		ButtonType btype;
		CursorDevice ctype;
		State state;
		if(b){
			if(release){
				state = State.PRESSED;
			}else state = State.DRAGGED;
		}else {
			state = State.MOVE;

		}
		switch(k.getType()){
		case ERASER:
			if(b){
				release = false;
			}
			btype =ButtonType.TAIL;
			ctype = CursorDevice.TABLET;
			break;
		case STYLUS:
			if(b){
				release = false;
			}
			btype =ButtonType.HEAD;
			ctype = CursorDevice.TABLET;
			break;
		case CUSTOM:
			return;
		case CURSOR:
			ctype = CursorDevice.MOUSE;
			btype = mousetype.getFirst();
			break;
		default:
			ctype = CursorDevice.TABLET;
			ctype = CursorDevice.MOUSE;
			btype = ButtonType.NULL;
		}



		PenTabletMouseEvent ev= PenTabletMouseEvent.createEvent(c, e.getTime(), modify, x, y, pen.getPressedButtonsCount(), btype,
				ctype, state, p, r, tx, ty);
		dump(ev);
	}


	public void penScrollEvent(PScrollEvent e) {
		int val = e.scroll.value;
		if(val==0)return;
		Pen pen = e.pen;
		int rotation = 1;
		if(e.scroll.getType()==PScroll.Type.UP){
			rotation=-1;
		}

		int x = (int) pen.getLevelValue(PLevel.Type.X);//x取得
		int y = (int) pen.getLevelValue(PLevel.Type.Y);//y取得
		dump(new MouseWheelEvent(c, MouseEvent.MOUSE_WHEEL, e.getTime(), modify,
				x, y, pen.getPressedButtonsCount(), false,
				0, val, rotation));

	}

	public void penTock(long n) {time+=n;}

	private void memberinit(){
		release = true;
		time =0;
	}
}







//------------------------------------------------
//ただのリスナー
//------------------------------------------------
final class MouseRecognizer  implements MouseListener,MouseMotionListener,MouseWheelListener,Listener{
	MouseRecognizer(PenTabletRecognizer t){this.t=t;set();}

	public void mousePressed(MouseEvent e) {
		PenTabletRecognizer.setFinaltype(ButtonType.HEAD);
		t.onPressed(PenTabletMouseEvent.wrapEvent(e));
	}


	public void mouseReleased(MouseEvent e) {
		PenTabletRecognizer.setFinaltype(ButtonType.HEAD);
		t.onReleased(PenTabletMouseEvent.wrapEvent(e));
	}

	public void mouseMoved(MouseEvent e) {
		PenTabletRecognizer.setFinaltype(ButtonType.HEAD);
		t.onMove(PenTabletMouseEvent.wrapEvent(e));
	}

	public void mouseDragged(MouseEvent e) {
		PenTabletRecognizer.setFinaltype(ButtonType.HEAD);
		t.onDragged(PenTabletMouseEvent.wrapEvent(e));
	}

	public void mouseWheelMoved(MouseWheelEvent e) {t.onScroll(e);}
	public void mouseEntered(MouseEvent e) {t.onEnter(PenTabletMouseEvent.wrapEvent(e));}
	public void mouseExited(MouseEvent e) {t.onExit(PenTabletMouseEvent.wrapEvent(e));}
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void remove() {
		t.target.removeMouseListener(this);
		t.target.removeMouseMotionListener(this);
		t.target.removeMouseWheelListener(this);
	}

	@Override
	public void set() {
		t.target.addMouseListener(this);
		t.target.addMouseMotionListener(this);
		t.target.addMouseWheelListener(this);
	}

	@Override
	public boolean isSyncronized() {
		return true;
	}

	@Override
	public int canusetablet() {
		return -1;
	}

	@Override
	public void setSyncronized(boolean b) {}

	final PenTabletRecognizer t;
}
