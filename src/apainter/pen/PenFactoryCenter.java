package apainter.pen;

import java.util.ArrayList;

public class PenFactoryCenter {

	private ArrayList<PenShapeFactory> factory = new ArrayList<PenShapeFactory>();

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

	public void addPenShapeFactory(PenShapeFactory f){
		if(!factory.contains(f))
			factory.add(f);
	}

	public void removePenShapeFactory(PenShapeFactory f){
		factory.remove(f);
	}
}
