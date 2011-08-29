package apainter.pen;

import java.io.IOException;

import apainter.Device;

public interface PenShapeFactory {

	public String getPenName();
	/**
	 * サイズは10倍された値です。1サイズは10です。
	 * @param size
	 * @param device
	 * @return
	 */
	public PenShape createPenShape(int size,Device device);
	public long getID();

	/**
	 * ペンのデータを読み込みます。<br>
	 * 読み込まれていない場合、createPenShapeを実行すると、先にloadを呼び出します。<br>
	 * そのため、期待する時間通りに動かないかもしれないので、きちんとloadを呼び出してください。
	 * @throws IOException
	 */
	public void load() throws IOException;
	/**
	 * ペンのデータを解放します。もう一度利用するにはloadを呼び出す必要があります。
	 */
	public void release();
	/**
	 * ペンのデータが読み込まれているかどうかを返します。<br>
	 * @return
	 */
	public boolean isLoaded();

}
