package apainter.rendering;

import java.util.HashMap;

import apainter.color.Color;
import apainter.data.PixelDataByteBuffer;

public class RenderingOption {
	static final Color white = new Color(0xffffffff),black = new Color(0xff000000);

	public Color
		frontColor,
		backColor;
	public PixelDataByteBuffer
	/**
	 * overのマスクとして使われる。それ以外の意味を持たせても良い。<br>
	 * overのマスクとして扱う場合、問答無用でoverと同じ大きさであるとして扱われる。
	 */
		mask,
		/**
		 * over2のマスクとして使われる。それ以外の意味を持たせても良い。
		 * over2のマスクとして扱う場合、問答無用でover2と同じ大きさであるとして扱われる。
		 */
		mask2;
	public int
		overlayeralph,
		over2layeralph;
	public final HashMap<Object, Object> option = new HashMap<Object, Object>();


	public RenderingOption(int overalph) {
		this(null,overalph);
	}
	public RenderingOption(PixelDataByteBuffer mask,int overalph) {
		this(white,black,mask,overalph,null,256);
	}
	/**
	 *
	 * @param front 前景色
	 * @param back 背景色
	 * @param mask マスクデータ
	 * @param overalph 透明度 0～256
	 */
	public RenderingOption(Color front,Color back,PixelDataByteBuffer mask,int overalph) {
		this(front,back,mask,overalph,null,256);
	}

	/**
	 *
	 * @param front 前景色
	 * @param back 背景色
	 * @param mask マスクデータ
	 * @param overalph 透明度 0～256
	 * @param mask2 over2に対するマスクデータ
	 * @param over2alph 透明度0～256
	 */
	public RenderingOption(Color front,Color back,PixelDataByteBuffer mask,int overalph,PixelDataByteBuffer mask2,int over2alph) {
		if(front!=null)frontColor = front.clone();
		else frontColor = null;
		if(back !=null)backColor = back.clone();
		else backColor = null;
		this.mask = mask;
		this.mask2 = mask2;
		overlayeralph = overalph;
		over2layeralph = over2alph;
	}
	public boolean hasMask(){
		return mask!=null;
	}
	public boolean hasMask2(){
		return mask2!=null;
	}

}
