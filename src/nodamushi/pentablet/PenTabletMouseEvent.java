package nodamushi.pentablet;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

public final class PenTabletMouseEvent extends MouseEvent{



	private static final long serialVersionUID = 5560345349890009137L;
	private int x,y;
	private double dx,dy;
	private ButtonType buttontype;
	private CursorDevice cursorDevice;
	private State state;
	private double
		pressure=1,//筆圧
		titx=0,tity=0,//ペンの傾き
		rotation=0;//回転角度

	//Mouse用
	private PenTabletMouseEvent(Component source,int id,long when,int modifiers,
            int x,int y,int clickCount,boolean popupTrigger,int button,
            ButtonType btype,CursorDevice ctype,State state)
	{
		super(source,id,when,modifiers,x,y,clickCount,popupTrigger,button);
		dx = x+0.5f;dy=y+0.5f;
		this.x=x;this.y=y;
		buttontype = btype;
		cursorDevice=ctype;
		this.state = state;
	}


	private PenTabletMouseEvent(Component source,int id,long when,int modifiers,
            double x,double y,int clickCount,boolean popupTrigger,ButtonType
            btype,CursorDevice ctype,State state,
            double pres,double rot,double tx,double ty)
	{
		super(source,id,when,modifiers,(int)x,(int)y,clickCount,popupTrigger);
		setPoint(x,y);
		buttontype = btype;
		cursorDevice=ctype;
		this.state = state;
		if(ctype == CursorDevice.TABLET)
		{
			pressure = pres;
			titx = tx;
			tity = ty;
			rotation = rot;
		}
	}


	public boolean isTypedKeyBord(){
		return buttontype==ButtonType.CONTROL||
				buttontype==ButtonType.SHIFT||
				buttontype==ButtonType.ALT;
	}


	/**
	 * 筆圧がクリック最低圧を超えているかどうかを返します。<br>
	 * 筆圧があっても最低筆圧以下ではfalseになります。
	 * @see #setClickMinPressure(double)
	 * @see #setClickMinPressure(int)
	 * @see #getClickMinPressure()
	 *
	 */
	public boolean isPressed(){	return pressure >= clickedMinPressure;}

	/**
	 * このイベントの発生元がマウスによる物かどうかを返します
	 * @see CursorDevice
	 * @see #getCursorDevice()
	 */
	public boolean isMouseDevice(){return cursorDevice == CursorDevice.MOUSE;}
	/**
	 * このイベントの発生元がタブレットによる物かどうかを返します
	 * @see CursorDevice
	 * @see #getCursorDevice()
	 */
	public boolean isTabletDevice(){return cursorDevice == CursorDevice.TABLET;}

	/**
	 * このインスタンスの保持する点を変更します
	 * @param x 変更後のx座標
	 * @param y 変更後のy座標
	 * @see #getX()
	 * @see #getY()
	 * @see #getXY()
	 * @see #getXYDouble()
	 */
	public void setPoint(double x,double y){
		dx = x;dy=y;
		this.x=(int)x;this.y=(int)y;
	}
	/**
	 * このインスタンスの保持する点を変更します
	 * @param p 変更後の座標
	 * @see #getX()
	 * @see #getY()
	 * @see #getXY()
	 * @see #getXYDouble()
	 */
	public void setPoint(Point.Double p){if(p != null)setPoint(p.x,p.y);}
	/**
	 * このインスタンスの保持する点を変更します
	 * @param x 変更後のx座標
	 * @param y 変更後のy座標
	 * @see #getX()
	 * @see #getY()
	 * @see #getXY()
	 * @see #getXYDouble()
	 */
	public void setPoint(int x,int y)
	{
		this.x = x;
		this.y = y;
		dx = x;
		dy = y;
	}
	/**
	 * このインスタンスの保持する点を変更します
	 * @param p 変更後の座標
	 * @see #getX()
	 * @see #getY()
	 * @see #getXY()
	 * @see #getXYDouble()
	 */
	public void setPoint(Point p){ if(p != null)setPoint(p.x,p.y); }
	/**筆圧を設定します*/
	public void setPressure(double p){pressure = p;}
	/**x方向の傾きを設定します*/
	public void setAlititudeX(double a){titx  = a;}
	/**y方向の傾きを設定します*/
	public void setAlititudeY(double a){tity  = a;}
	/**回転角度を設定します*/
	public void setRotation(double a){rotation = a;}

