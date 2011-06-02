package apainter.hierarchy;

public class ContainElementException extends RuntimeException{

	public ContainElementException(Element<?> e) {
		super("because contain the element,can't add the element"+e);
	}
}
