package apainter.pen.impl;

import static java.lang.Math.*;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DataFormatException;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import apainter.Device;
import apainter.data.PixelData;
import apainter.data.PixelDataByte;
import apainter.data.PixelDataContainer;
import apainter.pen.APainterPen;
import apainter.pen.Group;
import apainter.pen.PenData;
import apainter.pen.PenPackage;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;
import apainter.pen.PenShapeFactory2;

/**
 * ファイルから読み込み、ペン生成をする基底クラス。
 * @author nodamushi
 *
 */
public class URLPenFactory implements PenShapeFactory{

	private URL url;
	private int packageposition=0;
	private long id;
	private boolean loaded;
	protected HashMap<Integer, Group> cpudata = new HashMap<Integer, Group>();
	protected String name;

	public URLPenFactory(long id,URL url) {
		if(url==null)throw new NullPointerException("url");
		this.url =url;
		this.id = id;
	}

	@Override
	public String getPenName() {
		return name;
	}

	private static AtomicInteger factory2ID=new AtomicInteger();
	@Override
	public PenShapeFactory2 createFactory2() {
		int i = factory2ID.getAndIncrement();
		long id = this.id+i*1000000000;
		return new URLFactory2(id);
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public synchronized void load() throws IOException {
		if(loaded)return;
		InputStream in=null;
		try{
			in = url.openStream();
			APainterPen ap=null;
			try {
				ap = APainterPen.decode(in);
			} catch (DataFormatException e) {
				throw new IOException("DataFormatException;"+e.getMessage());
			}
			PenPackage p = ap.getPackage(packageposition);
			if(p==null){
				throw new IOException("file doesn't have "+packageposition+" packages!");
			}
			name = p.getName();
			if(name==null){
				name="";
			}

			for(Group g:p.getGroups()){
				int size = g.getSize();
				cpudata.put(size, g);
			}
			loaded=true;
		}finally{
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {}
		}
	}

	@Override
	public synchronized void release() {
		cpudata.clear();
		loaded = false;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	protected Group findGroup(int size){
		Group g = cpudata.get(size);
		if(g!=null)return g;

		size= size/10;
		size = size*10;//少数部分削除
		g = cpudata.get(size);
		if(g!=null)return g;

		//TODO 縮小または拡大の作成。

		return null;
	}

	protected double getIntervalLength(int size,double intervalpercent) {
		double w= size*0.025*intervalpercent;
		if(w < 0.01)return 0.01;
		else return w;
	}

	protected class URLFactory2 implements PenShapeFactory2{

		String name=URLPenFactory.this.name;
		long id;
		double p=1;

		public URLFactory2(long id) {
			this.id=id;
		}

		@Override
		public String getPenName() {
			return name;
		}
		@Override
		public void setName(String name) {
			if(name!=null)this.name=name;
		}

		@Override
		public long getID() {
			return id;
		}

		@Override
		public void load() throws IOException {
			URLPenFactory.this.load();
		}

		@Override
		public void release() {
			URLPenFactory.this.release();
		}

		@Override
		public boolean isLoaded() {
			return URLPenFactory.this.isLoaded();
		}

		@Override
		public PenShapeFactory2 createFactory2() {
			return URLPenFactory.this.createFactory2();
		}

		@Override
		public PenShape getPenShape(int size,Device d) {
			Group g = findGroup(size);
			PenShape p= new URLPenShape(g);
			p.setIntervalLengthPercent(this.p);
			return p;
		}
		@Override
		public double getMoveDistancePercent() {
			return p;
		}
		@Override
		public void setMoveDistancePercent(double n) {
			if(n<=0)return;
			p = n;
		}

	}

	protected class URLPenShape implements PenShape{

		Group g;
		double intervalpercent=1;



		public URLPenShape(Group gg) {
			g = gg;
		}

		@Override
		public Point getCenterPoint() {
			return new Point(g.getCenterX(),g.getCenterY());
		}

		@Override
		public PixelDataContainer getFootPrint(double x, double y) {
			x = x-(int)floor(x);
			y = y-(int)floor(y);
			int px = (int) (x*g.getXBlocks());
			int py = (int) (y*g.getYBlocks());
			final PenData p = g. getPen(px,py);
			PixelDataContainer pdc = new PixelDataContainer() {

				@Override
				public int getWidth() {
					return p.getWidth();
				}

				@Override
				public PixelData getPixelData() {
					return p.getDataBuffer();
				}

				@Override
				public int getHeight() {
					return p.getHeight();
				}
			};
			return pdc;
		}

		@Override
		public Dimension getMapSize() {
			return new Dimension(g.getWidth(), g.getHeight());
		}

		@Override
		public int getSize() {
			return g.getSize();
		}

		@Override
		public double getMoveDistance() {
			return URLPenFactory.this.getIntervalLength(g.getSize(), intervalpercent);
		}

		@Override
		public void setIntervalLengthPercent(double percent) {
			if(percent <0)percent=0;
			intervalpercent = percent;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public long getID() {
			return id;
		}

	}

}