	@Override
	public Point getPoint() {return new Point(x,y);}

	/**
	 * <p>マウス座標を配列で返します。</p>
	 * @return {x,y}
	 */
	public int[] getXY(){return new int[] {x,y};}
	/**
	 * <p>マウス座標をdouble型配列で返します。</p>
	 * @return {x,y}
	 */
	public double[] getXYDouble() {	return new double[] {dx,dy};}

	/**
	 * <p>マウス座標をPoint2D.Doubleで返します。</p>
	 */
	public Point2D.Double getPointDouble() {
		return new Point2D.Double(dx, dy);
	}


	/**
	 * ペンの筆圧を返します。
	 */
	public double getPressure(){return pressure;}
	/**
	 * ペンの傾きを取得します。
	 */
	public double getAlititudeX(){return titx;}
	/**
	 * ペンの傾きを取得します。
	 */
	public double getAlititudeY(){return tity;}

	/**
	 * ペンが北（上方向）から時計回りに何度の方向を向いているか
	 */
	public double getRotation(){return rotation;}


	/**
	 * 変化したボタンを返します。
	 * @see ButtonType
	 */
	public ButtonType getButtonType(){return buttontype;}
	/**
	 * このイベントを発生させたデバイスを返します。
	 * @see CursorDevice
	 */
	public CursorDevice getCursorDevice(){return cursorDevice;}
	/**
	 * このマウスイベントの状態を返します。
	 * @see State
	 * @see #getID()
	 */
	public State getState(){return state;}



	/**
	 * 渡された時間からダブルクリックを満たす時間経過かどうかを返します。
	 * @param when 対象の時間
	 * @return whenからダブルクリックを満たす時間経過かどうか
	 */
	public boolean isDoubleClick(long when){
		long t = getWhen()-when;
		return t >= doubleclickedMinTime && t <= doubleclickedMaxTime;
	}

	@Override
	public String toString() {
		return String.format("(x:%f,y:%f),pressure %f,tit(x %f,y %f),rotation %f,ButtonType %s,CursorType %s,State %s",dx,dy,pressure,titx,tity,rotation,buttontype.toString(),cursorDevice.toString(),state.toString());
	}

	public void setState(State s){ state = s; }


	/**
	 * ペン（先端）でクリックされたかどうか。
	 * @return ペンでクリックされた場合true。マウスでもtrueが返ります。
	 */
	public boolean isPen(){
		return buttontype == ButtonType.HEAD||buttontype == ButtonType.BUTTON1;
	}





	//****************************************************************//
	//************************static**********************************//
	//****************************************************************//

	/**
	 * CURSORTYPECHANGEのイベントid番号
	 */
	static public final int MOUSE_CURSORTYPECHANGE=510;

	static private ButtonType[] popuptrigger = {ButtonType.BUTTON3,ButtonType.SIDE2};
	static private double clickedMinPressure=0.1f;
	static private int doubleclickedMinTime = 60;
	static private int doubleclickedMaxTime = 200;
	/**
	 * クリックされたかどうかの境目の筆圧の割合を設定します
	 * @param pressure 百分率
	 */
	static public void setClickMinPressure(int pressure){
		clickedMinPressure = pressure/100f;
		if(clickedMinPressure > 1)clickedMinPressure = 1;
		else if(clickedMinPressure< 0)clickedMinPressure = 0;
	}
	/**
	 * クリックされたかどうかの境目の筆圧の割合を設定します
	 * @param pressur0～1
	 */
	static public void setClickMinPressure(double pressure){
		clickedMinPressure = pressure;
		if(clickedMinPressure > 1)clickedMinPressure = 1;
		else if(clickedMinPressure< 0)clickedMinPressure = 0;
	}
	/**
	 * クリックされたかどうかの境目の筆圧の割合を返します。
	 * @return 0～1
	 */
	static public double getClickMinPressure(){return clickedMinPressure;}

