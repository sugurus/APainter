package apainter.data;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;


/**
 * 渡されたデータはコピーせずそのまま保持します。
 * @author nodamushi
 *
 */
public abstract class PixelDataBuffer implements Cloneable{
	private static Class<?> intarr = (new int[1]).getClass(),bytearr = (new byte[1]).getClass();


	/**
	 * 渡されたオブジェクトを保持するバッファーを作成します。
	 * @param w
	 * @param h
	 * @param o
	 * @return
	 */
	public static PixelDataBuffer createBuffer(int w,int h,Object o){
		if(o.getClass().isArray()){
			if(o.getClass().equals(intarr)){
				return new PixelDataIntBuffer(w, h, (int[])o);
			}else if(o.getClass().equals(bytearr)){
				return new PixelDataByteBuffer(w, h, (byte[])o);
			}
		}
		throw new RuntimeException("このオブジェクトに対応していません。"+o);
	}



	/**
	 * オブジェクトのクローンを保持するバッファーを作成します。
	 * @param w
	 * @param h
	 * @param o
	 * @return
	 */
	public static PixelDataBuffer createCopyBuffer(int w,int h,Object o){
		if(o.getClass().isArray()){
			if(o.getClass().equals(intarr)){
				return new PixelDataIntBuffer(w, h, ((int[])o).clone());
			}else if(o.getClass().equals(bytearr)){
				return new PixelDataByteBuffer(w, h, ((byte[])o).clone());
			}
		}
		throw new RuntimeException("このオブジェクトに対応していません。"+o);
	}


	public final int width,height;
	private final Rectangle r = new Rectangle();

	public PixelDataBuffer(int w,int h) {
		if(w <=0 || h <= 0)throw new RuntimeException(String.format("size error w:%d,h%d",w,h));
		width = w;
		height =h;
		r.height = h;
		r.width = w;
	}

	public boolean contains(Point2D p){
		double x = p.getX(),y = p.getY();
		return x >=0 && x < width && y>=0 && y <height;
	}

	public boolean contains(double x, double y) {
		return x >=0 && x < width && y>=0 && y <height;
	}
	public boolean contains(int x, int y) {
		return x >=0 && x < width && y>=0 && y <height;
	}

	public boolean contains(Rectangle rectangle){
		return r.contains(rectangle);
	}

	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public Dimension getSize(){
		return new Dimension(width, height);
	}

	/**
	 * 0,0,width,heightの矩形を返します
	 * @return
	 */
	public Rectangle getBounds(){
		return new Rectangle(0, 0, width, height);
	}

	public Rectangle intersection(Rectangle r){
		return new Rectangle(0, 0, width, height).intersection(r);
	}

	/**
	 * p.x,p.y,width,heightの矩形を返します
	 * @param p
	 * @return
	 */
	public Rectangle getBounds(Point p){
		return new Rectangle(p.x,p.y,width,height);
	}


	public abstract void dispose();
	abstract Object getData();
	public abstract PixelDataBuffer clone();
	public abstract PixelDataBuffer copy(Rectangle r);
}
