package apainter.canvas;

import static javax.swing.SwingConstants.*;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.Timer;

import apainter.GlobalValue;
import apainter.canvas.event.PainterEvent;
import apainter.drawer.DrawEvent;

class CPUThread extends CanvasThread{

	private int paintThreadSize;
	private ExecutorService paintThread,historyThread,anyThread;
	private Timer renderingThread;
	private ArrayList<Rectangle> renderingBounds = new ArrayList<Rectangle>();
	private ActionListener rendering=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Rectangle k;
			synchronized (renderingBounds) {
				if(renderingBounds.isEmpty())return;
				//TODO 複合長方形領域の最小分割をすべき。
				//現在の所面倒なので全てを内包する最小の長方形でレンダリング中。
				Rectangle r = renderingBounds.get(0);
				for(int i=1,l=renderingBounds.size();i<l;i++){
					r = r.union(renderingBounds.get(i));
				}
				k = r;
				renderingBounds.clear();
			}
			canvas.rendering(k);
		}
	};

	public void repaint(Rectangle r){
		if(r==null||r.isEmpty())return;
		synchronized (renderingBounds) {
			renderingBounds.add(r);
		}
	}

	public CPUThread(GlobalValue global,Canvas canvas) {
		super(global,canvas);
		init();
	}

	@Override
	public void init() {
		paintThreadSize=Runtime.getRuntime().availableProcessors();
		paintThread = Executors.newFixedThreadPool(paintThreadSize);
		historyThread = Executors.newSingleThreadExecutor();
		anyThread = Executors.newSingleThreadExecutor();
		renderingThread = new Timer(1000/60, rendering);
		renderingThread.start();
	}
	@Override
	public void shutdown() {
		paintThread.shutdown();
		historyThread.shutdown();
		anyThread.shutdown();
		renderingThread.stop();
	}


	@Override
	public synchronized void dispatch(PainterEvent e) {
		// TODO CPUThreadのディスパッチを作成
		if(e==null)return;

		anyThread.submit(new AnyRunnable(e));
	}


	private void dispatch(DrawEvent e){
		e = e.getTarget().getCanvas().subsetEvent(e);
		if(e==null)return;

		Rectangle r = e.getBounds();
		PaintBlock:{
			if(paintThreadSize==1 || r.width*r.height <= paintThreadSize*5){
				Future<?> f=_dispatch(e);
				try {
					f.get();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}

				break PaintBlock;
			}

			Future<?>[] f = new Future<?>[paintThreadSize];
			if(r.width%paintThreadSize==0 || !(r.height%paintThreadSize==0 || r.width < r.height)){
				int x=r.x;
				int w = r.width/paintThreadSize;
				for(int i=0;i<paintThreadSize;i++){
					Rectangle rect = new Rectangle(x,r.y,w,r.height);
					x+=w;
					DrawEvent e2 = e.subsetEvent(rect);
					f[i]=_dispatch(e2);
				}
			}else{
				int y = r.y;
				int h = r.height/paintThreadSize;
				for(int i=0;i<paintThreadSize;i++){
					Rectangle rect = new Rectangle(r.x,y,r.width,h);
					y+=h;
					DrawEvent e2 = e.subsetEvent(rect);
					f[i]=_dispatch(e2);
				}
			}

			for(Future<?> fu:f){
				try {
					fu.get();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
			}
		}//END PaintBlock
		repaint(r);

		//TODO Hisotry
	}


	private Future<?> _dispatch(DrawEvent e){
		return paintThread.submit(new PaintRunnable(e));
	}

	private class AnyRunnable implements Runnable{
		PainterEvent e;
		public AnyRunnable(PainterEvent e) {
			this.e = e;
		}
		@Override
		public void run() {
			if (e instanceof DrawEvent) {
				DrawEvent d = (DrawEvent) e;
				dispatch(d);
			}
		}

	}

	private class PaintRunnable implements Runnable{
		DrawEvent e;
		public PaintRunnable(DrawEvent e) {
			this.e = e;
		}

		public void run(){
			canvas.paint(e);
		}
	}


}
