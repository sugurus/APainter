package apainter.data;

import static apainter.misc.Utility_PixelFunction.*;

import java.util.zip.Deflater;
import java.util.zip.Inflater;

import apainter.io.AOutPutStream;
import apainter.io.SeekOutPutStream;

public class Compressed15BitPixelData extends CompressedPixelData{
	private int size;

	public Compressed15BitPixelData(PixelData15BitColor buffer) {
		PixelDataInt i1,i2;
		i1 = buffer.getIntegerBuffer();
		i2 = buffer.getDecimalBuffer();
		compress(i1.getData(), i2.getData(), buffer.getWidth(), buffer.getHeight(),
				0, 0, buffer.getWidth(), buffer.getHeight());
	}

	public Compressed15BitPixelData(PixelData15BitColor buffer,int x,int y,int subw,int subh) {
		PixelDataInt i1,i2;
		i1 = buffer.getIntegerBuffer();
		i2 = buffer.getDecimalBuffer();
		compress(i1.getData(), i2.getData(), buffer.getWidth(), buffer.getHeight(),
				x, y, subw, subh);
	}

	private void compress(int[] pixel_u,int[] pixel_d,int width,int height,int x,int y,int subw,int subh){
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

		int al,rr,gg,bb,ba = 0,brr=0,bgg=0,bbb=0,write,cu,cd;
		int rseekPoint = size*2,gseeekPoint = size*4,bseekPoint=size*6,aseekPoint =0;

		//1行目　横差分
		for(int k=0;k<subw;k++){
			cu = pixel_u[x+k+y*width];
			cd = pixel_d[x+k+y*width];
			al = a(cu)<<7 | a(cd);
			rr = r(cu)<<7 | r(cd);
			gg = g(cu)<<7 | g(cd);
			bb = b(cu)<<7 | b(cd);
			o.seek(aseekPoint);
			o.writeShort(ba-al+32767);
			aseekPoint+=2;
			o.seek(rseekPoint);
			o.writeShort(brr-rr+32767);
			rseekPoint+=2;
			o.seek(gseeekPoint);
			o.writeShort(bgg-gg+32767);
			gseeekPoint+=2;
			o.seek(bseekPoint);
			o.writeShort(bbb-bb+32767);
			bseekPoint+=2;
			ba = al;
			brr = rr;
			bgg = gg;
			bbb = bb;
		}

		//2列目以降　縦差分
		for(int k=1;k<subh;k++){
			for(int l=0;l < subw;l++){
				cu = pixel_u[x+l+(y+k-1)*width];//上の値
				cd = pixel_d[x+l+(y+k-1)*width];
				ba = a(cu)<<7 | a(cd);
				brr = r(cu)<<7 | r(cd);
				bgg = g(cu)<<7 | g(cd);
				bbb = b(cu)<<7 | b(cd);

				cu = pixel_u[x+l+(y+k)*width];//対象の値
				cd = pixel_d[x+l+(y+k)*width];
				al = a(cu)<<7 | a(cd);
				rr = r(cu)<<7 | r(cd);
				gg = g(cu)<<7 | g(cd);
				bb = b(cu)<<7 | b(cd);
				o.seek(aseekPoint);
				o.writeShort(ba-al+32767);
				aseekPoint+=2;
				o.seek(rseekPoint);
				o.writeShort(brr-rr+32767);
				rseekPoint+=2;
				o.seek(gseeekPoint);
				o.writeShort(bgg-gg+32767);
				gseeekPoint+=2;
				o.seek(bseekPoint);
				o.writeShort(bbb-bb+32767);
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
	public synchronized PixelData15BitColor inflate() {
		if(flushed)throw new RuntimeException("flushed");
		try{
			Inflater inf = new Inflater();
			int bsize = 100000;
			byte[] buffer = new byte[bsize];
			inf.setInput(binary, 0, binarySize);
			int[] data_u = new int[size];
			int[] data_d =  new int[size];
			int shift = 24;
			int imp,read,index=0,impu,impd;
			int blength=inf.inflate(buffer);
			int bpos=0;

			for(int i=4;i>0;i--){
				//1列目　横差分
				imp = 0;
				for(index=0;index<width;index++){
					read = ((buffer[bpos++]&0xff)<<8)|(buffer[bpos++]&0xff);
					imp = imp-read+32767;
					impu = imp>>>7;
					impd = imp&127;
					data_u[index] |= impu<<shift;
					data_d[index] |= impd<<shift;
					if(bpos==blength){
						blength=inf.inflate(buffer);
						bpos=0;
					}
				}

				//2列目以降　縦差分
				while(index<size){
					read = ((buffer[bpos++]&0xff)<<8)|(buffer[bpos++]&0xff);
					int k=(data_u[index-width]>>shift &0xff)<<7 | (data_d[index-width]>>shift &0xff);
					imp = k-read+32767;
					impu = imp>>>7;
					impd = imp&127;
					data_u[index] |= impu<<shift;
					data_d[index] |= impd<<shift;
					index++;
					if(bpos==blength){
						blength=inf.inflate(buffer);
						bpos=0;
					}
				}

				shift -=8;
			}

			inf.end();
			return new PixelData15BitColor(width, height, data_u, data_d);
		}catch (Exception e) {
			throw new Error();
		}
	}


}

//	private void compress(long[] pixel,int width,int height,int x,int y,int subw,int subh){
//		if(x<0 || y<0||subw<=0||subh <=0)
//			throw new RuntimeException(String.format("x:%d,y:%d,subw:%d,subh:%d",x,y,subw,subh));
//		if(x+subw > width || y+subh > height)
//			throw new RuntimeException(String.format
//					("width=%d,x+subw=%d,  height=%d,y+subh=%d",width,x+subw,height,y+subh));
//		if(subh*subw*8 < 0)throw new Error("data is too large");
//		this.width = subw;
//		this.height = subh;
//		this.size = subw*subh;
//		SeekOutPutStream o = new SeekOutPutStream(size*8);
//
//		int al,rr,gg,bb,ba = 0,brr=0,bgg=0,bbb=0,write,cu,cd;
//		long c;
//		int rseekPoint = size*2,gseeekPoint = size*4,bseekPoint=size*6,aseekPoint =0;
//
//		//1行目　横差分
//		for(int k=0;k<subw;k++){
//			c = pixel[x+k+y*width];
//			cu = (int)(c>>>32);
//			cd = (int)c;
//			al = cu>>>16;
//			rr = cu&0xffff;
//			gg = cd>>>16;
//			bb = cd & 0xffff;
//			o.seek(aseekPoint);
//			o.writeShort(ba-al+32767);
//			aseekPoint+=2;
//			o.seek(rseekPoint);
//			o.writeShort(brr-rr+32767);
//			rseekPoint+=2;
//			o.seek(gseeekPoint);
//			o.writeShort(bgg-gg+32767);
//			gseeekPoint+=2;
//			o.seek(bseekPoint);
//			o.writeShort(bbb-bb+32767);
//			bseekPoint+=2;
//			ba = al;
//			brr = rr;
//			bgg = gg;
//			bbb = bb;
//		}
//
//		//2列目以降　縦差分
//		for(int k=1;k<subh;k++){
//			for(int l=0;l < subw;l++){
//				c = pixel[x+l+(y+k-1)*width];//上の値
//				cu = (int)(c>>>32);
//				cd = (int)c;
//				ba = cu>>>16;
//				brr = cu&0xffff;
//				bgg = cd >>>16;
//				bbb = cd&0xffff;
//
//				c = pixel[x+l+(y+k)*width];//対象の値
//				cu = (int)(c>>>32);
//				cd = (int)c;
//				al = cu>>>16;
//				rr = cu&0xffff;
//				gg = cd >>>16;
//				bb = cd&0xffff;
//				o.seek(aseekPoint);
//				o.writeShort(ba-al+32767);
//				aseekPoint+=2;
//				o.seek(rseekPoint);
//				o.writeShort(brr-rr+32767);
//				rseekPoint+=2;
//				o.seek(gseeekPoint);
//				o.writeShort(bgg-gg+32767);
//				gseeekPoint+=2;
//				o.seek(bseekPoint);
//				o.writeShort(bbb-bb+32767);
//				bseekPoint+=2;
//			}
//		}
//
//		Deflater deflater = new Deflater();
//		byte[] buffer = o.toByteArray();
//		deflater.setInput(buffer,0,buffer.length);
//		deflater.finish();
//		AOutPutStream oo = new AOutPutStream(size*2);
//		buffer = new byte[Math.max(size>>2,4)];
//		while((write=deflater.deflate(buffer, 0, buffer.length))!=0){
//			oo.write(buffer,0,write);
//		}
//		deflater.end();
//
//		this.binary = oo.toByteArray();
//		this.binarySize = oo.size();
//	}