package apainter.canvas.cedt.cpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import apainter.APainter;

/**
 *レンダリングなど速度が必要な並行処理を行うためのスレッド。<br>
 *特に重要でない簡単な処理を任せるためのスレッドではない。
 */
public class CPUParallelWorkThread {
	private static ExecutorService thread=null;
	private static int threadsize = Runtime.getRuntime().availableProcessors();
	private static ArrayList<APainter> apainters=new ArrayList<APainter>();

	public static synchronized void use(APainter apainter){
		if(!apainters.contains(apainter)){
			apainters.add(apainter);
			runThread();
		}
	}

	public static synchronized void stop(APainter apainter){
		apainters.remove(apainter);
		if(apainters.isEmpty()){
			shutDown();
		}
	}

	//DefaultThreadFactoryの優先度だけ改変
	static class WorkThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        WorkThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();
            namePrefix = "workpool-" +
                          poolNumber.getAndIncrement() +
                         "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.MAX_PRIORITY)
                t.setPriority(Thread.MAX_PRIORITY);
            return t;
        }
    }

	public static boolean isRunning(){
		return thread!=null && !thread.isShutdown();
	}

	public static synchronized void runThread(){
		if(isRunning())return;
		threadsize=Runtime.getRuntime().availableProcessors();
		thread = Executors.newFixedThreadPool(threadsize-1,new WorkThreadFactory());

	}

	public static synchronized void shutDown(){
		if(isRunning())thread.shutdown();
	}

	public static int getThreadSize(){
		return threadsize;
	}


	public static <V> Collection<V> exec(Callable<? extends V>... callable){
		if(callable==null || callable.length==0)return new ArrayList<V>(1);
		if(!isRunning())runThread();
		@SuppressWarnings("unchecked")
		Future<? extends V>[] fs = new Future[callable.length-1];
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
		for(Future<? extends V> f:fs){
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

	private CPUParallelWorkThread() {}
}
