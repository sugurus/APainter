package apainter.hierarchy;

public class Element<E>{

	final E property;
	protected Unit<E> parent;
	final Hierarchy<E> hierachy;


	Element(Hierarchy<E> h) {
		property = null;
		hierachy = h;
	}

	Element(E property,Hierarchy<E> h) {
		if(property==null)throw new NullPointerException("property");
		this.property = property;
		hierachy = h;
	}


	public int getIndex(){
		return parent.elements.indexOf(this);
	}

	public E getProperty() {
		return property;
	}

	void setUnit(Unit<E> u) {
		parent = u;
	}

	public Unit<E> getUnit() {
		return parent;
	}

	public boolean isUnit() {
		return false;
	}

	public String toString() {
		return parent.toString()+"/"+property.toString();
	}


}
