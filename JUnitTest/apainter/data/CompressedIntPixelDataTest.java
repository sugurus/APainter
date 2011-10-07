package apainter.data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CompressedIntPixelDataTest {

	public static int w = 100,h = 100;
	public static int[] i=new int[w*h];
	public static PixelDataInt buffer;
	public static CompressedIntPixelData compress;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Random r  = new Random();
		for(int t=0;t<i.length;t++){
			i[t] = r.nextInt();
		}
		buffer = new PixelDataInt(w, h, i);
		compress = new CompressedIntPixelData(buffer);
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
		long time= System.currentTimeMillis();
		PixelDataInt p = compress.inflate();
		System.out.println((System.currentTimeMillis()-time));
		int[] ii = p.getData();
		assertTrue(Arrays.equals(i, ii));
	}

}
