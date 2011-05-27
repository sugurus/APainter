package apainter.data;


public abstract class CompressedData {

	public CompressedData compress(PixelDataBuffer p){
		if(p==null)throw new NullPointerException("p");
		if(p instanceof IntPixelDataBuffer){
			return new IntCompressedData((IntPixelDataBuffer)p);
		}
		else if(p instanceof BytePixelDataBuffer){

		}

		throw new RuntimeException(String.format("この形式に対応していません:class %d",p.getClass().getName()));
	}

	protected int width,height;
	protected byte[] binary;
	protected int binarySize;

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

}
