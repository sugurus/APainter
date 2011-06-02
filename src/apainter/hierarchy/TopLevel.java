package apainter.hierarchy;


class TopLevel<E> extends Unit<E>{

	TopLevel(Hierarchy<E> h) {
		super(h);
	}

	@Override
	public String toString() {
		return "";
	}

	@Deprecated
	public void setUnit(Unit<E> u) {
		if(u!=null)throw new RuntimeException("this is Toplevel Unit!");
	}

}
