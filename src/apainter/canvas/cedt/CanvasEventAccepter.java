package apainter.canvas.cedt;

import apainter.canvas.event.CanvasEvent;

public interface CanvasEventAccepter {
	public void passEvent(CanvasEvent e);
	public void initCEDT();
	public void shutDownCEDT();
	/**
	 * なんでも受け入れる「ホスト内の」スレッドにて実行させます。<br>
	 * ここで重たい処理をすると他のイベント処理も遅れます。
	 * @param r
	 */
	public void runInAnyThread(Runnable r);
}
