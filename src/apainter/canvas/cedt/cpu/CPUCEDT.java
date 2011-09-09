package apainter.canvas.cedt.cpu;

import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import apainter.canvas.Canvas;
import apainter.canvas.cedt.CanvasEventDispatchThread;
import apainter.canvas.event.CanvasEvent;
import apainter.canvas.layerdata.InnerLayerHandler;
import apainter.drawer.DrawEvent;
import apainter.drawer.PaintLastEvent;
import apainter.drawer.PaintStartEvent;
import apainter.history.HisotryReduceEvent;
import apainter.history.History;

public class CPUCEDT implements CanvasEventDispatchThread{

	private ExecutorService anyThread,drawThread,historyReduceThread;
	RepaintThread repaint;
	private Canvas canvas;

	public CPUCEDT(Canvas c) {
		canvas = c;
	}

	public void runInAnyThread(Runnable r){
		if(!isRunning())init();
		anyThread.submit(r);
	}


	@Override
	public synchronized void init() {
		if(anyThread==null || anyThread.isShutdown())
			anyThread = Executors.newSingleThreadExecutor();
		if(drawThread==null || drawThread.isShutdown())
			drawThread = Executors.newSingleThreadExecutor();
		if(historyReduceThread==null || historyReduceThread.isShutdown()){
			historyReduceThread =Executors.newSingleThreadExecutor();
		}
		if(repaint==null || !repaint.isRunning())
			repaint = new RepaintThread(1000/60, canvas);
		repaint.start();
	}

	@Override
	public boolean isRunning() {
		return anyThread!=null && !anyThread.isShutdown()&&
		drawThread!=null && !drawThread.isShutdown()&&
		historyReduceThread!=null && !historyReduceThread.isShutdown()&&
		repaint!=null && repaint.isRunning();
	}

	@Override
	public synchronized void shutdown() {
		if(!isRunning())return;
		anyThread.shutdown();
		drawThread.shutdown();
		repaint.stop();
	}

	@Override
	public void dispatch(final CanvasEvent e) {
		if(e==null||e.canvas!=canvas)return;
		Runnable r = new Runnable() {
			public void run() {
				if (e instanceof PaintStartEvent) {
					PaintStartEvent pse = (PaintStartEvent) e;
					InnerLayerHandler h = pse.getTarget();
					h.startPaint(e.getSource());
				}else if (e instanceof PaintLastEvent) {
					submitDrawEnd((PaintLastEvent)e);
				}else if (e instanceof DrawEvent) {
					DrawEvent d = (DrawEvent) e;
					submitDraw(d);
				}else if(e instanceof HisotryReduceEvent){
					HisotryReduceEvent h = (HisotryReduceEvent)e;
					submitReduceHistory(h);
				}
			}
		};
		anyThread.submit(r);
	}

	private void submitDraw(final DrawEvent e){
		drawThread.submit(new Runnable() {
			public void run() {
				draw(e);
			}
		});
	}
	private void submitDrawEnd(final PaintLastEvent pe){
		drawThread.submit(new Runnable() {
			public void run() {
				InnerLayerHandler h = pe.getTarget();
				h.endPaint(pe.getSource());
			}
		});

	}

	private void submitReduceHistory(final HisotryReduceEvent e){
		historyReduceThread.submit(new Runnable() {
			public void run() {
				History h = e.getSource();
				h.tryCompress();
				h.reduceHistory();
			}
		});
	}

	private void draw(final DrawEvent de){
		DrawEvent e = de.canvas.subsetEvent(de);
		if(e ==null)return;

		Rectangle r = e.getBounds();
		int s = CPUParallelWorkThread.getThreadSize();
		if(s==1|| r.width*r.height < 100){
			canvas.paint(e);
		}else{
			Runnable[] runs = new Runnable[s];
			if(r.width%s==0 || !(r.height%s==0 || r.width < r.height)){
				for(int i=0;i<s;i++){
					Rectangle rect = new Rectangle(r.x+(r.width*i/s),r.y,r.width*(i+1)/s-(r.width*i/s),r.height);
					final DrawEvent e2 = e.subsetEvent(rect);
					runs[i]=new Runnable() {
						public void run() {
							canvas.paint(e2);
						}
					};
				}
			}else{
				for(int i=0;i<s;i++){
					Rectangle rect = new Rectangle(r.x,r.y+r.height*i/s,r.width,r.height*(i+1)/s-r.height*i/s);
					final DrawEvent e2 = e.subsetEvent(rect);
					runs[i]=new Runnable() {
						public void run() {
							canvas.paint(e2);
						}
					};
				}
			}
			CPUParallelWorkThread.exec(runs);
		}
		repaint.addJob(r);
	}

}
