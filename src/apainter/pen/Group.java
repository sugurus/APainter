package apainter.pen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

import static apainter.pen.APainterPen.*;

public class Group{
	private int size,width,height,xblocks,yblocks;
	private PenData[] pens;
	private int mode;


	public Group(int size,int width,int height,int xblocks,int yblocks,int mode) {
		if(size < 1)throw new RuntimeException("size error");
		if(width<1)throw new RuntimeException("width"+width);
		if(height<1)throw new RuntimeException("height:"+height);
		if(xblocks<1)throw new RuntimeException("xblocks"+xblocks);
		if(yblocks<1)throw new RuntimeException("yblocks"+yblocks);
		this.size = size;
		this.width=width;
		this.height = height;
		this.xblocks=xblocks;
		this.yblocks=yblocks;
		pens = new PenData[xblocks*yblocks];
		if(mode<0 || mode >2)throw new RuntimeException("unsupertedMode "+mode);
		this.mode = mode;
	}

	public PenData[] getPens(){
		return pens.clone();
	}


	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public int getXBlocks(){
		return xblocks;
	}

	public int getYBlocks(){
		return yblocks;
	}

	public PenData createPen(int x,int y){
		if(x <0 || x >=xblocks)throw new RuntimeException("x :"+x);
		if(y<0 || y >=yblocks)throw new RuntimeException("y :"+y);
		PenData p=null;
		if(mode==0){
			p = new Pen8bitGray(width,height,x,y);
		}else{
			//TODO
		}
		pens[x+y*xblocks] = p;
		return p;
	}

	public Pen8bitGray createPen(int x,int y,byte[] data){
		if(x <0 || x >=xblocks)throw new RuntimeException("x :"+x);
		if(y<0 || y >=yblocks)throw new RuntimeException("y :"+y);
		Pen8bitGray p = new Pen8bitGray(width,height,x,y,data);
		pens[x+y*xblocks] = p;
		return p;
	}

	public int getSize(){
		return size;
	}

	public void write(OutputStream out) throws IOException{
		if(mode==0){
			ByteArrayOutputStream b = new ByteArrayOutputStream(10000);
			for(PenData p :pens){
				if(p==null)throw new Error();
				Pen8bitGray pp = (Pen8bitGray)p;
				b.write(pp.mapdata);
			}
			Deflater def = new Deflater();
			def.setInput(b.toByteArray());
			def.finish();
			b.reset();
			byte[] bb = new byte[10000];
			int write;
			do{
				write = def.deflate(bb);
				b.write(bb,0,write);
			}while(write!=0);
			def.end();
			writeShort(out, 0xff30);
			writeShort(out,size);
			writeShort(out,width);
			writeShort(out,height);
			writeShort(out,xblocks);
			writeShort(out,yblocks);
			writeShort(out,0xff31);
			writeInt(out,b.size());
			out.write(b.toByteArray());
			writeShort(out,0xffff);
		}
	}

	public PenData getPen(int x, int y) {
		return pens[x+y*xblocks];
	}


}