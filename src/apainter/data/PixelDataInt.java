package apainter.data;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class PixelDataInt extends PixelData{

	public static PixelDataInt create(int w,int h){
		if(w<=0||h<=0)throw new IllegalArgumentException(String.format("w:%d,h:%d",w,h));
		return new PixelDataInt(w, h, new int[w*h]);
	}


	/**
	 * 渡されたピクセルデータの一部をコピーし保持します。
	 * @param w
	 * @param h
	 * @param data
	 * @param x
	 * @param y
	 * @param subw
	 * @param subh
	 * @return
	 */
	public static PixelDataInt create(int w,int h,int[] data,
			int x,int y,int subw,int subh){
		if(w<=0||h<=0 || data.length != w*h || x<0||y<0||subw<=0||subh<=0||
				x+subw>w|y+subh>h)
			throw new IllegalArgumentException(String.format(
					"w:%d,h:%d,data.length:%d,x:%d,y:%d,subw:%d,subh:%d"
					,w,h,data.length,x,y,subw,subh));
		int[] t = new int[subw*subh];
		for(int yy=y,e=y+subh;yy<e;yy++){
			System.arraycopy(data, yy*w+x, t, (yy-y)*subw, subw);
		}
		return new PixelDataInt(subw, subh, t);
	}

	private int[] pixel;
	@Override
	public ColorType getColorType() {
		return ColorType.ARGB;
	}

	public PixelDataInt(int w,int h,int[] pixel) {
		super(w,h);
		if(pixel.length!=w*h)throw new RuntimeException(String.format("pixel length(%d) != w(%d)*h(%d)",pixel.length,w,h));
		this.pixel = pixel;
	}

	@Override
	public PixelData clone() {
		return new PixelDataInt(width, height, pixel.clone());
	}

	@Override
	public PixelDataInt copy(Rectangle r){
		return new PixelDataInt(r.width, r.height, copy(null, r));
	}

	@Override
	public void dispose() {
		pixel = null;
	}

	public void setData(int b,Rectangle r){
		r = getBounds().intersection(r);
		for(int y=r.y,e = r.y+r.height,ex = r.x+r.width;y<e;y++){
			for(int x=r.x;x<ex;x++){
				pixel[x+y*width]=b;
			}
		}
	}

	public void fill(int b){
		Arrays.fill(pixel, b);
	}

	public void setData(int[] b,Rectangle r){
		if(b.length < r.width*r.height)throw new IllegalArgumentException(String.format("b.length(%d) < r.width(%d)*r.height(%d)!",b.length,r.width,r.height));
		Rectangle r2 = intersection(r);
		if(r2.isEmpty())return;
		int xx = r2.x-r.x;
		int yy = r2.y -r.y;
		for(int y=0,ey=r2.height;y<ey;y++){
			System.arraycopy(b, xx+(y+yy)*r.width, pixel, r2.x+(r2.y+y)*width, r2.width);
		}
	}


	@Override
	public int[] getData() {
		return pixel;
	}

	public final void setData(int b,int x,int y){
		if(!contains(x,y))return;
		pixel[x+y*width]=b;
	}
	public int getData(int x,int y)throws OutBoundsException{
		if(!contains(x,y))throw new OutBoundsException(getBounds(), x, y);
		return pixel[x+y*width];
	}

	public int getData(int x,int y,int defaultvalue){
		if(!contains(x,y))return defaultvalue;
		return pixel[x+y*width];
	}

	public void draw(PixelDataInt p){
		int w = p.getWidth(),h=p.getHeight();
		int rw,rh;
		if(getWidth()<w)rw=getWidth();
		else rw=w;
		if(getHeight()<h)rh=getHeight();
		else rh=h;
		for(int y=0;y<rh;y++){
			System.arraycopy(p.pixel, y*w, pixel, y*getWidth(), rw);
		}
	}


	public int[] copy(int[] distination){
		if(distination ==null || distination.length < pixel.length)distination = new int[pixel.length];
		System.arraycopy(pixel, 0, distination, 0, pixel.length);
		return distination;
	}

	public int[] copy(int[] distination,Rectangle r){
		if(distination==null ||distination.length < r.width*r.height){
			distination = new int[r.width*r.height];
		}
		for(int y=r.y,ey=r.y+r.height,i=0;y<ey;y++){
			System.arraycopy(pixel, y*width+r.x, distination, i, r.width);
			i+=r.width;
		}
		return distination;
	}

	private BufferedImage img;
	/**
	 * 保持するデータを直接画像データに変換します。
	 * @return
	 */
	public synchronized Image getDirectImage(){
		if(img == null)
			img =createImage(pixel, width, height, 0xff000000, 0xff0000, 0xff00, 0xff);
		return img;
	}

	/**
	 * aRGB画像を作成します
	 * @param data イメージデータ
	 * @param width 画像幅
	 * @param height 画像高さ
	 * @param alphaMask
	 * 画素データからアルファ値を取り出すためのマスク。
	 * @param redMask 画素データから赤を取り出すためのマスク
	 * @param greenMask 画素データから緑を取り出すためのマスク
	 * @param blueMask 画素データから青を取り出すためのマスク
	 * @return dataを保持するBufferedImage
	 */
	public static BufferedImage createImage(int[] data,int width,int height,
			int alphaMask,int redMask,int greenMask,int blueMask){
		int[] mask = {redMask,greenMask,blueMask,alphaMask};
		DataBuffer buffer = new DataBufferInt(data, width*height);
		WritableRaster raster = Raster.
				createPackedRaster(buffer,width,height,width,mask,new Point());
		ColorModel model =
				new DirectColorModel(32, redMask, greenMask, blueMask, alphaMask);
		return new BufferedImage(model, raster, false, null);
	}

}
