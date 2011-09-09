package apainter.data;

import java.util.zip.Deflater;
import java.util.zip.Inflater;

import apainter.io.AOutPutStream;
import apainter.io.SeekOutPutStream;

public class CompressedIntPixelData extends CompressedPixelData{
	private int size;

	public CompressedIntPixelData(PixelDataIntBuffer p) {
		compress(p.getData(), p.width, p.height, 0, 0, p.width, p.height);
	}

	public CompressedIntPixelData(PixelDataIntBuffer p,int x,int y,int subw,int subh){
		compress(p.getData(), p.width, p.height, x, y, subw, subh);
	}



	private void compress(int[] pixel,int width,int height,int x,int y,int subw,int subh){
		if(x<0 || y<0||subw<=0||subh <=0)
			throw new RuntimeException(String.format("x:%d,y:%d,subw:%d,subh:%d",x,y,subw,subh));
		if(x+subw > width || y+subh > height)
			throw new RuntimeException(String.format
					("width=%d,x+subw=%d,  height=%d,y+subh=%d",width,x+subw,height,y+subh));
		if(subh*subw*8 < 0)throw new Error("data is too large");
		this.width = subw;
		this.height = subh;
		this.size = subw*subh;
		SeekOutPutStream o = new SeekOutPutStream(size*8);

		int c,al,rr,gg,bb,ba = 0,brr=0,bgg=0,bbb=0,write;
		int rseekPoint = size*2,gseeekPoint = size*4,bseekPoint=size*6,aseekPoint =0;

		//1行目　横差分
		for(int k=0;k<subw;k++){
			c = pixel[x+k+y*width];
			al = c>>>24;
			rr = c>>16&0xff;
			gg = c>>8 &0xff;
			bb = c & 0xff;
			o.seek(aseekPoint);
			o.writeShort(ba-al+255);
			aseekPoint+=2;
			o.seek(rseekPoint);
			o.writeShort(brr-rr+255);
			rseekPoint+=2;
			o.seek(gseeekPoint);
			o.writeShort(bgg-gg+255);
			gseeekPoint+=2;
			o.seek(bseekPoint);
			o.writeShort(bbb-bb+255);
			bseekPoint+=2;
			ba = al;
			brr = rr;
			bgg = gg;
			bbb = bb;
		}

		//2列目以降　縦差分
		for(int k=1;k<subh;k++){
			for(int l=0;l < subw;l++){
				c = pixel[x+l+(y+k-1)*width];//上の値
				ba = c>>>24;
				brr = c>>16&0xff;
				bgg = c>>8 &0xff;
				bbb = c & 0xff;

				c = pixel[x+l+(y+k)*width];//対象の値
				al = c>>>24;
				rr = c>>16&0xff;
				gg = c>>8 &0xff;
				bb = c & 0xff;
				o.seek(aseekPoint);
				o.writeShort(ba-al+255);
				aseekPoint+=2;
				o.seek(rseekPoint);
				o.writeShort(brr-rr+255);
				rseekPoint+=2;
				o.seek(gseeekPoint);
				o.writeShort(bgg-gg+255);
				gseeekPoint+=2;
				o.seek(bseekPoint);
				o.writeShort(bbb-bb+255);
				bseekPoint+=2;
			}
		}

		Deflater deflater = new Deflater();
		byte[] buffer = o.toByteArray();
		deflater.setInput(buffer,0,buffer.length);
		deflater.finish();
		AOutPutStream oo = new AOutPutStream(size*2);
		buffer = new byte[Math.max(size>>2,4)];
		while((write=deflater.deflate(buffer, 0, buffer.length))!=0){
			oo.write(buffer,0,write);
		}
		deflater.end();

		this.binary = oo.toByteArray();
		this.binarySize = oo.size();
	}



	@Override
	public synchronized PixelDataIntBuffer inflate() {
		if(flushed)throw new RuntimeException("flushed");
		try{
			Inflater inf = new Inflater();
			int bsize = 100000;
			byte[] buffer = new byte[bsize];
			inf.setInput(binary, 0, binarySize);
			int[] data = new int[size];
			int shift = 24;
			int imp,read,index=0;
			int blength=inf.inflate(buffer);
			int bpos=0;

			for(int i=4;i>0;i--){
				//1列目　横差分
				imp = 0;
				for(index=0;index<width;index++){
					read = ((buffer[bpos++]&0xff)<<8)|(buffer[bpos++]&0xff);
					imp = imp-read+255;
					data[index] |= imp<<shift;
					if(bpos==blength){
						blength=inf.inflate(buffer);
						bpos=0;
					}
				}

				//2列目以降　縦差分
				while(index<size){
					read = ((buffer[bpos++]&0xff)<<8)|(buffer[bpos++]&0xff);
					data[index] |= ((data[index-width]>>shift &0xff)-read+255)<<shift;
					index++;
					if(bpos==blength){
						blength=inf.inflate(buffer);
						bpos=0;
					}
				}

				shift -=8;
			}

			inf.end();
			PixelDataIntBuffer p = new PixelDataIntBuffer(width, height, data);
			return p;
		}catch (Exception e) {
			throw new Error();
		}
	}
}
