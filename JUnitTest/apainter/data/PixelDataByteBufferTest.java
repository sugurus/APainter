package apainter.data;

import static org.junit.Assert.*;

import java.awt.Rectangle;

import org.junit.Test;

public class PixelDataByteBufferTest {

	@Test
	public void testIntersection() {
		PixelDataByte b = PixelDataByte.create(100, 100);
		Rectangle r = new Rectangle(-10,20,100,230);
		Rectangle t = new Rectangle(0,20,90,80);
		assertEquals(b.intersection(r), t);
	}

}
