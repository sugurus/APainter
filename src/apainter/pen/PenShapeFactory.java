package apainter.pen;

public interface PenShapeFactory {

	public String getPenName();
	public PenShape createPenShape(double width,double height);
	public long getID();

	/**
	 * ペンのデータを読み込みます。<br>
	 * 読み込まれていない場合、createPenShapeを実行すると、先にloadを呼び出します。<br>
	 * そのため、期待する時間通りに動かないかもしれないので、きちんとloadを呼び出してください。
	 */
	public void load();
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
