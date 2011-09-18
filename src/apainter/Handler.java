package apainter;

public interface Handler {
	/**
	 * 対象の名前を返します。
	 * @return
	 */
	public String getName();

	/**
	 * ハンドラーを識別するための名前を返します。<br>
	 * それぞれのハンドラーでユニークでなくてはなりません。
	 * @return
	 */
	public String getHandlerName();
}
