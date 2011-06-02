package apainter.hierarchy;

public class OutOfUnitSizeIndexException extends RuntimeException{

	public OutOfUnitSizeIndexException(int i,int size) {
		super(String.format("size over! %d (size=%d)", i,size));
	}
}
