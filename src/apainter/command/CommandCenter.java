package apainter.command;

import java.util.ArrayList;

import apainter.GlobalValue;

public class CommandCenter {

	public void exec(String command){
		Command c = decode(command);
		if(c!=null)c.exec(v);
	}

	/**
	 * コマンドを解析し、実行をするCommandを返します。
	 * @param command コマンド文字列。複数行のコマンドはデコードできません。
	 * @return
	 */
	public Command decode(String command){
		String[] strs = command.split(" ",2);
		String com = strs[0];
		String par = strs.length<2||strs[1]==null?"":strs[1];
		for(CommandDecoder cd:decoders){
			if(cd.isMatch(com)){
				return cd.decode(par);
			}
		}
		System.err.println("command'"+com+"' is not find! "+command);
		return null;
	}

	GlobalValue v;
	ArrayList<CommandDecoder> decoders = new ArrayList<CommandDecoder>();
	public CommandCenter(GlobalValue gv){v = gv;}
	public void addCommand(CommandDecoder d){
		if(d!=null&&!decoders.contains(d))decoders.add(d);
	}

	public void removeCommand(CommandDecoder d){
		decoders.remove(d);
	}
}
