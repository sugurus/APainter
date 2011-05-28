package apainter.data;


public abstract class CompressedPixelData {

	public CompressedPixelData compress(PixelDataBuffer p){
		if(p==null)throw new NullPointerException("p");
		if(p instanceof IntPixelDataBuffer){
			return new CompressedIntPixelData((IntPixelDataBuffer)p);
		}
		else if(p instanceof BytePixelDataBuffer){

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
	public int height(){
		return height;
	}


	/**
	 * 解凍します。
	 * @return
	 */
	abstract public PixelDataBuffer inflate();

	/**
	 * 保持しているデータを解放します。
	 */
	public void flush(){
		binary = null;
		width =height=binarySize= -1;
		flushed = true;
	}

}
