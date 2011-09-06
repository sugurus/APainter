package apainter.bind;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import apainter.BindKey;

public abstract class BindObject {

	Bind bind;

	private String propertyName="BINDOBJECT";
	private Object source=this;
	private boolean isBind=false;

	public BindObject() {}
	public BindObject(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * 値を設定します。<br>
	 * setValueは使わないでください。
	 * @param o  PropertyChangeEventを生成して返します。
	 * @return 変更前の値。ただし、isReturnPropertyChangeEventがtrueを返すときはPropertyChangeEventを作成して返します。
	 * @see BindObject#setSource(Object)
	 * @see BindObject#getSource()
	 */
	public final PropertyChangeEvent set(Object o){
		Object old=_set(o);
		if(old==Bind.rejected){
			//後で何か書き足すつもり。
			return null;
		}

		return new  PropertyChangeEvent(source, propertyName, old, o);
	}


	/**
	 * バインドされている変数をメイン変数があればそれの値に変更します。
	 */
	public void addjust(){
		if(bind!=null&&isBind)bind.addjust();
	}

	/**
	 * バインド状態を設定します。falseの時、この値が変更されたり、他の値が変更されたりしても影響を受けません。<br>
	 * バインドしたとき自動的にtrueになります。
	 * @param b
	 */
	public void setBind(boolean b){
		isBind=b;
	}

	public boolean isBind(){
		return isBind;
	}

	/**
	 * propertyChangeEventのソースを設定します。
	 * @param o
	 */
	public final void setSource(Object o){
		source = o==null?this:o;
	}

	/**
	 * propertyChangeEventのソースを返します。
	 * @return
	 */
	public final Object getSource(){
		return source;
	}



	public final void setPorpertyName(String name){
		propertyName = name!=null?name:"";
	}

	public final String getPropertyName(){
		return propertyName;
	}


	private final Object _set(Object newobj){
		Object old =get();
		if(bind==null|| !isBind){
			if(isSettable(newobj))
				try {
					setValue(newobj);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}else{
			Object oo=bind.set(newobj);
			if(oo!=null)old=oo;
		}
		return old;
	}

	/**
	 * 設定されている値を返します。
	 * @return
	 */
	public Object get(){
		return bind==null?null:bind.get();
	}


	/**
	 * 渡された値を<b style="color:red">変更することなく</b>もしくは同等な値に設定する実装をしてください。<br>
	 * 代入する値を変更すると他の値との整合性がとれなくなります。<br>
	 * <b style="color:red">外部からは決して呼び出さないでください。</b><br>
	 * また、<b style="color:red">setValue関数がスタックにある状態からset関数を決して呼び出さないでください。</b><br>
	 * その代わりにBindObject#set(Object)を用いてください。
	 * @see BindObject#set(Object)
	 */
	public abstract void setValue(Object value) throws Exception;

	/**
	 * 渡された値に値を変更できるかどうかを返します。
	 * @param value 設定しようとしている値
	 * @return 設定可能かどうか
	 */
	public boolean isSettable(Object value){
		return true;
	}

	public void setend(Object oldobj,Object newobj){

	}

}
