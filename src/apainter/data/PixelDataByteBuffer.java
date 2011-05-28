package apainter.data;

/**
 * グレースケール画像を保持するためのバッファー
 * @author nodamushi
 *
 */
public class PixelDataByteBuffer extends PixelDataBuffer{

	public static PixelDataByteBuffer create(int w,int h){
		if(w<=0||h<=0)throw new IllegalArgumentException(String.format("w:%d,h:%d",w,h));
		return new PixelDataByteBuffer(w, h, new byte[w*h]);
	}

	public static PixelDataByteBuffer create(int w,int h,byte[] data,
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
		return new PixelDataByteBuffer(subw, subh, t);
	}

	/**
	 * 配列のコピーをデータに持つバッファーを作成します。
	 * @param w
	 * @param h
	 * @param b
	 * @return
	 */
	public static PixelDataByteBuffer copyBuffer(int w,int h,byte[] b){
		return new PixelDataByteBuffer(w, h, b.clone());
	}

	private byte[] pixel;

	public PixelDataByteBuffer(int w,int h,byte[] pixel) {
		super(w,h);
		if(pixel.length!=w*h)throw new RuntimeException(String.format("pixel length(%d) != w(%d)*h(%d)",pixel.length,w,h));
		this.pixel = pixel;
	}


	@Override
	byte[] getData() {
		return pixel;
	}

}
