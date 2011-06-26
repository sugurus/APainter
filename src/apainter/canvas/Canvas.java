package apainter.canvas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JComponent;

import apainter.Device;
import apainter.bind.annotation.BindProperty;
import apainter.canvas.event.PainterEvent;
import apainter.canvas.layerdata.CPULayerData;
import apainter.canvas.layerdata.LayerData;
import apainter.gui.canvas.CPUCanvasPanel;
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
	//-----------------------------------------------------------------

	private CanvasThread thread;
	private CanvasView view;

	private CPUCanvasPanel cpucanvas;


	public Canvas(int width,int height,Device device){
		this(width,height,device,null,null,0,0,0);
	}

	public Canvas(int width,int height,Device device,
			String author,String canvasname,long makeDay,long workTime,long actionCount) {
		if(width <=0 || height <= 0)
			throw new IllegalArgumentException(String.format("width:%d,height:%d",width,height));

		//TODO GPU実装がもしいつかできたら外す
		if(device==Device.GPU){
			throw new RuntimeException("GPUデバイスには対応していません");
		}

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
		thread = new CPUThread();
		CPULayerData c = new CPULayerData(this);
		layerdata = c;
		cpucanvas = new CPUCanvasPanel(c.getImage());
		view = new CanvasView(width, height, cpucanvas);
		cpucanvas.setCanvasView(view);
	}

	private void initGPU(){
		//TODO いつの日か実装したいね。
	}



	public void dispatchEvent(PainterEvent e){
		thread.dispatch(e);
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

	//TODO TestMethod
	public JComponent testMethod_getPanel(){
		return layerdata.testMethod_createViewPanel();
	}


	//propertychangelistener-----------------------------------------

	private ArrayList<PropertyChangeListener> propertylistener = new ArrayList<PropertyChangeListener>();

	public void addPropertyChangeListener(
			PropertyChangeListener l) {
		if (!propertylistener.contains(l))
			propertylistener.add(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertylistener.remove(l);
	}

	public void firePropertyChange(
			String name,Object oldValue,Object newValue) {
		PropertyChangeEvent e =
			new PropertyChangeEvent(this,name,oldValue,newValue);
		for (PropertyChangeListener l : propertylistener) {
			l.propertyChange(e);
		}
	}

}
