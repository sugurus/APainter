package apainter.data;


public abstract class CompressedPixelData {

	/**
	 * 渡されたデータを圧縮します
	 * @param p
	 * @return
	 */
	public static CompressedPixelData compress(PixelData p){
		if(p==null)throw new NullPointerException("p");
		if(p instanceof PixelDataInt){
			return new CompressedIntPixelData((PixelDataInt)p);
		}
		else if(p instanceof PixelDataByte){
			return new CompressedBytePixelData((PixelDataByte)p);
		}else if(p instanceof PixelData15BitColor){
			return new Compressed15BitPixelData((PixelData15BitColor) p);
		}

		throw new RuntimeException(String.format("この形式に対応していません:class %d",p.getClass().getName()));
	}

	/**
	 * 渡されたデータの一部を圧縮します。
	 * @param p
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static CompressedPixelData compress(PixelData p,
			int x,int y,int width,int height){
		if(p==null)throw new NullPointerException("p");
		if(p instanceof PixelDataInt){
			return new CompressedIntPixelData((PixelDataInt)p,x,y,width,height);
		}
		else if(p instanceof PixelDataByte){
			return new CompressedBytePixelData((PixelDataByte)p,x,y,width,height);
		}

		throw new RuntimeException(String.format("この形式に対応していません:class %d",p.getClass().getName()));
	}

	protected int width,height;
	protected byte[] binary;
	protected int binarySize;
	protected boolean flushed=false;

	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}

	public int dataSize(){
		return binarySize;
	}


	/**
	 * 解凍します。
	 * @return
	 */
	abstract public PixelData inflate();

	/**
	 * 保持しているデータを解放します。
	 */
	public void flush(){
		binary = null;
		width =height=binarySize= -1;
		flushed = true;
	}

}