	static public void setDoubleClickMinTime(int milisec)
	{
		if(milisec < 0)milisec = 0;
		else if(milisec >= doubleclickedMaxTime)milisec = doubleclickedMaxTime-1;
		doubleclickedMinTime = milisec;
	}

	static public int getDoubleClickMinTime(){return doubleclickedMinTime;}

	static public void setDoubleClickMaxTime(int milisec)
	{
		if(milisec < doubleclickedMinTime)milisec = doubleclickedMinTime+1;
		else if(milisec > 1000)milisec = 1000;
		doubleclickedMaxTime = milisec;
	}

	static public int getDoubleClickMaxTime(){ return doubleclickedMaxTime;}


	/**
	 * 前後関係を指定し、マウスイベントをラッピングします
	 * @param e 元となるマウスイベント
	 * @param befo リストの前方
	 * @param nex リストの後方
	 * @return ラップしたAPainterMouseEvent
	 */
	static public PenTabletMouseEvent wrapEvent(MouseEvent e)
	{
		State state;
		int id = e.getID();
		state = State.getState(id);
		ButtonType bt=ButtonType.NULL;
		if(SwingUtilities.isLeftMouseButton(e))bt = ButtonType.BUTTON1;
		else if(SwingUtilities.isMiddleMouseButton(e))bt = ButtonType.BUTTON2;
		else if(SwingUtilities.isRightMouseButton(e))bt = ButtonType.BUTTON3;
		return new PenTabletMouseEvent((Component)e.getSource(), id, e.getWhen(), e.getModifiers(), e.getX(), e.getY(),
				e.getClickCount(), e.isPopupTrigger(), e.getButton(), bt, CursorDevice.MOUSE, state);
	}

	static public PenTabletMouseEvent createEvent(Component source,long when,int modifiers,double x,double y,
			int clickCount,ButtonType btype,
			CursorDevice ctype,State state,double pres,double rot,double tx,double ty)
	{
		return createEvent(source, when, modifiers, x, y, clickCount,
				btype, ctype, state, pres, rot, tx, ty, null, null);
	}


	/**
	 *
	 * @param source
	 * @param when
	 * @param modifiers shift,alt,meta,ctrのダウンマスクを設定するだけで十分です。（BUTTON1_DOWN_MASK等は自動で設定します）
	 * @param x
	 * @param y
	 * @param clickCount
	 * @param btype
	 * @param ctype
	 * @param state
	 * @param pres
	 * @param rot
	 * @param tx
	 * @param ty
	 * @param befo
	 * @param nex
	 * @return
	 */
	static PenTabletMouseEvent createEvent(Component source,long when,int modifiers,double x,double y,
			int clickCount,ButtonType btype,CursorDevice ctype,State state,
			double pres,double rot,double tx,double ty,PenTabletMouseEvent befo,PenTabletMouseEvent nex)
	{
		boolean popupTrigger=false;
		for(ButtonType b :popuptrigger)
			if(b==btype){
				popupTrigger=true;
				break;
			}

		switch(btype)
		{
		case BUTTON1:
		case HEAD:
		case SIDE1:
			modifiers|=InputEvent.BUTTON1_DOWN_MASK;
			break;
		case BUTTON2:
			modifiers|=InputEvent.BUTTON2_DOWN_MASK;
			break;
		case BUTTON3:
		case TAIL:
		case SIDE2:
		case CUSTOM:
			modifiers|=InputEvent.BUTTON3_DOWN_MASK;
			break;
		case NULL:
			break;
		}
		if(pres <0)pres = 0;
		else if(pres >1)pres = 1;
		int id=state.getID();
		return new PenTabletMouseEvent(source, id, when, modifiers, x, y,
				clickCount, popupTrigger, btype, ctype, state, pres, rot, tx, ty);
	}

