package apainter.rendering;

import java.util.HashMap;

import apainter.color.Color;
import apainter.data.PixelDataByteBuffer;

public class RenderingOption {
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
	public final HashMap<Object, Object> option = new HashMap<Object, Object>();

	public RenderingOption(Color front,Color back,PixelDataByteBuffer mask) {
		this(front,back,mask,null);
	}

	public RenderingOption(Color front,Color back,PixelDataByteBuffer mask,PixelDataByteBuffer mask2) {
		if(front!=null)frontColor = front.clone();
		else frontColor = null;
		if(back !=null)backColor = back.clone();
		else backColor = null;
		this.mask = mask;
		this.mask2 = mask2;
	}
	public boolean hasMask(){
		return mask!=null;
	}
	public boolean hasMask2(){
		return mask2!=null;
	}

}
