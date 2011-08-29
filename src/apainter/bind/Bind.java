package apainter.bind;

import java.util.Vector;

public class Bind {

	static final Object rejected=new Object();

	BindObject mainObject;
	final Vector<BindObject> list = new Vector<BindObject>();
	public Bind(){
		this(null);
	}

	public Bind(BindObject obj){
		mainObject = obj;
		if(mainObject!=null){
			list.add(obj);
			obj.bind = this;
			obj.setBind(true);
		}
	}

	/**
	 * BindObjectを追加します
	 * @param b
	 * @return 追加に成功したか否か
	 */
	public boolean add(BindObject b){
		if(b!=null&&!list.contains(b)){
			if(mainObject!=null){

				Object o=get();
				try {
					b.setValue(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
				b.setPorpertyName(mainObject.getPropertyName());
			}
			list.add(b);
			b.bind=this;
			b.setBind(true);
			return true;
		}
		return false;
	}

	public void remove(BindObject b){
		if(list.contains(b)){
			list.remove(b);
			b.bind = null;
			b.setBind(false);
		}
	}

	public void removeAll(){
		list.clear();
	}

	void addjust(){
		if(mainObject!=null){
			set(get());
		}
	}


	Object set(Object newobj){
		Object o=null;
		for(BindObject b:list){
			if(!b.isSettable(newobj))return rejected;
		}
		if(mainObject!=null){
			if(!mainObject.isSettable(newobj))return rejected;
			o=get();
		}

		for(BindObject b:list){
			if(b.isBind()){
				try {
					b.setValue(newobj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return o;
	}

	private int getnum=0;
	public synchronized Object get() {
		Object o = null;
		if(mainObject!=null){
			getnum++;
			if(getnum!=1)return null;
			o = mainObject.get();
			getnum = 0;
		}
		return o;
	}


}
