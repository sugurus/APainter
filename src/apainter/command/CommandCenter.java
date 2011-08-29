package apainter.command;

import java.util.ArrayList;

import apainter.APainter;
import apainter.GlobalKey;
import apainter.GlobalValue;

public class CommandCenter {

	/**
	 * 解析し、実行する。
	 * @param command
	 * @throws NotFoundCommandException コマンドが見つからなかった場合投げられます。
	 */
	public void exec(String command)throws NotFoundCommandException{
		Command c = decode(command);
		if(c!=null)c.exe(v);
	}

	private static final String[] zero = new String[0];
	/**
	 * コマンドを解析し、実行をするCommandを返します。
	 * @param command コマンド文字列。複数行のコマンドはデコードできません。
	 * @return
	 * @throws NotFoundCommandException コマンドが見つからなかった場合投げられます。
	 */
	public Command decode(String command)throws NotFoundCommandException{
		if(command.equals(""))return null;
		APainter a = v.get(GlobalKey.APainter,APainter.class);
		String[] strs = command.split(" ",2);
		String com = strs[0];
		String[] par = strs.length<2||strs[1]==null?zero:strs[1].split(" ");
		for(CommandDecoder cd:decoders){
			if(cd.getCommandName().equals(com)){
				if(par.length!=0 && par[0].equals("-help")){
					v.commandPrintln(cd.help());
					return null;
				}
				Command c;
				try {
					c = cd.decode(par);
				} catch (Exception e) {
					return null;
				}
				if(c==null)return null;
				c.setAPainter(a);
				return c;
			}
		}
		throw new NotFoundCommandException(com);
	}

	GlobalValue v;
	ArrayList<CommandDecoder> decoders = new ArrayList<CommandDecoder>();
	public CommandCenter(GlobalValue gv){v = gv;}

	public CommandDecoder[] getAllDecoder(){
		return decoders.toArray(new CommandDecoder[decoders.size()]);
	}

	public void addCommand(CommandDecoder d)throws ExistCommandNameException{
		if(d!=null){
			String s = d.getCommandName();
			for(CommandDecoder cd:decoders){
				if(s.equals(cd.getCommandName())){
					throw new ExistCommandNameException(s);
				}
			}
			decoders.add(d);
		}
	}

	public void removeCommand(CommandDecoder d){
		decoders.remove(d);
	}
}
