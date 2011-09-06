package apainter.misc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

public class PropertyChangeUtility {

	public static void addPropertyChangeListener(PropertyChangeListener l,
			EventListenerList eventlistenerlist) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
		eventlistenerlist.add(PropertyChangeListener.class, l);
	}

	public static void removePropertyChangeListener(PropertyChangeListener l,
			EventListenerList eventlistenerlist) {
		eventlistenerlist.remove(PropertyChangeListener.class, l);
	}

	public static void firePropertyChange(String name, Object oldValue, Object newValue,
			Object source,EventListenerList eventlistenerlist) {
		PropertyChangeEvent e = new PropertyChangeEvent(source, name, oldValue,
				newValue);

		Object[] listeners = eventlistenerlist.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}
}
