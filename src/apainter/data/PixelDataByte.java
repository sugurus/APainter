package apainter.data;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * グレースケール画像を保持するためのバッファー
 * @author nodamushi
 *
 */
public class PixelDataByte extends PixelData{

	public static PixelDataByte create(int w,int h){
		if(w<=0||h<=0)throw new IllegalArgumentException(String.format("w:%d,h:%d",w,h));
		return new PixelDataByte(w, h, new byte[w*h]);
	}

	public static PixelDataByte create(int w,int h,byte[] data,
			int x,int y,int subw,int subh){
		if(w<=0||h<=0 || data.length != w*h || x<0||y<0||subw<=0||subh<=0||
				x+subw>w|y+subh>h)
			throw new IllegalArgumentException(String.format(
					"w:%d,h:%d,data.length:%d,x:%d,y:%d,subw:%d,subh:%d"
					,w,h,data.length,x,y,subw,subh));
		byte[] t = new byte[subw*subh];
		for(int yy=y,e=y+subh;yy<e;yy++){
			System.arraycopy(data, yy*w+x, t, (yy-y)*subw, subw);
		}
		return new PixelDataByte(subw, subh, t);
	}

	/**
	 * 配列のコピーをデータに持つバッファーを作成します。
	 * @param w
	 * @param h
	 * @param b
	 * @return
	 */
	public static PixelDataByte copyBuffer(int w,int h,byte[] b){
		return new PixelDataByte(w, h, b.clone());
	}

	@Override
	public PixelData clone() {
		return new PixelDataByte(width, height, pixel.clone());
	}

	public PixelDataByte copy(Rectangle r){
		return new PixelDataByte(r.width, r.height,copy((byte[])null, r));
	}

	@Override
	public ColorType getColorType() {
		return ColorType.GRAY;
	}

	private byte[] pixel;

	public PixelDataByte(int w,int h,byte[] pixel) {
		super(w,h);
		if(pixel.length!=w*h)throw new RuntimeException(String.format("pixel length(%d) != w(%d)*h(%d)",pixel.length,w,h));
		this.pixel = pixel;
	}

	@Override
	public void dispose() {
		pixel = null;
	}


	@Override
	public byte[] getData() {
		return pixel;
	}

	public void setData(byte b,int x,int y){
		if(!contains(x,y))return;
		pixel[x+y*width]=b;
	}

	public void setData(byte b,Rectangle r){
		r = getBounds().intersection(r);
		for(int y=r.y,e = r.y+r.height,ex = r.x+r.width;y<e;y++){
			for(int x=r.x;x<ex;x++){
				pixel[x+y*width]=b;
			}
		}
	}

	public void fill(byte b){
		Arrays.fill(pixel,b);
	}

	public void setData(byte[] b,Rectangle r){
		if(b.length < r.width*r.height)throw new IllegalArgumentException(String.format("b.length(%d) < r.width(%d)*r.height(%d)!",b.length,r.width,r.height));
		Rectangle r2 = intersection(r);
		if(r2.isEmpty())return;
		int xx = r2.x-r.x;
		int yy = r2.y -r.y;
		for(int y=0,ey=r2.height;y<ey;y++){
			System.arraycopy(b, xx+(y+yy)*r.width, pixel, r2.x+(r2.y+y)*width, r2.width);
		}
	}

	public int getData(int x,int y){
		if(!contains(x,y))throw new OutBoundsException(getBounds(), x, y);
		return pixel[x+y*width]&0xff;
	}

	public int[] copy(int[] distination){
		if(distination ==null || distination.length < pixel.length)distination = new int[pixel.length];
		for(int i=pixel.length-1;i>=0;i--){
			distination[i] = pixel[i]&0xff;
		}
		return distination;
	}

	public int[] copy(int[] distination,Rectangle r){
		if(distination==null ||distination.length < r.width*r.height){
			distination = new int[r.width*r.height];
		}
		for(int x,y=r.y,ex=r.x+r.width,ey=r.y+r.height,i=0;y<ey;y++){
			for(x = r.x;x<ex;i++,x++){
				distination[i] = pixel[x+y*width]&0xff;
			}
		}
		return distination;
	}

	public byte[] copy(byte[] distination,Rectangle r){
		if(distination==null ||distination.length < r.width*r.height){
			distination = new byte[r.width*r.height];
		}
		for(int x,y=r.y,ex=r.x+r.width,ey=r.y+r.height,i=0;y<ey;y++){
			for(x = r.x;x<ex;i++,x++){
				distination[i] = pixel[x+y*width];
			}
		}
		return distination;
	}

	public void draw(PixelDataByte p) {
		int w = p.getWidth(),h=p.getHeight();
		int rw,rh;
		if(getWidth()<w)rw=getWidth();
		else rw=w;
		if(getHeight()<h)rh=getHeight();
		else rh=h;
		for(int y=0;y<rh;y++){
			System.arraycopy(p.pixel, rh*w, pixel, y*getWidth(), rw);
		}
	}


	private BufferedImage img;
	public synchronized Image getDirectImage(){
		if(img==null)
			img = createImage(pixel, width, height);
		return img;
	}

	/**
	 * グレー画像を作成します
	 * @param data イメージデータ
	 * @param width 画像幅
	 * @param height 画像高さ
	 * @return dataを保持するBufferedImage
	 */
	public static BufferedImage createImage(byte[] data,int width,int height){
		DataBuffer buffer = new DataBufferByte(data, width*height);
		WritableRaster raster = Raster.
				createInterleavedRaster(buffer, width, height, width,
						1,new int[1], new Point());
		ColorModel model = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(model, raster, false, null);
	}

}
