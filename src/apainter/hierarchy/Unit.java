package apainter.hierarchy;

import java.util.ArrayList;
import java.util.Collection;

public class Unit<E> extends Element<E>{
	final ArrayList<Element<E>> elements = new ArrayList<Element<E>>();

	Unit(Hierarchy<E> h) {super(h);}
	Unit(E property,Hierarchy<E> h) {
		super(property,h);
	}


	public int size() {
		return elements.size();
	}

	public Element<E> getElement(int pos) {
		if(pos >= size())throw new OutOfUnitSizeIndexException(pos,size());
		return elements.get(pos);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Element<E>> getElements() {
		return (ArrayList<Element<E>>)elements.clone();
	}

	public boolean contain(Element<E> e) {
		return elements.contains(e);
	}

	void moveTo(Element<E> child, Unit<E> to) {
		if(!contain(child))throw new NotContainElementException(child);
		to.acceptElement(child);
		elements.remove(child);
	}

	void addCheck(Element<?> e)throws AddParentException,ContainElementException{
		if(elements.contains(e))throw new ContainElementException(e);
		Unit<?> p = parent;
		while(p!=null){
			if(p == e)throw new AddParentException(p);
			p = p.getUnit();
		}
	}

	private void acceptElement(Element<E> e) {
		addCheck(e);
		elements.add(e);
		e.setUnit(this);
	}

	void appendElement(Element<E> e) {
		Unit<E> u = e.getUnit();
		if(u==this) return;
		addCheck(e);
		elements.add(e);
		if(u!=null){
			u.removeElement(e);
		}
		e.setUnit(this);
	}

	void addElement(Element<E> e, int pos) {
		Unit<E> u = e.getUnit();
		addCheck(e);
		if(pos > size())pos = size();
		else if(pos < 0)pos = 0;
		elements.add(pos, e);
		if(u!=null){
			u.removeElement(e);
		}
		e.setUnit(this);
	}

	void removeElement(Element<E> e) {
		elements.remove(e);
		e.setUnit(null);
	}

	@Override
	public boolean isUnit() {
		return true;
	}

	void move(Element<E> e,int pos){
		if(!elements.contains(e))throw new NotContainElementException(e);
		elements.remove(e);
		if(size()<pos)pos = size();
		else if(pos < 0)pos = 0;
		elements.add(pos,e);
	}

}

