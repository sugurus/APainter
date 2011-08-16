package apainter.pen.impl;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;

import apainter.construct.DimensionDouble;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;

/**
 * テスト用のペン。真四角のペン。
 * @author nodamushi
 *
 */
public class TestPenFactory implements PenShapeFactory{

	private boolean loaded = false;
	private static final String PenName ="Test";
	private long id;
	private HashMap<String,PixelDataIntBuffer> cpudata = new HashMap<String,PixelDataIntBuffer>();

	public TestPenFactory(long id) {
		this.id = id;
	}

	private int[] createMap(int w){
		int[] m = new int[w*w];
		Arrays.fill(m, 255);
		return m;
	}

	@Override
	public String getPenName() {
		return PenName;
	}


	@Override
	public PenShape createPenShape(double width, double height) {
		if(width<1)width = 1;
		return new TestPen((int)width);
	}

	@Override
	public long getID() {
		return id;
	}

	private String toString(double d){
		int i;
		if(d<1)i=1;
		else i = (int)d;
		return Integer.toString(i);
	}

	@Override
	public synchronized void load() {
		if(loaded)return;
		for(int i=1;i<21;i++){
			int[] m = createMap(i);
			PixelDataIntBuffer p = new PixelDataIntBuffer(i, i, m);
			cpudata.put(toString(i*10), p);
		}
		loaded= true;
	}

	@Override
	public synchronized void release() {
		cpudata.clear();
		loaded=false;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	private class TestPen implements PenShape{
		int width;
		double intervalpercent=1;

		public TestPen(int w) {
			width = w;
		}


		@Override
		public PixelDataBuffer getFootPrint(double x,double y,int size) {
			if(!loaded)load();
			return cpudata.get(TestPenFactory.this.toString(size));
		}


		@Override
		public Dimension getMapSize() {
			return new Dimension(width,width);
		}
		@Override
		public int getSize() {
			return width*10;
		}

		@Override
		public double getIntervalLength(int size) {
			double w= size*0.04*intervalpercent;
			if(w < 1)return 1;
			else return (int)w;
		}

		@Override
		public void setIntervalLengthPercent(double percent) {
			if(percent <0)percent=0;
			intervalpercent = percent;
		}

		@Override
		public String getName() {
			return PenName;
		}

		@Override
		public long getID() {
			return id;
		}

	}

}
