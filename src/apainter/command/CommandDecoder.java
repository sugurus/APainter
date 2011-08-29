package apainter.command;

/**
 * APainterを操作するコマンド文字列を解析し、コマンド実行用のオブジェクトを返すインターフェースです。<br>
 * CommandDecoder自体はAPainterに対して操作を行いません。
 *
 * @see apainter.command.Command
 * @author nodamushi
 *
 */
public interface CommandDecoder {
	/**
	 * 渡されたパラメータを解析し、正しいコマンドだった場合、それを実行するためのCommandを返します<br>
	 * @param コマンドのパラメータ文字配列。nullは渡されない。(パラメータがない場合空配列が入る)
	 * @return Command実行オブジェクト。パラメータが正しくない場合はnull
	 */
	public Command decode(String[] params) throws Exception;

	/**
	 * コマンド名を返します。
	 * @return コマンド名。nullや空文字列を返してはならない
	 */
	public String getCommandName();

	/**
	 * コマンドの説明を返します。
	 * @return
	 */
	public String help();

}
