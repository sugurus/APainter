package apainter.debug;

public interface CommandDecoder {
	/**
	 * 渡された文字列を解析し、正しいコマンドだった場合、それを実行するためのCommandを返します
	 * @return Command実行オブジェクト。commandが正しくない場合はnull
	 */
	public Command decode(String command);

}
