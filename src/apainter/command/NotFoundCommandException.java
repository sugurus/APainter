package apainter.command;

public class NotFoundCommandException extends Exception{
	public NotFoundCommandException(String commandname) {
		super(commandname);
	}
}
