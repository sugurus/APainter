package apainter.resorce;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class LimitedResource <E>{
	public static interface ResourceFactory<E>{
		public E create();
	}
	
	final LockObject[] locked;
	private Semaphore sema;
	private ResourceFactory<E> fact;
	
	public LimitedResource(int size,ResourceFactory<E> factory) {
		locked = new LockObject[size];
		fact = factory;
		sema = new Semaphore(size);
		for(int i=0;i<size;i++){
			locked[i]=new LockObject(factory.create(),sema);
		}
	}
	
	/**
	 * リソースを新たに作り直してしまいます。
	 */
	public void reflesh(){
		sema = new Semaphore(locked.length);
		for(int i=0;i<locked.length;i++){
			locked[i]=new LockObject(fact.create(),sema);
		}
	}
	
	/**
	 * リソースを取得するまで待機します。スレッドが割り込まれたなどの場合にはnullが返ります
	 * @return Resouceオブジェクト。使い終わったら必ずrelease関数を呼び出してください。
	 */
	public Resource<E> getResource(){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			return null;
		}
		Resource<E> r = new Resource<E>(this);
		boolean b=false;
		for(int i=0;i<locked.length;i++){
			b=r.getLock(i);
			if(b)break;
		}
		if(!b){
			throw new RuntimeException("error:resource can't get");
		}
		return r;
	}
	
	/**
	 * リソースを取得するまで最大でtimeoutミリ秒待機します。得られなかった場合はnullが返ります
	 * @param timeout 待機する時間（ミリ秒）
	 * @return Resouceオブジェクト。使い終わったら必ずrelease関数を呼び出してください。
	 */
	public Resource<E> getResource(long timeout){
		boolean k;
		try {
			k=sema.tryAcquire(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
		if(!k)return null;
		Resource<E> r = new Resource<E>(this);
		boolean b=false;
		for(int i=0;i<locked.length;i++){
			b=r.getLock(i);
			if(b)break;
		}
		if(!b){
			throw new RuntimeException("error:resource can't get");
		}
		return r;
	}
	
	/**
	 * リソースを取得するまで待機しません。取得試みに失敗した場合は直ちにnullを返します。
	 * @return Resouceオブジェクト。使い終わったら必ずrelease関数を呼び出してください。
	 */
	public Resource<E> tryGetResource(){
		if(!sema.tryAcquire())return null;
		Resource<E> r = new Resource<E>(this);
		boolean b=false;
		for(int i=0;i<locked.length;i++){
			b=r.getLock(i);
			if(b)break;
		}
		if(!b){
			throw new RuntimeException("error:resource can't get");
		}
		return r;
	}
	
	
}

class LockObject{
	private Object resource;
	private Object key;
	private boolean locked=false;
	private Semaphore sm;
	LockObject(Object e,Semaphore s){
		resource =e;
		sm = s;
	}
	
	boolean isLocked(){
		return locked;
	}
	
	synchronized boolean lock(Object key){
		if(locked)return false;
		locked=true;
		this.key = key;
		return true;
	}
	
	synchronized void release(Object key){
		if(key==this.key){
			locked=false;
			this.key =null;
			sm.release();
		}
	}

	
	synchronized Object getResource(Object key){
		if(key==this.key)
			return resource;
		return null;
	}
	
}