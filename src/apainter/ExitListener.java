package apainter;

import java.util.EventListener;

public interface ExitListener extends EventListener{
	/**
	 * 終了処理に入る前に呼び出されます。<br>
	 * 終了してはいけない場合はfalseを返してください。<br>
	 * その場合終了処理を中断します。<br>
	 * このメソッドが呼び出され、trueを返しても、
	 * そのほかのリスナーがfalseを返した場合は終了しないので、終了処理をここで行わないでください。
	 * @param apainter 終了処理に入っているAPainter
	 * @return 終了して良いか否か
	 */
	public boolean exiting(APainter apainter);

	/**
	 * 終了前に呼び出されます。<br>
	 * このメソッドが呼ばれた場合はAPainterは終了します。<br>
	 * 将来このメソッドは削除される可能性があります。<br>
	 * System#exitなどは呼び出さないでください。
	 * @param apainter 終了するAPainter
	 */
	public void exit(APainter apainter);

	/**
	 * 終了した後に呼び出されます。
	 * @param apainter 終了したAPainter
	 */
	public void exited(APainter apainter);
}
