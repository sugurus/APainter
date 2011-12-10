package apainter.pen.impl;

import static java.lang.Math.*;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import apainter.Device;
import apainter.data.PixelData;
import apainter.data.PixelData15BitGray;
import apainter.data.PixelDataByte;
import apainter.data.PixelDataContainer;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;
import apainter.pen.PenShapeFactory2;

/**
 * 必要になったら計算するようにしようとして、放置中。
 * @author nodamushi
 *
 */
public class CylinderPenFactory implements PenShapeFactory{

	static HashMap<Integer, CylinderMapGroup> data = new HashMap<Integer, CylinderMapGroup>();
	
	final long id;
	
	
	public CylinderPenFactory(long id) {
		this.id = id;
	}
	static MapData getMapData(int size,float cx,float cy){
		CylinderMapGroup m = data.get(size);
		if(m==null)
			m =createGroup(size);
		return m.getData(cx, cy);
	}
	
	private static synchronized CylinderMapGroup createGroup(int size){
		CylinderMapGroup m = data.get(size);
		if(m==null){
			m = new CylinderMapGroup(size, getLength(size));
			data.put(size,m);
		}
		return m;
	}
	
	
	private static int getLength(int size){
		int l=5;
		if(size <=10){
			l=15;
		}else if(size<=100){
			l=11;
		}
		return l;
	}
	
