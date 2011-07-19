package apainter.hierarchy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.EventListenerList;

public class Hierarchy<E> {

	private final ArrayList<HierarchyListener<E>> listener  = new ArrayList<HierarchyListener<E>>();
	private final TopLevel<E> toplevel =new TopLevel<E>(this);
	private final ArrayList<Element<E>> elements = new ArrayList<Element<E>>();
	private Unit<E> currentunit=toplevel;

	public Collection<Element<E>> getElements(){
		return toplevel.getElements();
	}

	@SuppressWarnings("unchecked")
	public Collection<Element<E>> getAllElements(){
		return (ArrayList<Element<E>>)elements.clone();
	}



	public void addListener(HierarchyListener<E> l){
		if(!listener.contains(l))
			listener.add(l);
	}

	public void removeListener(HierarchyListener<E> l){
		listener.remove(l);
	}

	public boolean containListener(HierarchyListener<E> l){
		return listener.contains(l);
	}

	private void _move(Element<E> e){
		for(HierarchyListener<E> l:listener){
			l.moved(e);
		}
	}

	private void _delete(Element<E> e,Collection<Element<E>> childElements){
		for(HierarchyListener<E> l:listener){
			l.deleted(e, childElements);
		}
	}
	private void _readd(Element<E> e,Collection<Element<E>> childElements){
		for(HierarchyListener<E> l:listener){
			l.readd(e, childElements);
		}
	}

	private void _create(Element<E> e){
		for(HierarchyListener<E> l:listener){
			l.created(e);
		}
	}

	/**
	 * 要素を追加するときは必ずここを利用します<br>
	 * ここをオーバーライドすれば、サブクラスは追加される要素を全て取得できます。<br>
	 * かならず、super.addを呼び出してください
	 * @param e
	 */
	protected void add(Element<E> e){
		elements.add(e);
	}

	protected void addAll(Collection<Element<E>> e){
		elements.addAll(e);
	}

	protected void remove(Element<E> e){
		elements.remove(e);
	}

	protected void removeAll(Collection<Element<E>> e){
		elements.removeAll(e);
	}

	public Element<E> addElement(E property){
		Element<E> e = new Element<E>(property, this);
		add(e);
		toplevel.appendElement(e);
		_create(e);
		return e;
	}

	public Unit<E> addUnit(E property){
		Unit<E> e = new Unit<E>(property, this);
		add(e);
		toplevel.appendElement(e);
		_create(e);
		return e;
	}

	public Element<E> addElementToCurrentUnit(E property){
		Element<E> e = new Element<E>(property, this);
		add(e);
		currentunit.appendElement(e);
		_create(e);
		return e;
	}

	public Unit<E> addUnitToCurrentUnit(E property){
		Unit<E> e = new Unit<E>(property, this);
		add(e);
		currentunit.appendElement(e);
		_create(e);
		return e;
	}

	public boolean setCurrentUnit(Unit<E> u){
		if(contains(u)){
			currentunit = u;
			return true;
		}
		return false;
	}

	public Unit<E> getCurrentUnit(){
		return currentunit;
	}

	public void removeElement(Element<E> e){
		if(contains(e)){
			e.getUnit().removeElement(e);
			remove(e);
			ArrayList<Element<E>> lis = new ArrayList<Element<E>>();
			if(e.isUnit()){
				ArrayList<Element<E>> es = ((Unit<E>)e).elements;
				lis.addAll(es);
				for(Element<E> ee:es){
					if(e.isUnit())unitAdd(lis, (Unit<E>)ee);
				}
				removeAll(lis);
			}
			_delete(e,lis);
		}
	}

	private void unitAdd(ArrayList<Element<E>> arr,Unit<E> u){
		arr.addAll(u.elements);
		for(Element<E> e:u.elements){
			if(e.isUnit())unitAdd(arr, (Unit<E>)e);
		}
	}

	public boolean reAddElement(Element<E> e){
		if(!contains(e)&&e.getUnit()==null){
			add(e);
			toplevel.appendElement(e);
			ArrayList<Element<E>> lis = new ArrayList<Element<E>>();
			if(e.isUnit()){
				ArrayList<Element<E>> es = ((Unit<E>)e).elements;
				lis.addAll(es);
				for(Element<E> ee:es){
					if(e.isUnit())unitAdd(lis, (Unit<E>)ee);
				}
				addAll(lis);
			}
			_readd(e,lis);
			return true;
		}
		return false;
	}


	public void moveToNext(Element<E> e,Element<E> before){
		if(containsAll(e,before)){
			Unit<E> u = before.getUnit(),eu= e.getUnit();
			if(eu==u){
				u.removeElement(e);
				int i = before.getIndex();
				u.move(e, i+1);
			}else{
				eu.moveTo(e, u);
				int i = before.getIndex();
				u.move(e, i+1);
			}
			_move(e);
		}
	}

	public void moveToBefore(Element<E> e,Element<E> next){
		if(containsAll(e,next)){
			Unit<E> u = next.getUnit(),eu= e.getUnit();
			if(eu==u){
				u.removeElement(e);
				int i = next.getIndex();
				u.move(e, i);
			}else{
				eu.moveTo(e, u);
				int i = next.getIndex();
				u.move(e, i);
			}
			_move(e);
		}
	}

	public void moveIntoTop(Element<E> e,Unit<E> u){
		if(u==null){
			if(contains(e)){
				Unit<E> ue = e.getUnit();
				if(toplevel == ue){
					toplevel.move(e, 0);
				}else{
					ue.moveTo(e, toplevel);
					toplevel.move(e,0);
				}
				_move(e);
			}
		}else if(containsAll(e,u)){
			Unit<E> ue = e.getUnit();
			if(u == ue){
				u.move(e, 0);
			}else{
				ue.moveTo(e, u);
				u.move(e,0);
			}
			_move(e);
		}
	}



	public void moveIntoLast(Element<E> e,Unit<E> u){
		if(u==null){
			if(contains(e)){
				Unit<E> ue = e.getUnit();
				if(toplevel==ue){
					toplevel.move(e,toplevel.size()-1);
				}else{
					ue.moveTo(e, toplevel);
					toplevel.move(e,toplevel.size()-1);
				}
				_move(e);
			}
		}else if(containsAll(e,u)){
			Unit<E> ue = e.getUnit();
			if(u==ue){
				u.move(e,u.size()-1);
			}else{
				ue.moveTo(e, u);
				u.move(e,u.size()-1);
			}
			_move(e);
		}
	}


	public Unit<E> getTopLevelUnit(){
		return toplevel;
	}

	public boolean contains(Element<E> e){
		return elements.contains(e);
	}


	public boolean contains(E property){
		for(Element<E> e:elements){
			if(e.getProperty() == property){
				return true;
			}
		}
		return false;
	}

	private boolean containsAll(Element<?>... e){
		for(Element<?> ee:e){
			if(!elements.contains(ee))return false;
		}
		return true;
	}



	private EventListenerList eventlistenerlist = new EventListenerList();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
		eventlistenerlist.add(PropertyChangeListener.class, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
	}

	public void firePropertyChange(String name, Object oldValue, Object newValue) {
		PropertyChangeEvent e = new PropertyChangeEvent(this, name, oldValue,
				newValue);

		Object[] listeners = eventlistenerlist.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}


}
