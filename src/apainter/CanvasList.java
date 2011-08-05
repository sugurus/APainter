package apainter;

import java.util.Vector;

import apainter.canvas.Canvas;

public class CanvasList extends Vector<Canvas>{

	public Canvas getCanvas(int id){
		for(Canvas c:this){
			if(id==c.getID()){
				return c;
			}
		}
		return null;
	}

	private boolean cheack(Canvas ca){
		int id = ca.getID();
		for(Canvas c:this){
			if(id==c.getID()){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean add(Canvas e) {
		if(cheack(e))return false;
		return super.add(e);
	}

	@Override
	public void add(int index, Canvas element) {
		if(cheack(element))return;
		super.add(index, element);
	}


}
