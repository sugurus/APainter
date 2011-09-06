package apainter.misc;

import static apainter.misc.Util.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PropertyChangeHolder implements PropertyChangeListener{
	private PropertyChangeListener listener;
	private String propertyName;


	public PropertyChangeHolder(PropertyChangeListener l,String name) {
		listener = nullCheack(l, "PropertyChangeListener is null!");
		propertyName = nullCheack(name,"propertyName is null!");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String n = evt.getPropertyName();
		if(propertyName.equals(n)){
			listener.propertyChange(evt);
		}
	}



}
