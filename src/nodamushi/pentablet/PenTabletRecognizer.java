package nodamushi.pentablet;
import static java.awt.event.InputEvent.*;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;

import javax.swing.SwingUtilities;

import jpen.PButton.Type;
import jpen.PButton.TypeGroup;
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
import jpen.owner.multiAwt.AwtPenToolkit;
import nodamushi.pentablet.PenTabletMouseEvent.CursorDevice;
import nodamushi.pentablet.PenTabletMouseEvent.State;

/**
 * コンポーネントにタブレットの情報を受け取れるようにする為のリスナークラスです。<br>
 * リスナーはこのクラスを拡張してください。<br><br>
 * TabletLintenerはjpen2をラップするクラスになります。jpen2がインストールされていない環境では通常のマウスリスナーとして振る舞います
 * 。<br>
 * ユーザーはjpen2の存在や使い方と言った事を意識する必要はありません。<br>
 * なお、TabletListenerと通常のマウスイベントリスナーとの共存は動作を保証しません。<br>
 * @see PenTabletMouseEvent
 * @author nodamushi
 * @version 1.2.2
 */
public abstract class PenTabletRecognizer implements MouseWheelListener{

	/**
	 * PenTabletRecognizerのバージョン情報　"x_y_z"
	 */
	public static final String Version = "1_2_2";


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

	public final void setWaitTime(long time){
		listener.setWaitTime(time);
	}

	/**
	 * ターゲットコンポーネントからイベントを受け取らないようにします
	 */
	public final void removeListener(){
		listener.remove();
	}

	/**
	 * 破棄し、使用不可能にします。
	 */
	public final void dispose(){
		listener.dispose();
		_dispose();
	}

	/**
	 * disposeメソッドが呼ばれたときに、
	 * 何らかの処理をしたい場合はこのメソッドをオーバーライドして下さい。
	 */
	protected void _dispose(){}

	/**
	 * コンストラクターで指定したターゲットコンポーネントからイベントを受け取れるようにします。<br>
	 * removeListenerで受け取らなくした後、再び受け取りたい場合は呼び出してください
	 */
	public final void setListener(){
		listener.set();
	}


	/**
	 * JPen2がきちんと動作しているか確認をします。<br>
	 * ただし、ここで0が返ってきた場合でもタブレットをまだ動作させていないだけの可能性があります。<br>
	 * タブレットを動かした後も0が返る場合は読みこまれてないかも
	 * @return 0:ネイティブライブラリが読み込まれていない可能性があります<br>
	 * 1:利用可能です。<br>
	 * -1:jpen2が使えません。
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
	static private final int[] JPenVersion = {2};
	static private final int[] SubVersion={110623};

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
		TRY:try {
			Class.forName("jpen.PenManager");
			String[] s = PenManager.getJPenFullVersion().split("-");
			int[] v = {Integer.parseInt(s[0]),Integer.parseInt(s[1])};
			int i=0;
			for(int jv:JPenVersion){
				if(jv==v[0]){
					b= true;
					break;
				}
				i++;
			}
			if(!b){
				break TRY;
			}
			b= SubVersion[i]==v[1];
		} catch (ClassNotFoundException e) {
			b =false;
		}
		enablejpen = b;
		return b;
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
	 * マウスイベント
	 */
	public abstract void mouseDragged(PenTabletMouseEvent e);

	/**
	 * ボタンが押されていない、もしくは筆圧が無い状態でマウスの移動が起こると呼び出される関数です。
	 *
	 * @param e
	 */
	public abstract void mouseMoved(PenTabletMouseEvent e);

	/**
	 * ボタンが押された、筆圧を感知した時に呼び出される関数です。
	 *
	 * @param e
	 */
	public abstract void mousePressed(PenTabletMouseEvent e);

	/**
	 * ボタンが離された、筆圧がなくなった時に呼び出される関数です。
	 *
	 * @param e
	 */
	public abstract void mouseReleased(PenTabletMouseEvent e);


