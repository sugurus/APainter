package apainter.gui;

import static apainter.misc.Util.*;
import static java.lang.Math.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nodamushi.pentablet.TabletMouseEvent;
import nodamushi.pentablet.TabletRecognizer;
import apainter.GlobalValue;
import apainter.bind.Bind;
import apainter.bind.BindObject;
import apainter.canvas.Canvas;
import apainter.misc.Angle;

//注意　Double==>Point2D.Double
public final class CanvasView extends JPanel{


	private  Dimension canvasDafaultSize;


	//座標などの情報
	private double
		zoom = 1,//拡大率
		centerR=0;//表示するキャンパスの中心のCanvasView上の距離(ただし、この値は画面の中心で0とする)
	private Angle centerAngle=new Angle();//表示するキャンパスの中心の角度。（ただし、画面が回転していない座標での回転角度）
	private boolean reverse = false;//キャンパスがx座標方向に反転表示するか否か

	//キャンパスのCanvasViewのコンポーネント座標系に対する回転角度。
	//我々の見た目で言えば、時計回りが正。
	private Angle angle=new Angle();

	//T:平行移動　S:n倍変換  R:画面中央で回転 Rev:水平方向逆転　cx = centerR*cos(centerAngle),cy=centerR*sin(centerAngle)
	//左上を原点とする通常のCanvasViewのコンポーネント座標からCanvas上の座標へ変換する行列
	//T(Lw/2,Lh/2)S(1/zoom)T(-cx,-cy)R(-angle)(Rev)
	private AffineTransform toCanvas  = new AffineTransform();
	//Canvas上の座標からCanvasViewのコンポーネント座標へ変換
	//toCanvasの逆行列
	//(Rev)R(angle)T(cx,cy)S(zoom)T(-Lw/2,-Lh/2)
	private AffineTransform toComponent= new AffineTransform();


	private TabletRecognizer tabletlistener;
	private KeyListener keylistener;
	private JComponent canvasComponent;//画像を表示するパネル
	private CanvasViewRendering canvasRendering;
	private JComponent background=new JPanel();//背景
	private JComponent overlayer=new JPanel();//画像を表示するパネルの上のパネル。
	@SuppressWarnings("unused")
	private GlobalValue global;
	private Canvas canvas;

	private ComponentListener componentlistener = new ComponentAdapter() {
		@Override public void componentResized(ComponentEvent e) {
			setAffinTransform();
		}
	};



	public CanvasView(int width,int height,JComponent canvascomponent,CanvasViewRendering rendering,Canvas canvas,GlobalValue global) {
		if(width<=0 || height <=0)throw new RuntimeException(String.format("width:%d,height:%d",width,height));
		if(canvascomponent==null)throw new NullPointerException("canvas");
		if(global==null)throw new NullPointerException("global");
		this.global =global;
		canvasComponent = canvascomponent;
		canvasRendering = rendering;
		this.canvas = nullCheack(canvas, "canvas is null!");
		canvasDafaultSize = new Dimension(width,height);
		setOpaque(false);
		setAffinTransform();
		addComponentListener(componentlistener);
		overlayer.setOpaque(false);
		overlayer.setVisible(false);
		background.setOpaque(false);
		background.setVisible(false);
		add(overlayer);
		add(canvasComponent);
		add(background);
		setComponentZOrder(overlayer, 0);
		setComponentZOrder(canvasComponent, 1);
		setComponentZOrder(background, 2);


		super.setLayout(new LayoutManager() {
			@Override
			public Dimension preferredLayoutSize(Container parent) {
				Container p = getParent();
				Dimension d =p==null?new Dimension():p.getSize();
				if(d.width==0||d.height==0){
					if(canvasComponent!=null){
						d = canvasComponent.getPreferredSize();
					}else{
						d.width = 30;
						d.height = 30;
					}
				}
				return d;
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return new Dimension(30,30);
			}

			@Override
			public void layoutContainer(Container parent) {
				int w = getWidth(),h=getHeight();
				canvasComponent.setBounds(0, 0, w, h);

				background.setBounds(0, 0, w, h);
				overlayer.setBounds(0, 0, w, h);
				canvasRendering.init();

			}
			@Override public void removeLayoutComponent(Component comp) {}
			@Override public void addLayoutComponent(String name, Component comp) {}
		});
		installMouseListener();
	}

