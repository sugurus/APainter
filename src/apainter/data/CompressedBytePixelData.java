package apainter.data;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import apainter.io.AOutPutStream;

public class CompressedBytePixelData extends CompressedPixelData{

	private int size;

	public CompressedBytePixelData(PixelDataByteBuffer p) {
		compress(p.getData(), p.width, p.height, 0, 0, p.width, p.height);
	}

	public CompressedBytePixelData(PixelDataByteBuffer p,int x,int y,int subw,int subh){
		compress(p.getData(), p.width, p.height, x, y, subw, subh);
	}

	private void compress(
			byte[] pixel,int width,int height,
			int x,int y,int subw,int subh){
		if(x<0 || y<0||subw<=0||subh <=0)
			throw new RuntimeException(String.format("x:%d,y:%d,subw:%d,subh:%d",x,y,subw,subh));
		if(x+subw > width || y+subh > height)
			throw new RuntimeException(String.format
					("width=%d,x+subw=%d,  height=%d,y+subh=%d",width,x+subw,height,y+subh));
		this.width = subw;
		this.height = subh;
		this.size = subw*subh;
		AOutPutStream o = new AOutPutStream(size*2);
		int c,bc=0,write;
		//1行目　横差分
		for(int l=0;l<subw;l++){
			c = pixel[x+l+y*width]&0xff;
			o.writeShort(bc-c+255);
			bc=c;
		}
		//2行目　縦差分
		for(int k=1;k<subh;k++){
			for(int l=0;l<subw;l++){
				bc = pixel[x+l+(y+k-1)*width]&0xff;
				c = pixel[x+l+(y+k)*width]&0xff;
				o.writeShort(bc-c+255);
			}
		}

		Deflater deflater = new Deflater();
		byte[] buffer = o.toByteArray();
		deflater.setInput(buffer,0,buffer.length);
		deflater.finish();
		AOutPutStream oo = new AOutPutStream(size);
		buffer = new byte[Math.max(size>>2,4)];
		while((write=deflater.deflate(buffer, 0, buffer.length))!=0){
			oo.write(buffer,0,write);
		}
		deflater.end();

		this.binary = oo.toByteArray();
		this.binarySize = oo.size();
	}

//	private Inflater inf;
//	byte[] bytes = new byte[2];
//	//infから2byte読み込む
//	private int read() throws DataFormatException{
//		inf.inflate(bytes, 0, 2);
//		return ((bytes[0]&0xff)<<8)|(bytes[1]&0xff);
//	}

	@Override
	public PixelDataByteBuffer inflate() {
		if(flushed)throw new RuntimeException("flushed");
		try{
			byte[] bytes = new byte[2];
			Inflater inf = new Inflater();
			inf.setInput(binary, 0, binarySize);
			byte[] data = new byte[size];
			int imp=0,read,index=0;

			//1行目　横差分
			for(index=0;index<width;index++){
				inf.inflate(bytes, 0, 2);
				read = ((bytes[0]&0xff)<<8)|(bytes[1]&0xff);
				imp = imp-read+255;
				data[index] = (byte) imp;
			}

			//2行目　縦差分
			while(index<size){
				inf.inflate(bytes, 0, 2);
				read = ((bytes[0]&0xff)<<8)|(bytes[1]&0xff);
				data[index] = (byte) ((data[index-width] &0xff)-read+255);
				index++;
			}
			inf.end();
			PixelDataByteBuffer p = new PixelDataByteBuffer(width, height, data);
			return p;
		}catch (DataFormatException e) {
			throw new Error();
		}
	}

}