	/**
	 * ペン先から消しゴムやマウスとかにカーソルを操作するものが変わったときに呼び出される。
	 *
	 * @param e
	 */
	public abstract void mouseOperatorChanged(PenTabletMouseEvent e);

	/**
	 * マウスが領域に入ったときに起こるイベントです。
	 *
	 * @param e
	 */
	public abstract void mouseEntered(PenTabletMouseEvent e);

	/**
	 * マウスが領域から出たときに起こるイベントです。
	 *
	 * @param e
	 */
	public abstract void mouseExited(PenTabletMouseEvent e);

}
interface Listener{
	void set();
	void remove();
	void dispose();
	int canusetablet();
	void setWaitTime(long t);
}

/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
//PenListener
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
final class JPenRecognizer implements PenListener,Listener{

	private static int keymodify=0;


	/**
	 * JPen2がきちんと動作しているか確認をします。<br>
	 * ただし、ここで0が返ってきた場合でもタブレットをまだ動作させていないだけの可能性があります。<br>
	 * タブレットを動かした後も0が返る場合は読みこまれてないかも
	 * @return 0:ネイティブライブラリが読み込まれていない可能性があります<br>
	 * 1:利用可能です。<br>
	 */
	public int canusetablet(){
		PenManager pm = AwtPenToolkit.getPenManager();
		Collection<PenDevice> c = pm.getDevices();
		if(c.size()==2)return 0;
		for(PenDevice p:c){
			if(!pm.isSystemMouseDevice(p)){
				if(p.getProvider().getConstructor().getNativeVersion() != -1)return 1;
			}
		}
		return 0;
	}

	private PenTabletRecognizer t;
	private Component c;
	private boolean set;
	private boolean isPress = true;
	private long waittime=0;
	private long time;
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

	@Override
	public void setWaitTime(long t) {
		if(t<0)t=0;
		waittime = t;
	}



