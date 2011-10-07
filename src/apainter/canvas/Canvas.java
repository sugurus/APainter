package apainter.canvas;

import static apainter.GlobalKey.*;
import static apainter.PropertyChangeNames.*;
import static apainter.misc.Util.*;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import nodamushi.pentablet.TabletMouseEvent;
import nodamushi.pentablet.TabletMouseEvent.CursorDevice;
import apainter.APainter;
import apainter.CreateHandler;
import apainter.Device;
import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.canvas.cedt.CanvasEventAccepter;
import apainter.canvas.cedt.cpu.CPUCEA_0;
import apainter.canvas.event.CanvasEvent;
import apainter.canvas.event.PaintEvent;
import apainter.canvas.layerdata.CPULayerData;
import apainter.canvas.layerdata.InnerLayerHandler;
import apainter.canvas.layerdata.LayerData;
import apainter.canvas.layerdata.LayerHandler;
import apainter.data.PixelDataByte;
import apainter.data.PixelDataInt;
import apainter.drawer.DrawTarget;
import apainter.gui.CPUCanvasPanel;
import apainter.gui.CanvasMouseListener;
import apainter.gui.CanvasScrollListener;
import apainter.gui.CanvasView;
import apainter.gui.RightReleaseListener;
import apainter.history.History;
import apainter.history.HistoryObject;
import apainter.misc.PropertyChangeUtility;
import apainter.resorce.LimitedResource;

public class Canvas implements CreateHandler{


	//property---------------------------------------------------------
	private int width,height;
	private Device device;
	private String author,canvasname;
	private long makeDay,workTime,actionCount;
	private long createdTime;//このCanvasが作成された時間
	private LayerData layerdata;
	private int id;

	private GlobalValue global;
	private CanvasEventAccepter ceaccepter;
	private CanvasView view;
	private APainter apainter;

	private History history;


	private CPUCanvasPanel cpucanvas;

	private final LimitedResource<PixelDataInt> intbuffer;
	private final LimitedResource<PixelDataByte> bytebuffer;


	public Canvas(int width,int height,Device device,GlobalValue globalvalue,int canvasid,APainter ap){
		this(width,height,device,globalvalue,null,null,0,0,0,canvasid,ap);
	}

	public Canvas(int width,int height,Device device,GlobalValue globalvalue,
			String author,String canvasname,long makeDay,long workTime,long actionCount,int canvasid,APainter ap) {
		if(width <=0 || height <= 0)
			throw new IllegalArgumentException(String.format("width:%d,height:%d",width,height));
		if(globalvalue ==null)throw new NullPointerException("GlobalValue");

		//TODO GPU実装がもしいつかできたら外す
		if(device==Device.GPU){
			throw new RuntimeException("GPUデバイスには対応していません");
		}
		global = globalvalue;

		if(makeDay<=0)makeDay = System.currentTimeMillis();
		if(workTime <=0)workTime = 0;
		if(actionCount<=0)actionCount = 0;
		if(author==null)author = "";
		if(canvasname ==null)canvasname="";

		this.width = width;
		this.height = height;
		this.device =device;
		this.author = author;
		this.canvasname = canvasname;
		this.makeDay = makeDay;
		this.workTime  = workTime;
		this.actionCount = actionCount;
		this.apainter = nullCheack(ap, "apainter is null");
		this.intbuffer=new LimitedResource<PixelDataInt>(5,new LimitedResource.ResourceFactory<PixelDataInt>(){
			public PixelDataInt create() {
				return PixelDataInt.create(Canvas.this.width, Canvas.this.height);
			}
		});
		this.bytebuffer=new LimitedResource<PixelDataByte>(5,new LimitedResource.ResourceFactory<PixelDataByte>(){
			public PixelDataByte create() {
				return PixelDataByte.create(Canvas.this.width, Canvas.this.height);
			}
		});
		id = canvasid;
		createdTime = System.currentTimeMillis();
		history = new History(this);
		switch(device){

		case CPU:
			initCPU();
			break;

		case GPU:
			initGPU();
			break;

		}
	}


	private void initCPU(){
		ceaccepter = new CPUCEA_0(this);
		CPULayerData c = new CPULayerData(this,global);
		layerdata = c;
		cpucanvas = new CPUCanvasPanel(c.getImage());
		view = new CanvasView(width, height, cpucanvas,cpucanvas,this,global);//thisどうしよ
		cpucanvas.setCanvasView(view);
	}

	private void initGPU(){
		//TODO いつの日か実装したいね。
	}


	public boolean paint(PaintEvent e){
		return layerdata.paint(e);
	}

