package apainter.command;

public interface CommandDecoder {
	/**
	 * 渡されたパラメーターを解析し、正しいコマンドだった場合、それを実行するためのCommandを返します
	 * @return Command実行オブジェクト。commandが正しくない場合はnull
	 */
	public Command decode(String param);

	/**
	 *
	 * @param commandname
	 * @return
	 */
	public boolean isMatch(String commandname);

}