	private void dump(final PenTabletMouseEvent e){
		if(!SwingUtilities.isEventDispatchThread()){
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					switch (e.getState()) {
					case DRAGGED:
						t.mouseDragged(e);
						break;
					case MOVE:
						t.mouseMoved(e);
						break;
					case PRESSED:
						t.mousePressed(e);
						break;
					case RELEASED:
						t.mouseReleased(e);
						break;
					case ENTERED:
						if(!isPress)
							t.mouseEntered(e);
						break;
					case EXITED:
						if(!isPress)
							t.mouseExited(e);
						break;
					}
				}
			});
		}else{
			switch (e.getState()) {
			case ENTERED:
				if(!isPress)
					t.mouseEntered(e);
				break;
			case EXITED:
				if(!isPress)
					t.mouseExited(e);
				break;
			}
		}
	}

	void dump(final MouseWheelEvent e){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				t.mouseWheelMoved(e);
			}
		});
	}


	JPenRecognizer(PenTabletRecognizer obj,Component target){
		t = obj;
		c=target;
		set();
	}

	public void remove(){
		if(set){
			c.removeMouseListener(enter_exitListener);
			AwtPenToolkit.removePenListener(c, this);
			set=false;
		}
	}
	public void set(){
		if(!set){
			c.addMouseListener(enter_exitListener);
			AwtPenToolkit.addPenListener(c, this);
			set=true;
		}
	}
	@Override
	public void dispose() {
		remove();
		t=null;c=null;
	}

	private static boolean isMousePressed(Pen pen){
		return pen.getButtonValue(Type.LEFT)|
				pen.getButtonValue(Type.CENTER)|
				pen.getButtonValue(Type.RIGHT);
	}
	private static boolean isOnPress(Pen pen){
		return pen.getButtonValue(Type.ON_PRESSURE);
	}
	final public void penButtonEvent(PButtonEvent e) {
		if(e.button.getType().getGroup()==TypeGroup.MODIFIER){
			int m=0;
			switch (e.button.getType()) {
			case ALT:
				m = ALT_DOWN_MASK|ALT_MASK;
				break;
			case CONTROL:
				m = CTRL_DOWN_MASK|CTRL_MASK;
				break;
			case SHIFT:
				m = SHIFT_DOWN_MASK|SHIFT_MASK;
				break;
			default:
				return;
			}
			if(e.pen.getButtonValue(e.button.getType())){
				keymodify|=m;
			}else{
				keymodify &= ~m;
			}
			return;
		}

		Pen pen = e.pen;
		PKind k = pen.getKind();
		int modify = keymodify;
		CursorDevice ctype;
		State state;
		isPress = isMousePressed(pen);
		switch(k.getType()){
		case ERASER:
			if(e.button.getType()!=Type.ON_PRESSURE)return;
			ctype = CursorDevice.TABLET;
			modify |=PenTabletMouseEvent.TAIL_DOWN_MASK;
			state = isOnPress(pen)?State.PRESSED:State.RELEASED;
			break;
		case STYLUS:
			if(e.button.getType()!=Type.ON_PRESSURE)return;
			ctype = CursorDevice.TABLET;
			modify |=PenTabletMouseEvent.HEAD_DOWN_MASK;
			state = isOnPress(pen)?State.PRESSED:State.RELEASED;
			break;
		case CUSTOM:
			return;
		case CURSOR:
			state =isPress? State.PRESSED:State.RELEASED;
			ctype = CursorDevice.MOUSE;
			switch(e.button.getType()){
			case LEFT:
				modify |=BUTTON1_MASK|BUTTON1_DOWN_MASK;break;
			case CENTER:
				modify |=BUTTON2_MASK|BUTTON2_DOWN_MASK;break;
			case RIGHT:
				modify |=BUTTON3_MASK|BUTTON3_DOWN_MASK;break;
			default:
				return;
			}
			break;
		default:
			return;
		}
		time=0;
		float x,y,p,r,tx,ty;
		x = pen.getLevelValue(PLevel.Type.X);//x取得
		y = pen.getLevelValue(PLevel.Type.Y);//y取得
		p = pen.getLevelValue(PLevel.Type.PRESSURE);//筆圧取得
		r = pen.getLevelValue(PLevel.Type.ROTATION);//rotation
		tx= pen.getLevelValue(PLevel.Type.TILT_X);//x傾き
		ty= pen.getLevelValue(PLevel.Type.TILT_Y);//y傾き
		PenTabletMouseEvent ev= PenTabletMouseEvent.createEvent(
				c, e.getTime(), modify, x, y, pen.getPressedButtonsCount(),
				ctype, state, p, r, tx, ty);
		dump(ev);

		//exit event
		if(!isPress){
			if(!c.contains((int)x, (int)y)){
				MouseEvent exit =
						new MouseEvent(
								c, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), keymodify,
								(int)x, (int)y, 0, 0, 0, false, 0);
				dump(PenTabletMouseEvent.wrapEvent(exit));
			}
		}
	}


	public void penKindEvent(PKindEvent e) {
		Pen pen = e.pen;
		float x,y,r,tx,ty;

		PKind k = pen.getKind();
		CursorDevice ctype;
		State state = State.CURSORTYPECHANGE;
		int modify =keymodify;
		switch(k.getType()){
		case ERASER:
			modify |=PenTabletMouseEvent.TAIL_MASK;
			ctype = CursorDevice.TABLET;
			break;
		case STYLUS:
			modify |=PenTabletMouseEvent.HEAD_MASK;
			ctype = CursorDevice.TABLET;
			break;
		case CUSTOM:
			return;
		default:
			ctype = CursorDevice.MOUSE;
		}

		x = pen.getLevelValue(PLevel.Type.X);//x取得
		y = pen.getLevelValue(PLevel.Type.Y);//y取得
		r = pen.getLevelValue(PLevel.Type.ROTATION);//rotation
		tx= pen.getLevelValue(PLevel.Type.TILT_X);//x傾き
		ty= pen.getLevelValue(PLevel.Type.TILT_Y);//y傾き
		final PenTabletMouseEvent ev= PenTabletMouseEvent.createEvent(
				c, e.getTime(), modify, x, y, pen.getPressedButtonsCount(),
				ctype, state, 0, r, tx, ty);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				t.mouseOperatorChanged(ev);
			}
		});
	}

	public void penLevelEvent(PLevelEvent e) {
		if(waittime > time)return;
		time -=waittime;
		Pen pen = e.pen;


		PKind k = pen.getKind();
		CursorDevice ctype;
		State state;
		int modify = keymodify;
		switch(k.getType()){
		case ERASER:
			if(isOnPress(pen)){
				modify |=PenTabletMouseEvent.TAIL_DOWN_MASK;
				state = State.DRAGGED;
			}else{
				modify |=PenTabletMouseEvent.TAIL_MASK;
				state = State.MOVE;
			}
			ctype = CursorDevice.TABLET;
			break;
		case STYLUS:
			if(isOnPress(pen)){
				modify |=PenTabletMouseEvent.HEAD_DOWN_MASK;
				state = State.DRAGGED;
			}else{
				modify |=PenTabletMouseEvent.HEAD_MASK;
				state = State.MOVE;
			}
			ctype = CursorDevice.TABLET;
			break;
		case CUSTOM:
			return;
		case CURSOR:
			ctype = CursorDevice.MOUSE;
			state = isMousePressed(pen)?State.DRAGGED:State.MOVE;
			if(pen.getButtonValue(Type.LEFT)){
				modify |=BUTTON1_DOWN_MASK|BUTTON1_MASK;
			}
			if(pen.getButtonValue(Type.CENTER)){
				modify |=BUTTON2_DOWN_MASK|BUTTON2_MASK;
			}
			if(pen.getButtonValue(Type.RIGHT)){
				modify |=BUTTON3_DOWN_MASK|BUTTON3_MASK;
			}
			break;
		default:
			return;
		}


		float x,y,p,r,tx,ty;

		x = pen.getLevelValue(PLevel.Type.X);//x取得
		y = pen.getLevelValue(PLevel.Type.Y);//y取得
		p = pen.getLevelValue(PLevel.Type.PRESSURE);//筆圧取得
		r = pen.getLevelValue(PLevel.Type.ROTATION);//rotation
		tx= pen.getLevelValue(PLevel.Type.TILT_X);//x傾き
		ty= pen.getLevelValue(PLevel.Type.TILT_Y);//y傾き
		PenTabletMouseEvent ev= PenTabletMouseEvent.createEvent(
				c, e.getTime(), modify, x, y, pen.getPressedButtonsCount(),
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
		dump(new MouseWheelEvent(c, MouseEvent.MOUSE_WHEEL, e.getTime(), keymodify,
				x, y, pen.getPressedButtonsCount(), false,
				0, val, rotation));

	}

	public void penTock(long n) {
		if(waittime!=0)
			time+=n;
	}

}