	public void removeKeyListener(){
		removeKeyListener(keylistener);
	}

	public void addKeyListener(){
		for(KeyListener k:getKeyListeners())if(k==keylistener)return;
		addKeyListener(keylistener);
	}

	private void installMouseListener(){
		tabletlistener = new TabletRecognizer(this) {

			@Override
			public void mouseOperatorChanged(TabletMouseEvent e) {
				Double k = convertToCanvas(e.getPoint2D());
				e.setPoint(k.x, k.y);
				canvas.dispatchEvent(e);
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Double k = convertToCanvas(e.getPoint());
				MouseWheelEvent e2 = new MouseWheelEvent((Component)e.getSource(), e.getID(), e.getWhen(),
						e.getModifiers()|e.getModifiersEx(),
						(int)k.x, (int)k.y, e.getClickCount(),
						e.isPopupTrigger(), e.getScrollType(),
						e.getScrollAmount(), e.getWheelRotation());
				canvas.dispatchEvent(e2);
			}

			@Override
			public void mousePressed(TabletMouseEvent e) {
				if(!hasFocus())	requestFocus();
				Double k = convertToCanvas(e.getPoint2D());
				e.setPoint(k.x, k.y);
				canvas.dispatchEvent(e);
			}

			@Override
			public void mouseReleased(TabletMouseEvent e) {
				if(e.isPopupTrigger()){
					//位置変換なし
					canvas.dispatchEvent(e);
					return;
				}
				Double k = convertToCanvas(e.getPoint2D());
				e.setPoint(k.x, k.y);
				canvas.dispatchEvent(e);
			}
			@Override
			public void mouseDragged(TabletMouseEvent e) {
				Double k = convertToCanvas(e.getPoint2D());
				e.setPoint(k.x, k.y);
				canvas.dispatchEvent(e);
			}

			@Override
			public void mouseMoved(TabletMouseEvent e) {
				Double k = convertToCanvas(e.getPoint2D());
				e.setPoint(k.x, k.y);
				canvas.dispatchEvent(e);
			}

			@Override
			public void mouseExited(TabletMouseEvent e) {
				Double k = convertToCanvas(e.getPoint2D());
				e.setPoint(k.x, k.y);
				canvas.dispatchEvent(e);
			}

			@Override
			public void mouseEntered(TabletMouseEvent e) {
				Double k = convertToCanvas(e.getPoint2D());
				e.setPoint(k.x, k.y);
				canvas.dispatchEvent(e);
			}
		};
	}


	public void setQuarityRendering(boolean b){
		canvasRendering.qualityRendering(b);
	}

	public void rendering(){
		canvasRendering.rendering();
	}

	public void rendering_rotation(){
		canvasRendering.rotation();
	}

	public void rendering(Rectangle r){
		canvasRendering.rendering(r);
	}

	public void renderingFlug(Rectangle r){
		canvasRendering.rendering(r);
	}

	public double getZoom() {
		return zoom;
	}


	/**
	 * 背景に設定されているコンポーネントを返します。<br>
	 * 初期状態では不可視になっています。
	 * @return
	 */
	public JComponent getBackGroundComponent(){
		return background;
	}
	/**
	 * キャンバスの上に描画されるコンポーネントを返します。<br>
	 * 初期状態では不可視になっています。
	 * @return
	 */
	public JComponent getOverLayerComponent(){
		return overlayer;
	}

	public boolean isReverse() {
		return reverse;
	}

	public Angle getAngle() {
		return angle;
	}

	public Dimension getCanvasDafaultSize() {
		return new Dimension(canvasDafaultSize);
	}

	public AffineTransform getToCanvas() {
		return new AffineTransform(toCanvas);
	}

	public AffineTransform getToComponent() {
		return new AffineTransform(toComponent);
	}

	//----setter--------------------------------------
	public void setReverse(boolean reverse) {
		if(reverse==this.reverse){
			reverseBindObject.set(reverse);
		}
	}
	public void setZoom(double zoom) {
		zoomBindObject.set(zoom);
	}

