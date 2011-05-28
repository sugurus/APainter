package apainter.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;

public class AOutPutStream extends ByteArrayOutputStream{
	byte[] imp = new byte[8];
	public AOutPutStream() {
		super(1024*512);
	}

	public AOutPutStream(int length){
		super(length);
	}

	public void writeInt(int i){
		imp[0] =(byte) (i>>>24);
		imp[1] =(byte) (i>>>16&0xff);
		imp[2] =(byte) (i>>>8&0xff);
		imp[3] =(byte) (i&0xff);
		write(imp,0,4);
	}

	public void writeShort(int i){
		imp[0]=(byte) (i>>>8&0xff);
		imp[1]=(byte) (i&0xff);
		write(imp,0,2);
	}
	public void writeShort(short s){
		imp[0]=(byte) (s>>>8&0xff);
		imp[1]=(byte) (s&0xff);
		write(imp,0,2);
	}
	public void writeLong(long l){
		imp[0]=(byte) (l>>>56&0xff);
		imp[1]=(byte) (l>>>48&0xff);
		imp[2]=(byte) (l>>>40&0xff);
		imp[3]=(byte) (l>>>32&0xff);
		imp[4]=(byte) (l>>>24&0xff);
		imp[5]=(byte) (l>>>16&0xff);
		imp[6]=(byte) (l>>>8&0xff);
		imp[7]=(byte) (l&0xff);
		write(imp,0,8);
	}
	public void writeDeflater(Deflater d){
		int write;
		byte[] buffer = new byte[10000];
		ByteArrayOutputStream o = new ByteArrayOutputStream(10000);
		write = d.deflate(buffer, 0, 10000);
		while(write!=0){
			o.write(buffer, 0, write);
			write = d.deflate(buffer,0,10000);
		}
		writeInt(d.getTotalOut());
		try {
			o.writeTo(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeString(String str){
		byte[] b;
		try {
			b = str.getBytes("UTF-8");
			int ln = b.length;
			writeInt(ln);
			write(b,0,ln);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public byte[] deflate(){
		Deflater deflater = new Deflater();
		byte[] buffer = buf;
		deflater.setInput(buffer, 0, buffer.length);
		deflater.finish();
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		int write = deflater.deflate(buffer,0,buffer.length);
		while(write!=0){
		    o.write(buffer,0,write);
		    write = deflater.deflate(buffer,0,buffer.length);
		}
		deflater.end();
		return o.toByteArray();
	}

	public byte[] getBufferData(){
		return buf;
	}

}
