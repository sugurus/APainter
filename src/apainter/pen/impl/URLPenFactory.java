package apainter.pen.impl;

import static java.lang.Math.*;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.DataFormatException;

import apainter.Device;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.pen.APainterPen;
import apainter.pen.Group;
import apainter.pen.PenData;
import apainter.pen.PenPackage;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;

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

	@Override
	public PenShape createPenShape(int size,Device d) {
		Group g = findGroup(size);
		return new URLPenShape(g);
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
		if(w < 1)return 1;
		else return (int)w;
	}


	protected class URLPenShape implements PenShape{

		Group g;
		double intervalpercent=1;



		public URLPenShape(Group gg) {
			g = gg;
		}

		@Override
		public PixelDataBuffer getFootPrint(double x, double y, int size) {
			Group gg ;
			if(g.getSize()==size)gg=g;
			else gg= findGroup(size);
			x = x-(int)floor(x);
			y = y-(int)floor(y);
			int px = (int) (x*gg.getXBlocks());
			int py = (int) (y*gg.getYBlocks());
			PenData p = gg. getPen(px,py);
			byte[] b =((PixelDataByteBuffer) p.getDataBuffer()).getData();
			return p.getDataBuffer();
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
		public double getIntervalLength(int size) {
			return URLPenFactory.this.getIntervalLength(size, intervalpercent);
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