//------------------------------------------------
//ただのリスナー
//------------------------------------------------
final class MouseRecognizer implements MouseListener,MouseMotionListener,MouseWheelListener,Listener{
	MouseRecognizer(PenTabletRecognizer t){this.t=t;set();}

	public void mousePressed(MouseEvent e) {
		t.mousePressed(PenTabletMouseEvent.wrapEvent(e));
	}


	public void mouseReleased(MouseEvent e) {
		t.mouseReleased(PenTabletMouseEvent.wrapEvent(e));
	}

	public void mouseMoved(MouseEvent e) {
		t.mouseMoved(PenTabletMouseEvent.wrapEvent(e));
	}

	public void mouseDragged(MouseEvent e) {
		t.mouseDragged(PenTabletMouseEvent.wrapEvent(e));
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		t.mouseWheelMoved(e);
	}
	public void mouseEntered(MouseEvent e) {
		t.mouseEntered(PenTabletMouseEvent.wrapEvent(e));
	}
	public void mouseExited(MouseEvent e) {
		t.mouseExited(PenTabletMouseEvent.wrapEvent(e));
	}
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
	public int canusetablet() {
		return -1;
	}
	@Override
	public void dispose() {
		remove();
		t=null;
	}
	@Override
	public void setWaitTime(long t) {
	}

	PenTabletRecognizer t;
}