	public void setAngle(Angle a){
		if(a==null)return;
		angleBindObject.set(a);
	}
	public void setAngle(double degree) {
		setAngle(new Angle(degree));
	}

	/**
	 * @param x 画面の中央からの位置（横軸）
	 * @param y 画面の中央からの位置（下向き正）
	 */
	public void setCenterPoint(double x,double y){
		double tx = x*angle.cos+y*angle.sin;
		double ty = -x*angle.sin+y*angle.cos;
		setDefaultCenterPoint(tx, ty);
	}

	/**
	 * @param x 画面の中央からの位置（横軸）
	 */
	public void setCenterPointX(double x){
		setCenterPoint(x, getCenterY());
	}

	/**
	 * @param y 画面の中央からの位置（下向き正）
	 */
	public void setCenterPointY(double y){
		setCenterPoint(getCenterX(), y);
	}

	private void setDefaultCenterPoint(double x,double y){
		Double d = new Double(x,y);
		posBindObject.set(d);
	}

	/**
	 * 画像が回転してない状態でのキャンバスの中央座標(画面の中央が原点)
	 * @return
	 */
	public double getDefaultCenterX(){
		return centerR*centerAngle.cos;
	}

	/**
	 * 画像が回転していない状態でのキャンバスの中央座標(画面の中央が原点)
	 * @return
	 */
	public double getDefaultCenterY(){
		return centerR*centerAngle.sin;
	}

	/**
	 * 画像が回転している状態でのキャンバスの中央座標(画面の中央が原点)
	 * @return
	 */
	public double getCenterX(){
		return centerAngle.add(angle).cos*centerR;
	}
	/**
	 * 画像が回転している状態でのキャンバスの中央座標(画面の中央が原点)
	 * @return
	 */
	public double getCenterY(){
		return centerAngle.add(angle).sin*centerR;
	}


	private void setAffinTransform(){
		Double p = getCenterPoint();
		double cx = getDefaultCenterX()+p.x;
		double cy = getDefaultCenterY()+p.y;
		double lw = canvasDafaultSize.width/2d;
		double lh = canvasDafaultSize.height/2d;

		toCanvas.setToTranslation((int)lw, (int)lh);

		toCanvas.scale(1/zoom, 1/zoom);

		toCanvas.translate(-(int)cx, -(int)cy);

		toCanvas.translate((int)p.x, (int)p.y);
		toCanvas.rotate(-angle.radian);
		toCanvas.translate(-(int)p.x, -(int)p.y);

		if(reverse)reverse(toCanvas);

		try {
			toComponent = toCanvas.createInverse();
		} catch (NoninvertibleTransformException e) {
			//必ず逆行列は存在する。
		}
	}

	private void reverse(AffineTransform af){
		af.scale(-1, 1);
		af.translate(getWidth(), 0);
	}

	private Double getCenterPoint(){
		return new Double(getWidth()/2d,getHeight()/2d);
	}

	/**
	 * コンポーネント座標系の座標pをキャンバス座標系に変換する
	 * @param p コンポーネント座標系の座標
	 * @return pのキャンバス座標系での座標
	 */
	public Double convertToCanvas(Double p){
		Double d = new Double();
		toCanvas.transform(p, d);
		return d;
	}
	public Double convertToCanvas(Point p){
		Double d = new Double();
		toCanvas.transform(p, d);
		return d;
	}
	/**
	 * コンポーネント座標系の座標をキャンバス座標系に変換する
	 * @param x コンポーネント座標系の座標
	 * @param y コンポーネント座標系の座標
	 * @return (x,y)のキャンバス座標系の座標
	 */
	public Double convertToCanvas(double x,double y){
		return convertToCanvas(new Double(x, y));
	}

	/**
	 * コンポーネント座標の中央をキャンバス座標に変換します。
	 * @return
	 */
	public Double convertCenterToCanvas(){
		return convertToCanvas(getWidth()/2d, getHeight()/2d);
	}


