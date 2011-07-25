package apainter.canvas.cedt.cpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CPUWorkThread {
	private static ExecutorService thread=null;
	private static int threadsize = Runtime.getRuntime().availableProcessors();

	public static boolean isRunning(){
		return thread!=null && !thread.isShutdown();
	}

	public static synchronized void runThread(){
		if(isRunning())return;
		threadsize=Runtime.getRuntime().availableProcessors();
		thread = Executors.newFixedThreadPool(threadsize-1);
	}

	public static synchronized void shutDown(){
		if(isRunning())thread.shutdown();
	}

	public static int getThreadSize(){
		return threadsize;
	}


	public static <V> Collection<V> exec(Callable<V>... callable){
		if(callable==null || callable.length==0)return new ArrayList<V>(1);
		if(!isRunning())runThread();
		@SuppressWarnings("unchecked")
		Future<V>[] fs = new Future[callable.length-1];
		for(int i=0;i<callable.length-1;i++){
			if(callable[i]==null)continue;
			fs[i]=thread.submit(callable[i]);
		}
		V v=null;
		try {
			v=callable[callable.length-1].call();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		ArrayList<V> vs = new ArrayList<V>();
		if(v!=null)vs.add(v);
		for(Future<V> f:fs){
			if(f==null)break;
			try {
				v =f.get();
				if(v!=null)vs.add(v);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return vs;
	}


	public static void exec(Runnable... run){
		if(run==null || run.length==0)return;
		if(!isRunning())runThread();
		Future<?>[] fs = new Future<?>[run.length-1];
		for(int i=0;i<run.length-1;i++){
			if(run[i]==null)continue;
			fs[i]=thread.submit(run[i]);
		}
		if(run[run.length-1]!=null)
			run[run.length-1].run();
		for(Future<?> f:fs){
			if(f==null)continue;
			try {
				f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}


	public static void exec(ArrayList<Runnable> run){
		if(run==null || run.size()==0)return;
		if(!isRunning())runThread();
		Future<?>[] fs = new Future<?>[run.size()-1];
		for(int i=0,l=run.size()-1;i<l;i++){
			if(run.get(i)==null)continue;
			fs[i]=thread.submit(run.get(i));
		}
		Runnable r = run.get(run.size()-1);
		if(r!=null)r.run();
		for(Future<?> f:fs){
			if(f==null)continue;
			try {
				f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

}
