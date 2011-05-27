package apainter.data;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import apainter.io.AOutPutStream;
import apainter.io.SeekOutPutStream;

class IntCompressedData extends CompressedData{
	private int size;
	public IntCompressedData(IntPixelDataBuffer p) {
		compress(p.getData(), p.width, p.height, 0, 0, p.width, p.height);
	}

	public IntCompressedData(IntPixelDataBuffer p,int x,int y,int subw,int subh){
		compress(p.getData(), p.width, p.height, x, y, subw, subh);
	}



	private void compress(int[] pixel,int width,int height,int x,int y,int subw,int subh){
		if(x+subw > width || y+subh > height)
			throw new RuntimeException(String.format
					("width=%d,x+subw=%d,  height=%d,y+subh=%d",width,x+subw,height,y+subh));
		if(subh*subw*6+4 < 0)throw new Error("data is too large");
		this.width = subw;
		this.height = subh;
		size = subw*subh;

		SeekOutPutStream
		o = new SeekOutPutStream(size*8);

		int c;//pixelを読み込んで格納する
		int ba = 0,brr=0,bgg=0,bbb=0;
		int al,rr,gg,bb;
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
		int write;
		buffer = new byte[Math.max(size>>2,4)];
		while((write=deflater.deflate(buffer, 0, buffer.length))!=0){
			o.write(buffer,0,write);
		}
		deflater.end();
		binary = oo.getBufferData();
		binarySize = oo.size();
	}



	private Inflater inf;
	byte[] imp = new byte[2];
	@Override
	public synchronized PixelDataBuffer inflate() {
		inf = new Inflater();
		inf.setInput(binary, 0, binarySize);
		int[] data = new int[size];
		readShorts(data);
		//alph 一列目　横差分
		int ba =0;
		int i=0;
		for(;i<width;i++){
			ba = ba-data[i]+255;
			data[i] = ba <<24;
		}
		//2列目　縦差分
		while(i<size){
			ba = (data[i-width]>>>24)-data[i]+255;
			data[i] = ba<<24;
		}

		//rgb　書くのが面倒だからループさせちゃった。何時か気が向いたら展開する
		int shift = 16;
		for(int t=0;t<3;t++){
			readShorts(data);
			ba = 0;
			for(i=0;i<width;i++){
				ba = ba-(data[i]&0xff)+255;
				data[i] = (data[i]&0xffffff00) | ba<<shift;
			}

			//2列目　縦差分
			while(i<size){
				ba = (data[i-width]>>shift &0xff)-(data[i]&0xff)+255;
				data[i] = (data[i]&0xffffff00) | ba<<shift;
			}
			shift -=8;
		}

		inf.end();
		inf = null;
		IntPixelDataBuffer p = new IntPixelDataBuffer(width, height, data);
		return p;
	}

	//下位8ビットに書き込み(メモリをけちるため)
	private void readShorts(int[] into){
		try {
			for(int i=0;i<into.length;i++){
				inf.inflate(imp, 0, 2);
				into[i] = (into[i]&0xffffff00)|((imp[0]&0xff)<<8)|(imp[1]&0xff);
			}
		} catch (DataFormatException e) {
			throw new Error();
		}
	}

}
