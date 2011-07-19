package apainter.bind;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import apainter.bind.annotation.BindProperty;

public class Bind {
	private String propertyName;
	private ArrayList<C> list = new ArrayList<Bind.C>();
	private PropertyChangeListener listner;
	private Object newvalue = new Object();

	public Bind(String propertyname){
		propertyName = propertyname;

		listner = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(propertyName)){
					if(newvalue==null && evt.getNewValue()==null)return;
					if(newvalue!=null&&newvalue.equals(evt.getNewValue()))return;

					newvalue = evt.getNewValue();
					for(C c:list){
						if(c.o!=evt.getSource()){
							c.remove();
							c.call(newvalue);
							c.add();
						}
					}
				}
			}

		};

	}

	public Bind allUnbind(){
		for(C c:list){
			c.remove();
		}
		list.clear();
		return this;
	}


	public Bind unbind(Object o){
		for(C c:list){
			if(c.o == o){
				list.remove(c);
				c.remove();
				return this;
			}
		}
		return this;
	}
	private void add(C c){
		for(C cc:list){
			if(cc.o==c.o)return;
		}
		list.add(c);
	}


	public Bind bind(Object o,String bindFunction,Class<?>... parameterTypes){
		Class<?> clasz = o.getClass();
		Method add,remove;
		try {
			add = clasz.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
			remove = clasz.getMethod("removePropertyChangeListener", PropertyChangeListener.class);
			Method me = clasz.getMethod(bindFunction, parameterTypes);
			C c = new C();
			c.m = me;
			c.o = o;
			c.remove=remove;
			c.add= add;
			c.add();
			add(c);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return this;
	}

	public Bind bind(Object o){
		Class<?> clasz = o.getClass();
		Method add,remove;
		try {
			add = clasz.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
			remove = clasz.getMethod("removePropertyChangeListener", PropertyChangeListener.class);
			Method[] ms = clasz.getMethods();
			Label:for(Method me:ms){
				Annotation[] a = me.getAnnotations();
				for(Annotation an:a){
					if(an.annotationType() == BindProperty.class){
						String[] s = ((BindProperty)an).value();
						for(String str:s){
							if(str.equals(propertyName)){
								C c = new C();
								c.m = me;
								c.o = o;
								c.remove=remove;
								c.add= add;
								c.add();
								add(c);
								break Label;
							}
						}
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return this;
	}



	private class C{
		Object o;
		Method m;
		Method add;
		Method remove;
		void call(Object newval){
			try {
				m.invoke(o, newval);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		void add(){
			try {
				add.invoke(o, listner);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		void remove(){
			try {
				remove.invoke(o, listner);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