	/**
	 * キャンバス座標系の座標pをコンポーネント座標系に変換する
	 * @param p キャンバス座標系の座標
	 * @return pのコンポーネント座標系での座標
	 */
	public Double convertToComponent(Double p){
		Double d = new Double();
		toComponent.transform(p, d);
		return d;
	}

	/**
	 * キャンバス座標系の座標をキャンバス座標系に変換する
	 * @param x キャンバス座標系の座標
	 * @param y キャンバス座標系の座標
	 * @return コンポーネント座標系での座標
	 */
	public Double convertToComponent(double x,double y){
		return convertToComponent(new Double(x, y));
	}

	/**
	 * 外部からのレイアウトの変更は許可されません。
	 */
	@Override @Deprecated
	public void setLayout(LayoutManager mgr) {}







	public void clear(){
		removeAllBinds();
		removeAll();
		canvasRendering.dispose();
		canvas = null;
		global = null;
		angle = null;
		background = null;
		canvasRendering = null;
		canvasComponent = null;
		toCanvas = null;
		toComponent = null;
		canvasDafaultSize = null;
		centerAngle = null;
		overlayer = null;
		tabletlistener.dispose();
		tabletlistener = null;
	}


	//----bind-------------------

	public void removeAllBinds(){
		reverseBind.removeAll();
		posBind.removeAll();
		angleBind.removeAll();
		zoomBind.removeAll();
	}

	private final BindObject reverseBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			reverse = (Boolean)value;
			setAffinTransform();
		}

		@Override
		public Object get() {
			return reverse;
		}

		public boolean isSettable(Object value) {
			return value instanceof Boolean;
		}
	};
	private final Bind reverseBind = new Bind(reverseBindObject);

	public void addreverseBindObject(BindObject b) {
		reverseBind.add(b);
	}

	public void removereverseBindObject(BindObject b) {
		reverseBind.remove(b);
	}

	private boolean posset= true;
	private final BindObject posBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			if(posset){
				Point2D p = (Point2D) value;
				double x = p.getX(),y = p.getY();
				centerR = hypot(x, y);
				centerAngle = Angle.getAngle(x, y).add(-angle.degree);
				setAffinTransform();
			}
		}

		@Override
		public Object get() {
			return new Double(getCenterX(),getCenterY());
		}

		public boolean isSettable(Object value) {
			return value instanceof Point2D;
		}
	};
	private final Bind posBind = new Bind(posBindObject);

	public void addposBindObject(BindObject b) {
		posBind.add(b);
	}

	public void removeposBindObject(BindObject b) {
		posBind.remove(b);
	}

	private final BindObject angleBindObject = new BindObject() {

		@Override
		public void setValue(Object value) throws Exception {
			angle = (Angle)value;
			setAffinTransform();
			posset  = false;
			setCenterPoint(getCenterX(), getCenterY());
			posset = true;
		}

		@Override
		public Object get() {
			return angle;
		}

		public boolean isSettable(Object value) {
			return value instanceof Angle;
		}
	};
	private final Bind angleBind = new Bind(angleBindObject);

	public void addangleBindObject(BindObject b) {angleBind.add(b);}
	public void removeangleBindObject(BindObject b) {angleBind.remove(b);}
	private final BindObject zoomBindObject = new BindObject() {
		@Override
		public void setValue(Object value) throws Exception {
			double d = (java.lang.Double)value;
			double old = zoom;
			zoom = d;
			double k = zoom/old;
			centerR *=k;
			setAffinTransform();
			posset  = false;
			setCenterPoint(getCenterX(), getCenterY());
			posset = true;
		}

		@Override
		public Object get() {
			return zoom;
		}

		public boolean isSettable(Object obj) {
			if(obj instanceof java.lang.Double){
				double d = (java.lang.Double)obj;
				if(d < 0.20)return false;
				else if(d > 16)return false;
				return true;
			}
			return false;
		}
	};
	private final Bind zoomBind = new Bind(zoomBindObject);
	public void addzoomBindObject(BindObject b) {zoomBind.add(b);}
	public void removezoomBindObject(BindObject b) {zoomBind.remove(b);}


	public void repaintMove() {
		canvasRendering.repaintMove();
	}

}
