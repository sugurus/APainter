package apainter.command;

import apainter.GlobalValue;

public interface Command {
	public void exec(GlobalValue global);
}
