package apainter;

import static apainter.GlobalBindKey.*;
import static apainter.GlobalKey.*;
import static apainter.misc.Util.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import apainter.bind.Bind;
import apainter.canvas.Canvas;
import apainter.color.Color;
/**
 *初期化スレッド以外（AWTスレッドなど）からのインスタンスの取得はできないので、<br>
 *必要ならば、mainスレッドから呼ばれる初期化時に取得しておくこと。
 *
 */
public class GlobalValue extends HashMap<Object, Object>{

	private Color front,back;
	private Properties property;



	public Color getFrontColor(){
		return front;
	}

	public Color getBackColor(){
		return back;
	}

	public Bind getBind(GlobalBindKey key){
		return (Bind)get(key);
	}

	public void bind(GlobalBindKey key,Object bindtarget){
		Bind b = getBind(key);
		b.bind(bindtarget);
	}

	public void addCanvas(Canvas canvas){
		CanvasList l = (CanvasList)get(CanvasList);
		if(!l.contains(canvas))l.add(canvas);
	}

	@SuppressWarnings("unchecked")
	public <E> E get(Object key,Class<E> claz){
		Object o = get(key);
		if(o!=null && claz.isAssignableFrom(o.getClass())){
			return (E)o;
		}
		return null;
	}

	@Override
	public Object put(Object key, Object value) {
		if((key instanceof GlobalBindKey && !((GlobalBindKey)key).change) ||
		(key instanceof GlobalKey && !((GlobalKey)key).change)){
			Object o = get(key);
			if(o!=null){
				System.err.println("already exist key!"+key.toString());
				return null;
			}else{
				return super.put(key, value);
			}
		}
		Object old= super.put(key, value);
		firePropertyChange(key.toString(), old, value);
		return old;
	}

	public GlobalValue(Properties pro) {
		property = nullCheack(pro);
		front = new Color(0xff000000);
		back = new Color(0xffffffff);
		super.put(FrontColor, front);
		super.put(BackColor,back);
		Bind f = new Bind(Color.propertyColorChange),
		f16 = new Bind(Color.propertyColorChangeLong),
		b = new Bind(Color.propertyColorChange),
		b16 = new Bind(Color.propertyColorChangeLong);
		f.bind(front);
		f16.bind(front);
		b.bind(back);
		b16.bind(back);
		super.put(FrontColorBIND,f);
		super.put(FrontColor16bitBIND,f16);
		super.put(BackColorBIND,b);
		super.put(BackColor16bitBIND,b16);
	}

	public String getProperty(String propertyname){
		return property.get(propertyname);
	}

	public String getProperty(String propertyname,String defaultvalue){
		return property.get(propertyname, defaultvalue);
	}

	public String setProperty(String propertyname,String value){
		return property.set(propertyname, value);
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


	private boolean commandprint=true;
	public void setCommandPrintFlag(boolean b){
		commandprint = b;
	}

	public void commandPrintln(Object o){
		if(!commandprint)return;
		PrintStream out = get(CommandPrintStream, PrintStream.class);
		if(out==null)out = System.out;
		out.println(o);
	}

	public void commandPrint(Object o){
		if(!commandprint)return;
		PrintStream out = get(CommandPrintStream, PrintStream.class);
		if(out==null)out = System.out;
		out.print(o);
	}

	public PrintStream getCommandPrintStream(){
		if(!commandprint)return null;
		PrintStream out = get(CommandPrintStream, PrintStream.class);
		if(out==null)out = System.out;
		return out;
	}

	public void commandErrorPrintln(Object o){
		if(!commandprint)return;
		PrintStream out = get(CommandErrorPrintStream, PrintStream.class);
		if(out==null)out = System.err;
		out.println(o);
	}

	public void commandErrorPrint(Object o){
		if(!commandprint)return;
		PrintStream out = get(CommandErrorPrintStream, PrintStream.class);
		if(out==null)out = System.err;
		out.print(o);
	}

	public PrintStream getErrorCommandPrintStream(){
		if(!commandprint)return null;
		PrintStream out = get(CommandErrorPrintStream, PrintStream.class);
		if(out==null)out = System.err;
		return out;
	}

}
