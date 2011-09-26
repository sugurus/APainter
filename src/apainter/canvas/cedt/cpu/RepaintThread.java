package apainter.canvas.cedt.cpu;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import apainter.canvas.Canvas;
import apainter.misc.UnionRectangle;

public class RepaintThread{

	private Timer timer;
	private TimerTask t;


	private HashMap<Canvas, ArrayList<Rectangle>> map = new HashMap<Canvas, ArrayList<Rectangle>>();

	private static RepaintThread thread;

	private static synchronized RepaintThread getRepaintThread(){
		if(thread == null){
			thread = new RepaintThread();
		}
		return thread;
	}

	public static synchronized void addCanvas(Canvas canvas){
		RepaintThread rt  = getRepaintThread();
		if(rt.map.get(canvas)!=null){
			return;
		}else{
			rt.map.put(canvas, new ArrayList<Rectangle>());
			if(!rt.isRunning())rt.start();
		}
	}

	public static synchronized void removeCanvas(Canvas canvas){
		RepaintThread rt  = getRepaintThread();
		rt.map.remove(canvas);
		if(rt.map.isEmpty()){
			rt.stop();
		}
	}

	public static void addRepaintJob(Rectangle r,Canvas canvas){
		getRepaintThread().addJob(r, canvas);
	}

	private void addJob(Rectangle r,Canvas canvas){
		if(r==null||r.isEmpty())return;
		ArrayList<Rectangle> bounds=map.get(canvas);
		if(bounds==null){
			return;
		}

		synchronized (bounds) {
			bounds.add(r);
		}
	}

	public synchronized void start(){
		if(timer!=null)return;
		timer = new Timer();
		t = new TimerTask() {

			@Override
			public void run() {
				if(haveJob()){
					exec();
				}
			}
		};
		timer.scheduleAtFixedRate(t, 0, 1000/60);
	}

	private synchronized void stop(){
		if(t==null)return;
		t.cancel();
		t=null;
		timer.cancel();
		timer = null;
	}

	private boolean isRunning(){
		return timer!=null;
	}


	private boolean haveJob(){
		if(map.isEmpty())return false;
		for(Canvas canvas:map.keySet()){
			ArrayList<Rectangle> bounds = map.get(canvas);
			if(!bounds.isEmpty())return true;
		}
		return false;
	}

	private void exec(){
		for(Canvas canvas:map.keySet()){
			ArrayList<Rectangle> bounds = map.get(canvas);
			exec(bounds,canvas);
		}
	}

	private void exec(ArrayList<Rectangle> bounds,Canvas canvas){
		Rectangle[] k;
		synchronized (bounds) {
			if(bounds.isEmpty())return;
			k = bounds.toArray(new Rectangle[bounds.size()]);
			bounds.clear();
		}
		UnionRectangle u = new UnionRectangle();
		u.add(k);
		if(u.isEmpty())return;
		k = u.divide();
		canvas.rendering(k);
	}
}