	static public PenTabletMouseEvent createEvent(Component source,long when,int modifiers,double x,double y,
			int clickCount,ButtonType btype,CursorDevice ctype,State state)
	{
		return createEvent(source, when, modifiers, x, y, clickCount, btype, ctype, state, 1, 0, 0, 0);
	}
	/**
	 * 現在タブレットの先端（もしくはマウス）で操作されているかのおおよそを返します。
	 * @return
	 */
	static public boolean isTabletCursorPen(){return PenTabletRecognizer.getFinalTabletType() == ButtonType.HEAD;}


	//enums/////////////////////////////////////////////////////////////////////
	/**
	 * どのボタンかを表す。
	 */
	static public enum ButtonType{
		/**ペン先*/HEAD,/**ペンの後ろ（消しゴム）*/TAIL,
		/**ペンのサイドボタン*/SIDE1,/**ペンのサイドボタン*/SIDE2,
		/**マウスの左（であることが多い）*/BUTTON1,/**マウスの右（であることが多い）*/BUTTON3,/**マウスの中（であることが多い）*/BUTTON2,
		/**カスタムボタン*/CUSTOM,
		/**ALTボタンが押されました**/ALT,/**SHIFTが押されました**/SHIFT,/**CONTROLが押されました。**/CONTROL,
		/**判別不能の時（nullの代用）*/NULL,
	}
	/**
	 * カーソルを動かしているデバイス
	 */
	static public enum CursorDevice{
		TABLET,MOUSE
	}
	/**
	 * ボタンの状態
	 */
	static public enum State{
		PRESSED,DRAGGED,RELEASED,ENTERED,EXITED,MOVE,
		/**カーソルを動かしているデバイスが変わった*/CURSORTYPECHANGE,
		/**判別不能の時（nullの代用）*/NULL,
		/**非推奨。マウスイベントとの整合性を保つ為にあります。今後もタブレットでこのイベントは発生させません*/CLICKED;
		/**
		 * 対応するMouseイベントのidを返します。
		 * @return
		 */
		public int getID(){ return getID(this); }
		/**
		 * 渡されたstateに対応するMouseイベントのidを返します
		 */
		public static int getID(State state){
			int id=0;
			switch (state) {
			case PRESSED:
				id = MOUSE_PRESSED;
				break;
			case DRAGGED:
				id = MOUSE_DRAGGED;
				break;
			case RELEASED:
				id = MOUSE_RELEASED;
				break;
			case MOVE:
				id = MOUSE_MOVED;
				break;
			case CURSORTYPECHANGE:
				id = MOUSE_CURSORTYPECHANGE;
				break;
			case CLICKED:
				id = MOUSE_CLICKED;
				break;
			case ENTERED:
				id = MOUSE_ENTERED;
				break;
			case EXITED:
				id = MOUSE_EXITED;
				break;
			}
			return id;
		}

		/**
		 * 渡されたMouseイベントのidに対応するStateを返します
		 */
		public static State getState(int id){
			State state;
			switch(id){
			case MOUSE_CURSORTYPECHANGE:
				state = CURSORTYPECHANGE;
				break;
			case MOUSE_CLICKED:
				state =State.CLICKED;
				break;
			case MOUSE_DRAGGED:
				state = State.DRAGGED;
				break;
			case MOUSE_ENTERED:
				state = State.ENTERED;
				break;
			case MOUSE_EXITED:
				state = State.EXITED;
				break;
			case MOUSE_MOVED:
				state = State.MOVE;
				break;
			case MOUSE_PRESSED:
				state = State.PRESSED;
				break;
			case MOUSE_RELEASED:
				state = State.RELEASED;
				break;
			default:
				state = State.NULL;
			}
			return state;
		}
	}
}
