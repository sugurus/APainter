package apainter.canvas;

import static apainter.misc.Util.*;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.APainter;
import apainter.Device;
import apainter.GlobalKey;
import apainter.GlobalValue;
import apainter.bind.annotation.BindProperty;
import apainter.canvas.cedt.CanvasEventAccepter;
import apainter.canvas.cedt.cpu.CPUCEA_0;
import apainter.canvas.event.CanvasEvent;
import apainter.canvas.layerdata.CPULayerData;
import apainter.canvas.layerdata.InnerLayerHandler;
import apainter.canvas.layerdata.LayerData;
import apainter.drawer.DrawEvent;
import apainter.gui.canvas.CPUCanvasPanel;
import apainter.gui.canvas.CanvasMouseListener;
import apainter.gui.canvas.CanvasView;

public class Canvas {

	public static final String
		authorNameChangeProperty ="author",
		canvasNameChangeProperty="canvasName";

	//property---------------------------------------------------------
	private int width,height;
	private Device device;
	private String author,canvasname;
	private long makeDay,workTime,actionCount;
	private long createdTime;//このCanvasが作成された時間
	private LayerData layerdata;
	private int id;
	//-----------------------------------------------------------------


	private GlobalValue global;
	private CanvasEventAccepter ceaccepter;
	private CanvasView view;
	private APainter apainter;


	private CPUCanvasPanel cpucanvas;


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
		id = canvasid;
		createdTime = System.currentTimeMillis();

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

	public boolean paint(DrawEvent e){
		return layerdata.paint(e);
	}

	public void shutDownCEDT(){
		ceaccepter.shutDownCEDT();
	}

	public void dispatchEvent(CanvasEvent e){
		ceaccepter.passEvent(e);
	}

	public DrawEvent subsetEvent(DrawEvent e){
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

	@BindProperty(authorNameChangeProperty)
	public void setAuthor(String s){
		String old = author;
		if(s!=null)author = s;
		else author = "";
		if(!old.equals(author)){
			firePropertyChange(authorNameChangeProperty, old, author);
		}
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

	@BindProperty(canvasNameChangeProperty)
	public void setCanvaNname(String canvasname) {
		String old = this.canvasname;
		if(canvasname!=null)this.canvasname = canvasname;
		else this.canvasname = "";
		if(!old.equals(this.canvasname)){
			firePropertyChange(canvasNameChangeProperty, old, this.canvasname);
		}
	}

	//FIXME TestMethod
	public JComponent testMethod_getPanel(){
		return layerdata.testMethod_createViewPanel();
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


	public CanvasHandler getCanvasHandler(){
		return new CanvasHandler(this, apainter);
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


	//MouseListener-------------------------------
	public synchronized void dispatchEvent(final PenTabletMouseEvent e){
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
				}

			}
		});
	}
	private CanvasMouseListener t,h;
	private void onPressed(PenTabletMouseEvent e) {
		h=t=null;
		switch(e.getButtonType()){
		case HEAD:
		case BUTTON1:
			Object head = global.get(GlobalKey.CanvasHeadAction);
			if(head!=null && head instanceof CanvasMouseListener)(h=(CanvasMouseListener)head).press(e,this);
			break;
		case TAIL:
			Object tail = global.get(GlobalKey.CanvasTailAction);
			if(tail!=null && tail instanceof CanvasMouseListener)(t=(CanvasMouseListener)tail).press(e,this);
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

	private void onReleased(PenTabletMouseEvent e) {
		switch(e.getButtonType()){
		case HEAD:
		case BUTTON1:
			if(h!=null)h.release(e,this);
			break;
		case TAIL:
			if(t!=null)t.release(e,this);
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
	private void onDragged(PenTabletMouseEvent e) {
		switch(e.getButtonType()){
		case HEAD:
		case BUTTON1:
			if(h!=null)h.drag(e,this);
			break;
		case TAIL:
			if(t!=null)t.drag(e,this);
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
	private void onMove(PenTabletMouseEvent e) {
		//TODO move
	}

	private void onExit(PenTabletMouseEvent e) {
		// TODO exit
	}

	private void onEnter(PenTabletMouseEvent e) {
		// TODO enter
	}


	//propertychangelistener-----------------------------------------


	private EventListenerList eventlistenerlist = new EventListenerList();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
		eventlistenerlist.add(PropertyChangeListener.class, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
	}

	public void firePropertyChange(String name, Object oldValue, Object newValue) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue,
				newValue);

		Object[] listeners = eventlistenerlist.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}

}
