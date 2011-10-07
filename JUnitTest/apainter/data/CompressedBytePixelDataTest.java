package apainter.data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CompressedBytePixelDataTest {

	static int w=100,h=100;
	static byte[] data = new byte[w*h];
	static CompressedBytePixelData p;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Random r = new Random();
		r.nextBytes(data);
		PixelDataByte d = new PixelDataByte(w, h, data);
		p = new CompressedBytePixelData(d);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInflate() {
		PixelDataByte d = p.inflate();
		assertTrue(Arrays.equals(d.getData(), data));
	}

}
