package apainter.hierarchy;

import java.util.Collection;

public interface HierarchyListener<E> {
	void moved(Element<E> e);
	void deleted(Element<E> e,Collection<Element<E>> childElements);
	void created(Element<E> e);
	void readd(Element<E> e,Collection<Element<E>> childElements);
}
