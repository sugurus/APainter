package apainter.debug;

import java.util.ArrayList;

import apainter.GlobalValue;

public class CommandCenter {

	public void exec(String command){
		for(CommandDecoder cd:decoders){
			Command c = cd.decode(command);
			if(c!=null){
				c.exec(v);
				return;
			}
		}
		System.out.println(command+" is not find!");
	}

	GlobalValue v;
	ArrayList<CommandDecoder> decoders = new ArrayList<CommandDecoder>();
	public CommandCenter() {}
	public CommandCenter(GlobalValue gv){v = gv;}
	public void setGlobalValue(GlobalValue gv){
		if(v==null)v=gv;
	}
	public void addCommand(CommandDecoder d){
		if(d!=null&&!decoders.contains(d))decoders.add(d);
	}

	public void removeCommand(CommandDecoder d){
		decoders.remove(d);
	}
}
