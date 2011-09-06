package apainter.pen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class APainterPen {

	public static void writeShort(OutputStream out,int i) throws IOException{
		out.write((i>>>8)&0xff);
		out.write(i&0xff);
	}
	public static void writeInt(OutputStream out,int i)throws IOException{
		out.write((i>>>24)&0xff);
		out.write((i>>>16)&0xff);
		out.write((i>>>8)&0xff);
		out.write(i&0xff);
	}


	public static int readInt(InputStream in) throws IOException{
		byte[] b = new byte[4];
		int r = in.read(b);
		if(r!=4)throw new IOException();
		return (b[0]&0xff)<<24 | (b[1]&0xff)<<16 | (b[2]&0xff)<<8 | (b[3]&0xff);
	}

	public static int readShort(InputStream in) throws IOException{
		byte[] b = new byte[2];
		int r = in.read(b);
		if(r!=2)throw new IOException();
		return (b[0]&0xff)<<8 | (b[1]&0xff);
	}



	private ArrayList<PenPackage> packagelist = new ArrayList<PenPackage>();

	public PenPackage createPackage(String name,int mode){
		PenPackage p = new PenPackage(name,mode);
		packagelist.add(p);
		return p;
	}


	public static APainterPen decode(InputStream in) throws IOException, DataFormatException{
		byte[] apen01 = "apen01".getBytes();
		byte[] b = new byte[6];
		in.read(b, 0, 6);
		if(!Arrays.equals(apen01, b))throw new RuntimeException("not Apainter Pen version 01 file.");
		int read;
		APainterPen p = new APainterPen();
		WHILE:while((read=in.read())!=-1){
			if(read==0xff){
				read = in.read();

				switch(read){
				case 1://終了
					break WHILE;
				case 0x11://スルー
					int l = readInt(in);
					in.skip(l+2);
					break;
				case 0x20://group
					readPackage(in, p);
					break;
				default:
					throw new IOException("decode error");
				}


			}else{
				throw new IOException("decode error");
			}
		}
		return p;
	}

	private static void readPackage(InputStream in,APainterPen p) throws IOException, DataFormatException{
		PenPackage pac;
		int read = in.read();
		switch(read){
		case 0://8bit gray
			int len = readShort(in);
			read = in.read();
			if(read!=0xff)throw new IOException();
			read = in.read();
			String nm=null;
			if(read==0x21){//name
				int ll = in.read();
				byte[] bb = new byte[ll];
				in.read(bb);
				nm = new String(bb, "utf-8");
				in.read();
				read=in.read();
			}
			pac=p.createPackage(nm,0);
			for(int i=0;i<len;i++){
				if(read!=0x30)throw new IOException();
				readGroup(in, p,pac);
				if(in.read()!=0xff)throw new IOException();
				read = in.read();
			}

			if(read!=0xff)throw new IOException();
			break;
		default:
			throw new IOException();
		}
	}

	private static void readGroup(InputStream in,APainterPen p,PenPackage pac) throws IOException, DataFormatException{
		int size=readShort(in),width=readShort(in),height=readShort(in),
				centerx = readShort(in),centery=readShort(in),
				xblocks=readShort(in),yblocks=readShort(in);
		if(readShort(in)!=0xff31)throw new IOException();
		int l = readInt(in);
		byte[] zipdata=new byte[l];
		if(l!=in.read(zipdata) || 0xffff!=readShort(in))throw new IOException();

		Inflater inf = new Inflater();
		inf.setInput(zipdata);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[10000];
		int write = 1;
		while(write!=0){
			write = inf.inflate(b);
			out.write(b,0,write);
		}
		inf.end();
		byte[] data = out.toByteArray();

		Group g = pac.createGroup(size, width, height,centerx,centery,xblocks, yblocks);
		int pos = 0;
		for(int y=0;y<yblocks;y++){
			for(int x=0;x<xblocks;x++){
				byte[] map = new byte[width*height];
				System.arraycopy(data, pos, map, 0, map.length);
				pos += map.length;
				g.createPen(x, y, map);
			}
		}
	}

	public ArrayList<PenPackage> getPackageList(){
		return new ArrayList<PenPackage>(packagelist);
	}
	public PenPackage getPackage(int pos){
		return pos>=packagelist.size()?null:packagelist.get(pos);
	}
	public void write(OutputStream out) throws IOException{
		if(packagelist.isEmpty())return;
		out.write("apen01".getBytes());
		for(PenPackage p:packagelist){
			p.write(out);
		}
		writeShort(out,0xff01);
	}






}
