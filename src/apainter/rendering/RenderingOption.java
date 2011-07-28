package apainter.rendering;

import java.awt.Point;
import java.util.HashMap;

import apainter.color.Color;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;

public class RenderingOption {
	static final Color white = new Color(0xffffffff),black = new Color(0xff000000);

	public Color
		frontColor,
		backColor;

	public boolean alphaFixed=false;

	public PixelDataBuffer
		sourcemask,
		/**
		 * destinationのマスクとして使われる。それ以外の意味を持たせても良い。<br>
		 * destinationのマスクとして扱う場合、問答無用でdestinationと同じ大きさであるとして扱われる。
		 */
		destinationmask;

	/**
	 * sourcemaskの左上の原点をマスクをかける対象上のどこにおくか。座標は必ず0以下。<br>
	 *
	 */
	public Point sourcemask_Point=new Point();

	/**
	 * overの全体の透明度
	 */
	public int
		overlayeralph;
	public final HashMap<Object, Object> option = new HashMap<Object, Object>();

	public void setAlphaFixed(boolean b){
		alphaFixed = b;
	}

	public boolean getAlphaFixed(){
		return alphaFixed;
	}

	public RenderingOption(int overalph) {
		this(null,overalph);
	}
	public RenderingOption(PixelDataByteBuffer mask,int overalph) {
		this(white,black,mask,overalph);
	}
	/**
	 *
	 * @param front 前景色
	 * @param back 背景色
	 * @param mask マスクデータ
	 * @param overalph 透明度 0～256
	 */
	public RenderingOption(Color front,Color back,PixelDataBuffer mask,int overalph) {
		if(front!=null)frontColor = front.clone();
		else frontColor = null;
		if(back !=null)backColor = back.clone();
		else backColor = null;
		this.sourcemask = mask;
		overlayeralph = overalph;
	}


	public boolean hasSourceMask(){
		return sourcemask!=null;
	}

	public boolean hasDestinationMask(){
		return destinationmask!=null;
	}
}
