package apainter.hierarchy;

public class AddParentException extends RuntimeException{
	public AddParentException(Unit<?> u) {
		super(String.format("add parent Unit.", u));
	}

}
