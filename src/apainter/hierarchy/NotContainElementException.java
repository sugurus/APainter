package apainter.hierarchy;

public class NotContainElementException extends RuntimeException{
	public NotContainElementException(Element<?> e) {
		super(String.format("don't have the Element:", e));
	}

}