	@Override
	public String getPenName() {
		return "cylinder";
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public void load() throws IOException {
		//処理時間のかかるr<1以下の作成と
		//r>1でのメソッドのコンパイルのための実行
		for(int size=1;size<22;size++){
			int l = getLength(size);
			CylinderMapGroup g = createGroup(size);
			for(int i=0;i<l;i++)for(int k=0;k<l;k++){
				g.getData(i/(float)l, k/(float)l);
			}
		}
		int size=40;
		int l = getLength(size);
		CylinderMapGroup g = createGroup(size);
		for(int i=0;i<l;i++)for(int k=0;k<l;k++){
			g.getData(i/(float)l, k/(float)l);
		}
	}

	@Override
	public void release() {
		for(int size:data.keySet()){
			if(size>100){
				CylinderMapGroup g = data.get(size);
				if(g!=null){
					g.release();
				}
			}
		}
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

	@Override
	public PenShapeFactory2 createFactory2() {
		return new CylinderShapeFactory();
	}
	
	private class CylinderShapeFactory implements PenShapeFactory2{

		final CylinderPenFactory p=CylinderPenFactory.this;
		String name;
		double percent=1;
		@Override
		public String getPenName() {
			return p.getPenName();
		}

		@Override
		public long getID() {
			return p.getID();
		}

		@Override
		public void load() throws IOException {
			p.load();
		}

		@Override
		public void release() {
			p.release();
		}

		@Override
		public boolean isLoaded() {
			return p.isLoaded();
		}

		@Override
		public PenShapeFactory2 createFactory2() {
			return p.createFactory2();
		}

		@Override
		public PenShape getPenShape(int size, Device device) {
			CylinderMapGroup m = data.get(size);
			if(m==null)
				m =createGroup(size);
			CylinderShape p = new CylinderShape(m, this);
			return p;
		}

		@Override
		public void setName(String name) {
			if(name==null)name ="";
			this.name = name;
		}

		@Override
		public void setMoveDistancePercent(double percent) {
			if(percent<0)percent=0;
			this.percent = percent;
		}

		@Override
		public double getMoveDistancePercent() {
			return percent;
		}
		
	}

	private class CylinderShape implements PenShape{
		
		final CylinderMapGroup group;
		CylinderShapeFactory f;
		
		CylinderShape(CylinderMapGroup map,CylinderShapeFactory fc){
			group = map;
			f = fc;
		}

		@Override
		public PixelDataContainer getFootPrint(double x, double y) {
			x = x-(int)floor(x);
			y = y-(int)floor(y);
			float cx = (float)x,cy=(float)y;
			MapData d = group.getData(cx, cy);
			CylinderDataContainer cd = new CylinderDataContainer(d, group.size, cx, cy);
			return cd;
		}

		@Override
		public Point getCenterPoint(double x,double y) {
			x = x-(int)floor(x);
			y = y-(int)floor(y);
			float cx = (float)x,cy=(float)y;
			MapData d = group.getData(cx, cy);
			return new Point(d.centerx,d.centery);
		}

		@Override
		public int getSize() {
			return group.size;
		}

		@Override
		public double getMoveDistance() {
			//TODO まぁ、あとでうまいこと考えよう。
			if(group.size < 25){
				return group.size*(f.percent>=1d?f.percent:1)/20d;
			}else if(group.size < 45){
				return group.size*f.percent/30d;
			}else if(group.size < 65){
				return group.size*f.percent/40d;
			}else if(group.size < 85){
				return group.size*f.percent/60d;
			}
			double r = group.size*f.percent/80d;
			return r;
		}


		@Override
		public String getName() {
			return f.name;
		}

		@Override
		public long getID() {
			return CylinderPenFactory.this.getID();
		}
		
	}
	
	
}

class CylinderMapGroup extends MapGroup{
	int length;
	public CylinderMapGroup(int size,int length) {
		super(size,length,length);
		this.length = length;
	}
	
	@Override
	public MapData createData(float cx,float cy){
		float t = 1f/length;
		int cenx=(int)(cx/t);
		int ceny=(int)(cy/t);
		cx = (cenx+0.5f)*t;
		cy = (ceny+0.5f)*t;
		float r = size/20f;//半径
		MapData md;
		if(r<1f){
			md=createMiniMapData(r, cx, cy);
		}else if(r < 2f)
			md =create15bitMapData(r, cx, cy);
		else
			md=createMapData(r, cx, cy);
		return md;
	}
	
	//rが1以下のちっちゃいデータ
	private static MapData createMiniMapData(float r,float cx,float cy){
		int centerx = -(int)floor(cx-r);
		int centery = -(int)floor(cy-r);
		int w = (int)ceil(cx+r)+centerx;
		int h = (int)ceil(cy+r)+centery;
		cx +=centerx;
		cy +=centery;
		byte[] integer= new byte[w*h];
		byte[] decimal = new byte[w*h];
		for(int y=0;y<h;y++)for(int x=0;x<w;x++){
			int k=0;
			for(int u=0;u<32;u++)for(int v=0;v<32;v++){
				boolean t = inCercle(r,
						x+1f/32f*u+1f/64f, y+1f/32f*v+1f/64f,
						cx, cy);
				if(t)k++;
			}
			k=(k*PixelData15BitGray.max15bitValue)/(32*32);
			integer[x+y*w]=PixelData15BitGray.integer(k);
			decimal[x+y*w]=PixelData15BitGray.decimal(k);
		}
		
		PixelData15BitGray data = new PixelData15BitGray(w, h, integer, decimal);
		return new MapData(data, centerx, centery);
	}
	
	private static MapData create15bitMapData(float r,float cx,float cy){
		int centerx = -(int)floor(cx-r);
		int centery = -(int)floor(cy-r);
		int w = (int)ceil(cx+r)+centerx;
		int h = (int)ceil(cy+r)+centery;
		cx +=centerx;
		cy +=centery;
		byte[] integer= new byte[w*h];
		byte[] decimal =new byte[w*h];
		for(int x=0;x<w;x++)for(int y=0;y<h;y++){
			boolean a = inCercle(r, x, y, cx, cy);
			boolean b = inCercle(r,x+1,y,cx,cy);
			boolean c = inCercle(r,x+1,y+1,cx,cy);
			boolean d = inCercle(r,x,y+1,cx,cy);
			
			if(a&&b&&c&&d){
				integer[x+y*w]=PixelData15BitGray.maxInteger;
				decimal[x+y*w]=PixelData15BitGray.maxDecimal;
			}else if(a|| b|| c|| d){
				int k=0;
				for(int u=0;u<5;u++)for(int v=0;v<5;v++){
					if(inCercle(r, x+0.2f*u+0.1f, y+0.2f*v+0.1f, cx, cy))k++;
				}
				k=k*PixelData15BitGray.max15bitValue/25;
				integer[x+y*w]=PixelData15BitGray.integer(k);
				decimal[x+y*w]=PixelData15BitGray.decimal(k);
			}
			
		}
		
		PixelData15BitGray data = new PixelData15BitGray(w, h, integer, decimal);
		return new MapData(data, centerx, centery);
	}
	
	private static MapData createMapData(float r,float cx,float cy){
		int centerx = -(int)floor(cx-r);
		int centery = -(int)floor(cy-r);
		int w = (int)ceil(cx+r)+centerx;
		int h = (int)ceil(cy+r)+centery;
		cx +=centerx;
		cy +=centery;
		byte[] array= new byte[w*h];
		for(int x=0;x<w;x++)for(int y=0;y<h;y++){
			boolean a = inCercle(r, x, y, cx, cy);
			boolean b = inCercle(r,x+1,y,cx,cy);
			boolean c = inCercle(r,x+1,y+1,cx,cy);
			boolean d = inCercle(r,x,y+1,cx,cy);
			
			if(a&&b&&c&&d){
				array[x+y*w]=(byte)255;
			}else if(a|| b|| c|| d){
				int k=0;
				for(int u=0;u<5;u++)for(int v=0;v<5;v++){
					if(inCercle(r, x+0.2f*u+0.1f, y+0.2f*v+0.1f, cx, cy))k++;
				}
				k=k*255/25;
				array[x+y*w]=(byte)k;
			}else{
				array[x+y*w]=0;
			}
			
		}
		
		PixelDataByte data = new PixelDataByte(w, h, array);
		return new MapData(data, centerx, centery);
	}
	
	private static boolean inCercle(float r,float x,float y,
			float centerx,float centery){
		return r*r-(x-centerx)*(x-centerx)-(y-centery)*(y-centery)>0;
	}
}

class CylinderDataContainer implements PixelDataContainer{

	transient PixelData data;
	final int size;
	final float cx,cy;
	
	public CylinderDataContainer(MapData m,int size,float cx,float cy) {
		this.size = size;
		this.cx = cx;
		this.cy = cy;
		data = m.data;
	}
	
	@Override
	public PixelData getPixelData() {
		return data;
	}

	@Override
	public int getWidth() {
		return data.getWidth();
	}

	@Override
	public int getHeight() {
		return data.getHeight();
	}

	@Override
	public void restore() {
		MapData m=CylinderPenFactory.getMapData(size, cx, cy);
		data = m.data;
	}
	
}
