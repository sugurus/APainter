package apainter.canvas.cedt.cpu;

import java.awt.Rectangle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import apainter.canvas.Canvas;
import apainter.canvas.cedt.CanvasEventDispatchThread;
import apainter.canvas.event.CanvasEvent;
import apainter.drawer.DrawEvent;

public class CPUCEDT implements CanvasEventDispatchThread{

	private ExecutorService anyThread,drawThread;
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
		if(repaint==null || !repaint.isRunning())
			repaint = new RepaintThread(1000/60, canvas);
		repaint.start();
	}

	@Override
	public boolean isRunning() {
		return anyThread!=null && !anyThread.isShutdown()&&
		drawThread!=null && !drawThread.isShutdown()&&
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
				if (e instanceof DrawEvent) {
					DrawEvent d = (DrawEvent) e;
					submitDraw(d);
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

	private void draw(DrawEvent de){
		DrawEvent e = de.canvas.subsetEvent(de);
		if(e ==null)return;

		Rectangle r = e.getBounds();
		int s = CPUParallelWorkThread.getThreadSize();
		if(s==1|| r.width*r.height < 100){
			canvas.paint(e);
		}else{
			Runnable[] runs = new Runnable[s];
			if(r.width%s==0 || !(r.height%s==0 || r.width < r.height)){
				int x=r.x;
				int w = r.width/s;
				for(int i=0;i<s;i++){
					Rectangle rect = new Rectangle(x,r.y,w,r.height);
					x+=w;
					final DrawEvent e2 = e.subsetEvent(rect);
					runs[i]=new Runnable() {
						public void run() {
							canvas.paint(e2);
						}
					};
				}
			}else{
				int y = r.y;
				int h = r.height/s;
				for(int i=0;i<s;i++){
					Rectangle rect = new Rectangle(r.x,y,r.width,h);
					y+=h;
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
		//TODO HistoryEventを投げる

	}

}
