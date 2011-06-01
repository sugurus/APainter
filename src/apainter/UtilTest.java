package apainter;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testModIntInt() {
		int t = Util.mod(-32, 5);
		assertEquals(t, 3);
	}

	@Test
	public void testModDoubleInt() {
		double t = Util.mod(5.6, 3);
		assertEquals(t, 2.6,0.01);
	}

	@Test
	public void testMax() {
		int[] m = Util.max(1,2,3,4,5);
		assertEquals(m[0], 5);
	}



}
