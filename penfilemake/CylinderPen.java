import static java.lang.Math.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import apainter.pen.APainterPen;
import apainter.pen.Group;
import apainter.pen.PenPackage;


public class CylinderPen {
	static final File f = new File("cylinder.apen");
	static final int core = Runtime.getRuntime().availableProcessors();
	static ExecutorService es = Executors.newFixedThreadPool(core);
	public static void main(String[] args) throws IOException {
		APainterPen pen = new APainterPen();
		PenPackage pac = pen.createPackage("cylinder", 0);
		for(int size = 1;size <= 100;size++){
			System.out.println(size);
			createGroup(size, pac);
		}
		FileOutputStream out=null;
		try{
			out = new FileOutputStream(f);
			pen.write(out);
		}finally{
			if(out!=null)
				out.close();
			es.shutdown();
		}
	}

	private static void createGroup(final int size,PenPackage pac){
		final int w,bl;
		final double r = size/20d;
		if(size <=10){
			w = 3;
			bl = 8+1;
		}else if(size < 20){
			w = 3;
			bl = 16+1;
		}else{
			w = 1 + (((size/10)+1)/2)*2;
			if(size < 40){
				bl=8+1;
			}else if(size < 60){
				bl = 8+1;
			}else if(size < 80){
				bl = 4+1;
			}else{
				bl = 2+1;
			}
		}
		final Group g = pac.createGroup(size, w, w, bl, bl);
		final int c = w-1>>1;
		Future<?>[] fs = new Future[core];
		for(int ii =0;ii<core;ii++){
			final int scy = bl*ii/core;
			final int ecy = bl*(ii+1)/core;
			Runnable run = new Runnable() {

				@Override
				public void run() {
					for(int cy=scy;cy<ecy;cy++){
						double CY = c+(double)(cy*2+1)/(bl*2);
						for(int cx=0;cx<bl;cx++){
							double CX = c+(double)(cx*2+1)/(bl*2);
							byte[] data = new byte[w*w];

							for(int y =0;y<w;y++){
								for(int x=0;x<w;x++){

									int d=0;
									for(double dy=0;dy<1;dy+=1/16d){
										double Y = y+dy-CY;
										for(double dx=0;dx<1;dx+=1/16d){
											double X = x+dx-CX;
											d+=value(X,Y, r);
										}
									}
									data[x+y*w] = (byte) (d/16/16);

								}
							}
							if(size == 90)
							{
								int ww=0;
								StringBuilder sb = new StringBuilder();
								for(byte b:data){
									sb.append(b&0xff).append("\t");
									ww++;
									if(ww==w){
										sb.append("\n");
										ww=0;
									}
								}
								sb.append("\n\n");
								System.out.println(sb);
							}
							g.createPen(cx, cy, data);
						}
					}

				}
			};

			fs[ii] = es.submit(run);
		}
		for(Future<?> f:fs){
			try {
				f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}


	private static int value(double x,double y,double r){
		double l=hypot(x, y);
		if(l<=r)return 255;
		else return 0;
	}

}
