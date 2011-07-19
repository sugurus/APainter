package apainter.pen;

import java.net.URL;
import java.util.ArrayList;

import apainter.pen.impl.TestPenFactory;
import apainter.pen.impl.URLPenFactory;

public class PenFactoryCenter {

	private ArrayList<PenShapeFactory> factory = new ArrayList<PenShapeFactory>();

	private long nextid=0;

	public PenFactoryCenter() {
		PenShapeFactory f;
		f = new TestPenFactory(id());
		factory.add(f);

	}


	public String[] getAllPenShapeFacotryName(){
		String[] s = new String[factory.size()];
		int i=0;
		for(PenShapeFactory f:factory){
			s[i++] = f.getPenName();
		}
		return s;
	}

	public PenShapeFactory getPenShapeFactory(long id){
		for(PenShapeFactory f:factory){
			if(f.getID()==id)return f;
		}
		return null;
	}

	public PenShapeFactory getPenShapeFactory(String name){
		for(PenShapeFactory f:factory){
			if(f.getPenName().equals(name))return f;
		}
		return null;
	}

	public PenShapeFactory createNewPenShapeFactory(URL penshapeFile){
		PenShapeFactory f = new URLPenFactory(id(), penshapeFile);
		factory.add(f);
		return f;
	}

	private synchronized long id(){
		return nextid++;
	}

}
