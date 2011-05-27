package apainter.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.Deflater;

public class SeekOutPutStream extends OutputStream{

	protected byte buf[];

	protected int count;



	public SeekOutPutStream(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Negative initial size: "
					+ size);
		}
		buf = new byte[size];
	}

	public synchronized void write(int b) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		buf[count] = (byte)b;
		count = newcount;
	}

	public synchronized void seek(int point){
		if(point < 0)return;
		if(point > buf.length){
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, point));
		}
		count = point;
	}

	public int getPointer(){
		return count;
	}

	public synchronized void write(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) ||
				((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		int newcount = count + len;
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	}

	public synchronized void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, buf.length);
	}

	public synchronized void reset() {
		count = 0;
	}

	public synchronized byte toByteArray()[] {
		return buf;
	}

	public synchronized int size() {
		return buf.length;
	}

	public synchronized String toString() {
		return new String(buf, 0, count);
	}



	public void close(){}



	byte[] imp = new byte[8];
	public SeekOutPutStream() {
		this(1024*512);
	}


	public void writeInt(int i){
		imp[0] =(byte) (i>>>24);
		imp[1] =(byte) (i>>16&0xff);
		imp[2] =(byte) (i>>8&0xff);
		imp[3] =(byte) (i&0xff);
		write(imp,0,4);
	}

	public void writeShort(int i){
		imp[0]=(byte) (i>>8&0xff);
		imp[1]=(byte) (i&0xff);
		write(imp,0,2);
	}
	public void writeShort(short s){
		imp[0]=(byte) (s>>>8&0xff);
		imp[1]=(byte) (s&0xff);
		write(imp,0,2);
	}
	public void writeLong(long l){
		imp[0]=(byte) (l>>56&0xff);
		imp[1]=(byte) (l>>48&0xff);
		imp[2]=(byte) (l>>40&0xff);
		imp[3]=(byte) (l>>32&0xff);
		imp[4]=(byte) (l>>24&0xff);
		imp[5]=(byte) (l>>16&0xff);
		imp[6]=(byte) (l>>8&0xff);
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

}
