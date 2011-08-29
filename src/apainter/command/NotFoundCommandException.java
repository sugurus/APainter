package apainter.command;

public class NotFoundCommandException extends RuntimeException{
	public NotFoundCommandException(String commandname) {
		super(commandname);
	}
}
