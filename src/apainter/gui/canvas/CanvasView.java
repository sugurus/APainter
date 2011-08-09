package apainter.gui.canvas;

import static apainter.misc.Util.*;
import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.canvas.Canvas;
import apainter.construct.Angle;
import java.awt.geom.Point2D.Double;

import nodamushi.pentablet.PenTabletMouseEvent;
import nodamushi.pentablet.PenTabletRecognizer;

//注意　Double==>Point2D.Double
public final class CanvasView extends JPanel{


	private final Dimension canvasDafaultSize;


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
	private final AffineTransform toCanvas  = new AffineTransform();
	//Canvas上の座標からCanvasViewのコンポーネント座標へ変換
	//toCanvasの逆行列
	//(Rev)R(angle)T(cx,cy)S(zoom)T(-Lw/2,-Lh/2)
	private AffineTransform toComponent= new AffineTransform();


	@SuppressWarnings("unused")
	private PenTabletRecognizer tabletlistener;
	private JComponent canvasComponent;//画像を表示するパネル
	private CanvasViewRendering canvasRendering;
	private JComponent background=new JPanel();//背景
	private JComponent overlayer=new JPanel();//画像を表示するパネルの上のパネル。
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
		setBackground(Color.white);
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
				//FIXME 書き直し。
				if(canvasComponent instanceof CPUCanvasPanel){
					CPUCanvasPanel c = (CPUCanvasPanel) canvasComponent;
					c.init();
				}
				background.setBounds(0, 0, w, h);
				overlayer.setBounds(0, 0, w, h);
			}
			@Override public void removeLayoutComponent(Component comp) {}
			@Override public void addLayoutComponent(String name, Component comp) {}
		});

		installMouseListener();

	}


	private void installMouseListener(){
		tabletlistener = new PenTabletRecognizer(this) {
			CanvasMouseListener h,t;

			@Override
			public void operatorChanged(PenTabletMouseEvent e) {
				// TODO operatorChange

			}

			@Override
			public void onScroll(MouseWheelEvent e) {
				// TODO onScroll

			}

			@Override
			public void onPressed(PenTabletMouseEvent e) {
				if(!hasFocus())	requestFocus();
				Double k = convertToCanvas(e.getPointDouble());
				e.setPoint(k.x, k.y);
				h=t=null;
				switch(e.getButtonType()){
				case HEAD:
				case BUTTON1:
					Object head = global.get(GlobalKey.CanvasHeadAction);
					if(head!=null && head instanceof CanvasMouseListener)(h=(CanvasMouseListener)head).press(e,canvas);
					break;
				case TAIL:
					Object tail = global.get(GlobalKey.CanvasTailAction);
					if(tail!=null && tail instanceof CanvasMouseListener)(t=(CanvasMouseListener)tail).press(e,canvas);
					break;
				case BUTTON3:
					break;
				case BUTTON2:
					break;
				case SIDE1:
					break;
				case SIDE2:
					break;
				}
			}

			@Override
			public void onReleased(PenTabletMouseEvent e) {
				Double k = convertToCanvas(e.getPointDouble());
				e.setPoint(k.x, k.y);
				switch(e.getButtonType()){
				case HEAD:
				case BUTTON1:
					if(h!=null)h.release(e,canvas);
					break;
				case TAIL:
					if(t!=null)t.release(e,canvas);
					break;
				case BUTTON3:
					break;
				case BUTTON2:
					break;
				case SIDE1:
					break;
				case SIDE2:
					break;
				}
				h=t=null;
			}
			@Override
			public void onDragged(PenTabletMouseEvent e) {
				Double k = convertToCanvas(e.getPointDouble());
				e.setPoint(k.x, k.y);
				switch(e.getButtonType()){
				case HEAD:
				case BUTTON1:
					if(h!=null)h.drag(e,canvas);
					break;
				case TAIL:
					if(t!=null)t.drag(e,canvas);
					break;
				case BUTTON3:
					break;
				case BUTTON2:
					break;
				case SIDE1:
					break;
				case SIDE2:
					break;
				}
			}

			@Override
			public void onMove(PenTabletMouseEvent e) {
				//TODO move
			}

			@Override
			public void onExit(PenTabletMouseEvent e) {
				// TODO exit
			}

			@Override
			public void onEnter(PenTabletMouseEvent e) {
				// TODO enter
			}

		};
	}

	public void rendering(){
		canvasRendering.rendering();
	}
	public void rendering(Rectangle r){
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
			this.reverse = reverse;
			setAffinTransform();
		}
	}
	public void setZoom(double zoom) {
		if(zoom < 0.05)zoom = 0.05;
		else if(zoom > 16)zoom = 16;
		this.zoom = zoom;
		setAffinTransform();
	}

	public void setAngle(Angle a){
		if(a==null)return;
		angle = a;
		setAffinTransform();
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

	public void setDefaultCenterPoint(double x,double y){
		centerR = hypot(x, y);
		centerAngle = Angle.getAngle(x, y);
		setAffinTransform();
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
			e.printStackTrace();
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


}