	public void shutDownCEDT(){
		ceaccepter.shutDownCEDT();
	}

	public void dispatchEvent(CanvasEvent e){
		ceaccepter.passEvent(e);
	}

	public PaintEvent subsetEvent(PaintEvent e){
		Rectangle r = e.getBounds();
		Rectangle size = new Rectangle(0,0,width,height);
		Rectangle k = size.intersection(r);
		if(k.isEmpty())return null;
		return e.subsetEvent(k);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Device getDevice() {
		return device;
	}

	public boolean isGPUCanvas(){
		return device==Device.GPU;
	}

	public CanvasView getCanvasView(){
		return view;
	}

	public int getID(){
		return id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String s){
		author = s;
	}

	public long getMakeDay() {
		return makeDay;
	}

	public long getWorkTime() {
		return workTime;
	}

	public long getActionCount() {
		return actionCount;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public String getCanvasName() {
		return canvasname;
	}

	public void setCanvaNname(String canvasname) {
		this.canvasname = canvasname;
	}

	public BufferedImage createSaveImage(){
		return layerdata.createImage();
	}

	public LimitedResource<PixelDataInt> getPixelDataIntBufferResource(){
		return intbuffer;
	}
	public LimitedResource<PixelDataByte> getPixelDataByteBuffereResource(){
		return bytebuffer;
	}

	public void rendering(Rectangle r){
		if(r==null || r.isEmpty())return;
		layerdata.rendering(r);
		view.rendering();
	}

	public void rendering(Rectangle[] rects){
		if(rects==null || rects.length==0)return;
		Rectangle union=rects[0];
		for(int i=1;i<rects.length;i++){
			union = union.union(rects[i]);
		}
		if(union.isEmpty())return;
		if(union.width*union.height < 100){
			layerdata.rendering(union);
		}else{
			for(Rectangle r:rects)layerdata.rendering(r);
		}
		view.renderingFlug(union);
	}

	@Override
	public CanvasHandler getHandler(){
		return new CanvasHandler(this, apainter);
	}


	public LayerHandler[] getAllLayers(){
		return layerdata.getAllLayerHandlers();
	}


	public void dispose(){
		//TODO
		layerdata.dispose();
		history.clear();
		shutDownCEDT();
		global.removeCanvas(this);
		getCanvasView().clear();

		intbuffer.dispose();
		bytebuffer.dispose();


	}



	//履歴操作-------------------------------------------------------
	public void addHistory(HistoryObject historyobj){
		history.addHistory(historyobj);
	}

	public void markGroupHistory(){
		history.markGroup();
	}

	public void finishGroupHistory(){
		history.finishGroup();
	}

	public void redo(){
		history.redo();
	}

	public boolean hasBeforeHistory(){
		return history.hasBeforeHistory();
	}

	public void undo(){
		history.undo();
	}
	public boolean hasNextHistory(){
		return history.hasNextHistory();
	}

	//LayerData操作--------------------------------------------------


	public InnerLayerHandler createNewLayer(String name){
		return layerdata.createLayer(name);
	}

	public InnerLayerHandler getLayer(int id){
		return layerdata.getLayer(id);
	}

	public String getLayerLine(){
		return layerdata.getLayerLine();
	}

	public InnerLayerHandler getSelectedLayer(){
		return layerdata.getSelectedLayer();
	}

	public void setSelectedLayer(int layerid){
		layerdata.setSelectLayer(layerid);
	}

	/**
	 * 選択されている書き込み可能なレイヤー（もしくはマスク）があればそれを返します。
	 */
	public DrawTarget getDrawTarget(){
		return layerdata.getDrawTarget();
	}


	//MouseListener-------------------------------
	public void dispatchEvent(final MouseWheelEvent e){
		if(e==null)return;
		ceaccepter.runInAnyThread(new Runnable() {
			public void run() {
				onScroll(e);
			}
		});
	}
	public void dispatchEvent(final TabletMouseEvent e){
		if(e==null)return;
		ceaccepter.runInAnyThread(new Runnable() {
			public void run() {
				int id=e.getID();
				switch(id){
				case MouseEvent.MOUSE_PRESSED:
					onPressed(e);
					break;
				case MouseEvent.MOUSE_DRAGGED:
					onDragged(e);
					break;
				case MouseEvent.MOUSE_RELEASED:
					onReleased(e);
					break;
				case MouseEvent.MOUSE_MOVED:
					onMove(e);
					break;
				case MouseEvent.MOUSE_ENTERED:
					onEnter(e);
					break;
				case MouseEvent.MOUSE_EXITED:
					onExit(e);
					break;
				case TabletMouseEvent.MOUSE_CURSORTYPECHANGE:
					Object source =e.getSource();
					CursorDevice dev = e.getCursorDevice();
					String newvalue;
					if(dev==CursorDevice.MOUSE){
						newvalue="mouse";
						CanvasMouseListener head =
								global.get(CanvasHeadAction, CanvasMouseListener.class);
						CanvasMouseListener tail =
								global.get(CanvasTailAction, CanvasMouseListener.class);
						if(head!=null&&!head.isSelected()){
							head.selected();
							if(head!=tail)
								tail.unselected();
						}
					}else if(e.isPenPressed()){
						newvalue="head";
						CanvasMouseListener head =
								global.get(CanvasHeadAction, CanvasMouseListener.class);
						CanvasMouseListener tail =
								global.get(CanvasTailAction, CanvasMouseListener.class);
						if(head!=null&&!head.isSelected()){
							head.selected();
							if(head!=tail)
								tail.unselected();
						}
					}else{
						newvalue="tail";
						CanvasMouseListener tail =
								global.get(CanvasTailAction, CanvasMouseListener.class);
						CanvasMouseListener head =
								global.get(CanvasHeadAction, CanvasMouseListener.class);
						if(tail!=null&&!tail.isSelected()){
							tail.selected();
							if(tail!=head)
								head.unselected();
						}
					}
					global.firePropertyChange(CursorTypeChangeProperty, null, newvalue, source);
					break;
				}

			}
		});
	}
	private CanvasMouseListener tailacton,headaction;
	private void onScroll(MouseWheelEvent e){
		CanvasScrollListener scroll = global.get(CanvasWheelAction,CanvasScrollListener.class);
		if(scroll!=null){
			scroll.scroll(e, this);
		}
	}
	private void onPressed(TabletMouseEvent e) {
		headaction=tailacton=null;
		if(e.isPenPressed()){
			Object head = global.get(GlobalKey.CanvasHeadAction);
			if(head!=null && head instanceof CanvasMouseListener)(headaction=(CanvasMouseListener)head).press(e,this);
		}else if(e.isTailPressed()){
			Object tail = global.get(GlobalKey.CanvasTailAction);
			if(tail!=null && tail instanceof CanvasMouseListener)(tailacton=(CanvasMouseListener)tail).press(e,this);
		}
	}

	private void onReleased(TabletMouseEvent e) {
		if(e.isPenPressed()){
			if(headaction!=null)headaction.release(e,this);
		}else if(e.isTailPressed()){
			if(tailacton!=null)tailacton.release(e,this);
		}else if(e.isPopupTrigger()){
			Component c = e.getComponent();
			if(c.contains(e.getPoint())){
				RightReleaseListener l = global.get(
						RightMouseReleaseAction, RightReleaseListener.class);
				if(l!=null){
					l.mouseRightReleased(e, this);
				}
			}
		}
		headaction=tailacton=null;
	}

	private void onDragged(TabletMouseEvent e) {
		if(e.isPenPressed()){
			if(headaction!=null)headaction.drag(e,this);
		}else if(e.isTailPressed()){
			if(tailacton!=null)tailacton.drag(e,this);
		}
	}
	private void onMove(TabletMouseEvent e) {
		if(e.isPenPressed()){
			if(headaction!=null)headaction.move(e,this);
		}else if(e.isTailPressed()){
			if(tailacton!=null)tailacton.move(e,this);
		}
	}

	private void onExit(TabletMouseEvent e) {
		if(e.isPenPressed()){
			if(headaction!=null)headaction.exit(e,this);
		}else if(e.isTailPressed()){
			if(tailacton!=null)tailacton.exit(e,this);
		}
	}

	private void onEnter(TabletMouseEvent e) {
		if(e.isPenPressed()){
			if(headaction!=null)headaction.enter(e,this);
		}else if(e.isTailPressed()){
			if(tailacton!=null)tailacton.enter(e,this);
		}
	}


	private EventListenerList eventlistenerlist = new EventListenerList();
	public void addPropertyChangeListener(PropertyChangeListener l){
		PropertyChangeUtility.addPropertyChangeListener(l, eventlistenerlist);
	}

	public void removePropertyChangeListener(PropertyChangeListener l){
		PropertyChangeUtility.removePropertyChangeListener(l, eventlistenerlist);
	}

	public void firePropertyChange(String name,Object old,Object newobj,Object source){
		PropertyChangeUtility.firePropertyChange(name, old, newobj, source, eventlistenerlist);
	}
}
