package apainter.canvas.cedt.cpu;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import apainter.canvas.Canvas;
import apainter.misc.UnionRectangle;

public class RepaintThread extends Timer{

	private static ActionListener lis = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			RepaintThread t = (RepaintThread) e.getSource();
			if(t.haveJob()){
				t.exec();
			}
		}
	};

	private ArrayList<Rectangle> bounds
		= new ArrayList<Rectangle>(30);
	private Canvas canvas;

	public RepaintThread(int delay,Canvas c) {
		super(delay, lis);
		canvas = c;
	}

	public void addJob(Rectangle r){
		if(r==null||r.isEmpty())return;
		synchronized (bounds) {
			bounds.add(r);
		}
	}


	private boolean haveJob(){
		return !bounds.isEmpty();
	}

	private void exec(){
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
