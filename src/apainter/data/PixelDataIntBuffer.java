package apainter.data;

public class PixelDataIntBuffer extends PixelDataBuffer{

	public static PixelDataIntBuffer create(int w,int h){
		if(w<=0||h<=0)throw new IllegalArgumentException(String.format("w:%d,h:%d",w,h));
		return new PixelDataIntBuffer(w, h, new int[w*h]);
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
	public static PixelDataIntBuffer create(int w,int h,int[] data,
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
		return new PixelDataIntBuffer(subw, subh, t);
	}

	private int[] pixel;

	public PixelDataIntBuffer(int w,int h,int[] pixel) {
		super(w,h);
		if(pixel.length!=w*h)throw new RuntimeException(String.format("pixel length(%d) != w(%d)*h(%d)",pixel.length,w,h));
		this.pixel = pixel;
	}


	@Override
	public int[] getData() {
		return pixel;
	}

}
