package apainter.pen.impl;

import java.net.URL;
import java.util.HashMap;

import apainter.data.PixelDataIntBuffer;
import apainter.pen.PenShape;
import apainter.pen.PenShapeFactory;

/**
 * ファイルから読み込み、ペン生成をする基底クラス。
 * @author nodamushi
 *
 */
public class URLPenFactory implements PenShapeFactory{

	private URL url;
	private long id;
	private boolean loaded;
	private HashMap<String, PixelDataIntBuffer> cpudata = new HashMap<String, PixelDataIntBuffer>();

	public URLPenFactory(long id,URL url) {
		if(url==null)throw new NullPointerException("url");
		this.url =url;
		this.id = id;
	}

	protected String getPenSizeName(double w,double h){
		//TODO URLPenFactory
		return null;
	}
	@Override
	public String getPenName() {
		// TODO URLPenFactoryの名前
		return null;
	}

	@Override
	public PenShape createPenShape(double width, double height) {
		// TODO URLPenFactory
		return null;
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public synchronized void load() {
		// TODO 読み込み

	}

	@Override
	public synchronized void release() {
		// TODO 解放

	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

}
