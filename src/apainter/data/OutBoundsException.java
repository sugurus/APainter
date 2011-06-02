package apainter.data;

import java.awt.Rectangle;

public class OutBoundsException extends RuntimeException{
	public OutBoundsException(Rectangle r,int x,int y) {
		super(String.format("Point(%d,%d)  out" +
				" bounds:Rectangle x:%d,y:%d,width:%d,height:%d",
				x,y,r.x,r.y,r.width,r.height));
	}

}
