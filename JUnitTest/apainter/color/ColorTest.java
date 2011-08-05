package apainter.color;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * JUnit Color Test
 */
public class ColorTest {

	static Color color;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		color = new Color();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		color.set16bitARGB(0xffff111122223333L);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetARGBInt() {
		int c = 0xffffffff;
		color.setARGB(c);
		int t = color.getARGB();
		assertEquals(c, t);
	}
	@Test
	public void testSetARGBInt2() {
		int c = 0xffffffff;
		color.setARGB(c);
		long l = color.get16bitARGB();
		assertEquals(0xffffffffffffffffL, l);
	}

	@Test
	public void testSetARGBInt3() {
		int c = 0xff112233;
		color.setARGB(c);
		int t = color.getARGB();
		assertEquals(c, t);
	}

	@Test
	public void testSetARGBInt4() {
		int c = 0xff112233;
		color.setARGB(c);
		long l = color.get16bitARGB();
		assertEquals(0xffff110022003300L, l);
	}
	@Test
	public void testSetARGBInt5() {
		int c = 0x00ffffff;
		color.setARGB(c);
		int t = color.getARGB();
		assertEquals(0, t);
	}



	@Test
	public void testSet16bitARGBLong() {
		long c = 0x12ffff43c23f54ffL;
		color.set16bitARGB(c);
		long t = color.get16bitARGB();
		assertEquals(c, t);
	}

	@Test
	public void testSet16bitARGBLong2() {
		long c = 0x12ffff43c23f54ffL;
		color.set16bitARGB(c);
		int i = color.getARGB();
		assertEquals(i, 0x12ffc254);
	}

	@Test
	public void testSet16bitARGBLong3() {
		long c = 0x0000ff43c23f54ffL;
		color.set16bitARGB(c);
		long t = color.get16bitARGB();
		assertEquals(0, t);
	}



	@Test
	public void testGetA() {
		int i = color.getA();
		assertEquals(i, 0xff);
	}

	@Test
	public void testGet16bitA() {
		int i = color.get16bitA();
		assertEquals(i, 0xffff);
	}

	@Test
	public void testGetR() {
		int i = color.getR();
		assertEquals(i, 0x11);
	}

	@Test
	public void testGet16bitR() {
		int i = color.get16bitR();
		assertEquals(i, 0x1111);
	}

	@Test
	public void testGetG() {
		int i = color.getG();
		assertEquals(i, 0x22);
	}

	@Test
	public void testGet16bitG() {
		int i = color.get16bitG();
		assertEquals(i, 0x2222);
	}

	@Test
	public void testGetB() {
		int i = color.getB();
		assertEquals(i, 0x33);
	}

	@Test
	public void testGet16bitB() {
		int i = color.get16bitB();
		assertEquals(i, 0x3333);
	}

	@Test
	public void testToAwtColor() {
		java.awt.Color c = color.toAwtColor();
		java.awt.Color cc = new java.awt.Color(0xff112233,true);
		assertEquals(c, cc);
	}

}
