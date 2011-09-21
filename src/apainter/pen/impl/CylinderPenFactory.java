package apainter.pen.impl;

import static java.lang.Math.*;

import java.net.URL;
import java.util.Set;

import apainter.pen.Group;


public class CylinderPenFactory extends URLPenFactory{
	static private final URL url = CylinderPenFactory.class.getResource("cylinder.apen");


	public CylinderPenFactory(long id) {
		super(id, url);
	}



	@Override
	public synchronized void release() {
		Set<Integer> set = cpudata.keySet();
		for(int i :set){
			if(i>100)cpudata.remove(i);
		}
	}


	@Override
	protected double getIntervalLength(int size, double intervalpercent) {
		if(size <= 20){
			double w= size*0.03*intervalpercent;
			if(w < 0.01)return 0.01;
			else return w;
		}
		if(size <= 40){
			double w= size*0.025*intervalpercent;
			if(w < 0.01)return 0.01;
			else return w;
		}
		if(size <= 60){
			double w= size*0.020*intervalpercent;
			if(w < 0.01)return 0.01;
			else return w;
		}
		if(size <= 80){
			double w= size*0.015*intervalpercent;
			if(w < 0.01)return 0.01;
			else return w;
		}
		else if(size <= 100){
			double w= size*0.01*intervalpercent;
			if(w < 0.01)return 0.01;
			else return w;
		}
		return 0.005*size;

	}


	private final double k = 1.2;

	@Override
	protected Group findGroup(int size) {
		Group g = cpudata.get(size);
		if(g!=null)return g;

		size = size/10;
		size = size*10;//小数部の削除
		g = cpudata.get(size);
		if(g!=null)return g;

		double r = size/20d,rr_=(r-k/2)*(r-k/2),rr=(r+k/2)*(r+k/2);
		int w = 1 + (((size/10)+1)/2)*2;
		double c = (w-1>>1);

		int bl = size < 1000?3:1;

		g = new Group(size, w, w,(w-1)>>1,(w-1)>>1, bl, bl, 0);

		for(int yb=0;yb<bl;yb++){
			double cyb = (c+(2*yb+1)/6d);
			for(int xb=0;xb<bl;xb++){
				double cxb = (c+(2*xb+1)/6d);
				byte[] data = new byte[w*w];
				for(int y=0;y<w;y++){
					double Y = y-cyb+0.5;
					for(int x=0;x<w;x++){
						double X = x-cxb+0.5;
						double l = X*X+Y*Y;
						if(l > rr)data[x+y*w] = 0;
						else if(l < rr_)data[x+y*w] = (byte)255;
						else{
							l = r+k/2 -sqrt(l);
							data[x+y*w] = (byte)(int)(l/k*255);
						}
					}
					g.createPen(xb, yb, data);
				}
			}
		}



		cpudata.put(size, g);
		return g;
	}





}


