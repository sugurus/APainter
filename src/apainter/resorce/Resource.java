package apainter.resorce;

public class Resource<E>{
	final LimitedResource<E> lr;
	
	Resource(LimitedResource<E> l){
		lr = l;
	}
	
	
	int pos;
	boolean getLock(int i){
		if(lr.locked[i].lock(this)){
			pos = i;
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public E getResource(){
		return (E) lr.locked[pos].getResource(this);
	}
	
	public void unlock(){
		lr.locked[pos].release(this);
	}
}