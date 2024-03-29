package apainter.canvas.cedt.cpu;

import java.awt.Rectangle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import apainter.canvas.Canvas;
import apainter.canvas.cedt.CanvasEventDispatchThread;
import apainter.canvas.event.CanvasEvent;
import apainter.canvas.event.PaintEvent;
import apainter.canvas.event.PaintEventAccepter;
import apainter.canvas.event.PaintLastEvent;
import apainter.canvas.event.PaintStartEvent;
import apainter.history.HisotryReduceEvent;
import apainter.history.History;
import apainter.history.RedoEvent;
import apainter.history.UndoEvent;

public class CPUCEDT implements CanvasEventDispatchThread{

	private ExecutorService anyThread,drawThread,historyReduceThread;
	private Canvas canvas;
	private boolean lockdraw=false;

	private synchronized void lockDraw(){
		lockdraw = true;
	}
	private synchronized boolean isLockedDraw(){
		return lockdraw;
	}
	private synchronized void unlockDraw(){
		lockdraw = false;
	}

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
		RepaintThread.addCanvas(canvas);
	}

	@Override
	public boolean isRunning() {
		return anyThread!=null && !anyThread.isShutdown()&&
		drawThread!=null && !drawThread.isShutdown()&&
		historyReduceThread!=null && !historyReduceThread.isShutdown();
	}

	@Override
	public synchronized void shutdown() {
		if(!isRunning())return;
		anyThread.shutdown();
		drawThread.shutdown();
		historyReduceThread.shutdown();
		RepaintThread.removeCanvas(canvas);
	}

	@Override
	public void dispatch(final CanvasEvent e) {
		if(e==null||e.canvas!=canvas)return;
		Runnable r = new Runnable() {
			public void run() {
				if (e instanceof PaintStartEvent) {
					lockDraw();
					PaintStartEvent pse = (PaintStartEvent) e;
					PaintEventAccepter h = pse.getTarget();
					h.startPaint(e.getSource());
				}else if (e instanceof PaintLastEvent) {
					submitDrawEnd((PaintLastEvent)e);
				}else if (e instanceof PaintEvent) {
					PaintEvent d = (PaintEvent) e;
					submitDraw(d);
				}else if(e instanceof HisotryReduceEvent){
					HisotryReduceEvent h = (HisotryReduceEvent)e;
					submitReduceHistory(h);
				}else if(e instanceof UndoEvent){
					UndoEvent ue =(UndoEvent)e;
					submitUndo(ue);
				}else if(e instanceof RedoEvent){
					submitRedo((RedoEvent)e);
				}
			}
		};
		anyThread.submit(r);
	}
	private void submitRedo(final RedoEvent e){
		drawThread.submit(new Runnable() {
			public void run() {
				synchronized (CPUCEDT.this) {
					if(isLockedDraw()){
						return;
					}
					e.redo();
				}
			}
		});
	}
	private void submitUndo(final UndoEvent e){
		drawThread.submit(new Runnable() {
			public void run() {
				synchronized (CPUCEDT.this) {
					if(isLockedDraw()){
						return;
					}
					e.undo();
				}
			}
		});
	}

	private void submitDraw(final PaintEvent e){
		drawThread.submit(new Runnable() {
			public void run() {
				draw(e);
			}
		});
	}
	private void submitDrawEnd(final PaintLastEvent pe){
		drawThread.submit(new Runnable() {
			public void run() {
				PaintEventAccepter h = pe.getTarget();
				h.endPaint(pe.getSource());
				unlockDraw();
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

	private void draw(final PaintEvent de){
		final PaintEvent e = de.canvas.subsetEvent(de);
		if(e ==null)return;

		final Rectangle r = e.getBounds();
		int s = CPUParallelWorkThread.getThreadSize();
		if(s==1|| r.width*r.height < 100){//ある程度小さい場合は分割しない方が速い
			canvas.paint(e);
		}else{
			if(r.width%s==0 || !(r.height%s==0 || r.width < r.height)){
				new CPUParallelWorker() {
					protected void task(int id, int size) {
						Rectangle rect = new Rectangle(r.x+(r.width*id/size),r.y,
								r.width*(id+1)/size-(r.width*id/size),
								r.height);
						if(rect.isEmpty())return;
						PaintEvent e2 = e.subsetEvent(rect);
						canvas.paint(e2);
					}
				}.start();
				
			}else{
				new CPUParallelWorker() {
					protected void task(int id, int size) {
						Rectangle rect = new Rectangle(r.x,r.y+r.height*id/size,
								r.width,r.height*(id+1)/size-r.height*id/size);
						PaintEvent e2 = e.subsetEvent(rect);
						canvas.paint(e2);
					}
				}.start();
			}
		}
		RepaintThread.addRepaintJob(r, canvas);
	}

}
