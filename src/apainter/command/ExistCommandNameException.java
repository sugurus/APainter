package apainter.command;

public class ExistCommandNameException extends RuntimeException{
	public ExistCommandNameException(String comname) {
		super(comname+" already exit!");
	}
}
