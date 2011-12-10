package apainter.canvas.cedt.cpu;
/**
 * CPUParallelWrokThreadを簡単に扱うためのクラス。
 * @author nodamushi
 *
 */
public abstract class CPUParallelWorker {
	private final int size;
	private final Runnable[] runs;
	public CPUParallelWorker() {
		this(false);
	}
	/**
	 * 
	 * @param single このフラグがtrueの時、シングルスレッドで計算します。
	 */
	public CPUParallelWorker(boolean single) {
		if(!CPUParallelWorkThread.isRunning())
			CPUParallelWorkThread.runThread();
		size = single?1:CPUParallelWorkThread.getThreadSize();
		runs = new Runnable[size];
		for(int i=0;i<size;i++){
			runs[i] = new R(i);
		}
	}
	/**
	 *　並列処理の実装
	 * @param id 現在実行されているタスクのid（0～size-1）
	 * @param psize 並行数
	 */
	protected abstract void task(int id,int psize);
	
	/**
	 * 処理を開始します。処理が終わるまでこの関数は返りません。
	 */
	public final void start(){
		if(size==1){
			runs[0].run();
		}else
			CPUParallelWorkThread.exec(runs);
	}
	
	private class R implements Runnable{
		int id;
		R(int i){
			id = i;
		}
		@Override
		public void run() {
			task(id, size);
		}
	}
	
	
}
