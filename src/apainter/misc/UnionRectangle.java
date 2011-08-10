package apainter.misc;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class UnionRectangle {
	private Area area = new Area();
	private boolean empty = true;

	public void add(Rectangle r){
		if(r.isEmpty())return;
		empty = false;
		area.add(new Area(r));
	}

	public boolean isEmpty(){
		return empty;
	}

	public void add(Rectangle... r){
		for(Rectangle rect:r){
			add(rect);
		}
	}

	public Rectangle[] divide(){
		XLine[] xlines;
		YLines ylines;
		{
			ArrayList<XLine> x = new ArrayList<XLine>();
			ArrayList<YLine> y = new ArrayList<YLine>();
			toLine(x, y);
			xlines = x.toArray(new XLine[x.size()]);
			Arrays.sort(xlines);
			ylines = new YLines(y.size());
		}

		ArrayList<Rectangle> rect = new ArrayList<Rectangle>();
		for(XLine x:xlines){
			YLine joinstart=x.getJoinStart(),joinend=x.getJoinEnd();

			if(x.increase){

				if(joinstart.increase){
					if(joinend.increase){

						YLine nright = nearestYLine_fromRight(x, ylines);
						if(nright==null)return new Rectangle[]{area.getBounds()};
						int w = nright.x-x.end;
						int h = nright.start-x.y;
						rect.add(new Rectangle(x.end,x.y,w,h));
						nright.setStart(x.y);
						ylines.add(joinstart);
						ylines.remove(joinend);
					}else{
						ylines.add(joinstart);
						ylines.add(joinend);
					}
				}else{
					if(joinend.increase){

						YLine nleft = nearestYLine_fromLeft(x, ylines);
						if(nleft==null)return new Rectangle[]{area.getBounds()};
						int w=x.start-nleft.x;
						int h = nleft.end-x.y;
						rect.add(new Rectangle(nleft.x,x.y,w,h));
						nleft.setEnd(x.y);

						YLine nright = nearestYLine_fromRight(x, ylines);
						if(nright==null)return new Rectangle[]{area.getBounds()};
						w = nright.x-x.end;
						h = nright.start-x.y;
						rect.add(new Rectangle(x.end,x.y,w,h));
						nright.setStart(x.y);
						ylines.remove(joinstart);
						ylines.remove(joinend);


					}else{
						YLine nleft = nearestYLine_fromLeft(x, ylines);
						if(nleft==null)return new Rectangle[]{area.getBounds()};
						int w=x.start-nleft.x;
						int h = nleft.end-x.y;
						rect.add(new Rectangle(nleft.x,x.y,w,h));
						nleft.setEnd(x.y);
						ylines.remove(joinstart);
						ylines.add(joinend);
					}
				}


			}else{
				if(joinstart.increase){
					if(joinend.increase){
						YLine nright = nearestYLine_fromRight(x, ylines);
						if(nright==null)return new Rectangle[]{area.getBounds()};
						int w = nright.x-x.end;
						int h = nright.start-x.y;
						rect.add(new Rectangle(x.end,x.y,w,h));
						nright.setStart(x.y);
						ylines.remove(joinend);
						ylines.add(joinstart);
					}else{
						YLine nright = nearestYLine_fromRight(x, ylines),nleft = nearestYLine_fromLeft(x, ylines);
						if(nright==null||nleft==null)return new Rectangle[]{area.getBounds()};
						int w = nright.x-nleft.x;
						int h = nright.start-x.y;
						rect.add(new Rectangle(nleft.x,x.y,w,h));
						nright.setStart(x.y);
						nleft.setEnd(x.y);
						ylines.add(joinstart);
						ylines.add(joinend);
					}
				}else{
					if(joinend.increase){
						rect.add(new Rectangle(joinend.x,x.y, x.start-x.end, joinend.end-joinend.start));
						ylines.remove(joinstart);
						ylines.remove(joinend);
					}else{
						YLine nleft = nearestYLine_fromLeft(x, ylines);
						if(nleft==null)return new Rectangle[]{area.getBounds()};
						int w = x.start-nleft.x;
						int h = nleft.end-x.y;
						rect.add(new Rectangle(nleft.x,x.y,w,h));
						nleft.setEnd(x.y);
						ylines.remove(joinstart);
						ylines.add(joinend);
					}
				}
			}
		}
		return rect.toArray(new Rectangle[rect.size()]);
	}


	private YLine nearestYLine_fromLeft(XLine x,YLines ys){
		int leftx;
		if(x.increase)leftx = x.start;
		else leftx = x.end;

		YLine l =null;
		for(YLine y:ys){
			if(y.x >= leftx)break;
			if(y.isIncluded(x.y)){
				l=y;
			}
		}
		return l;
	}

	private YLine nearestYLine_fromRight(XLine x,YLines ys){
		int rightx;
		if(x.increase)rightx = x.end;
		else rightx = x.start;

		YLine l =null;
		for(YLine y:ys){
			if(y.x <= rightx)continue;
			if(y.isIncluded(x.y)){
				l=y;
				break;
			}
		}
		return l;
	}

	private void toLine(ArrayList<XLine> xs,ArrayList<YLine> ys){
		int[] before = new int[2];
		double[] c = new double[6];
		int[] lastmove = new int[2];
		PathIterator p = area.getPathIterator(null);
		Line beforel = null,moveline = null;
		while(!p.isDone()){
			int x,y;
			Line l;
			switch(p.currentSegment(c)){
			case PathIterator.SEG_MOVETO:
				x = (int)c[0];
				y = (int)c[1];
				before[0] =x;
				before[1] = y;
				lastmove[0] = x;
				lastmove[1] = y;
				break;
			case PathIterator.SEG_LINETO:
				x = (int)c[0];
				y = (int)c[1];
				if(before[0]==x){
					YLine yl = new YLine(before[1], y, x);
					l = yl;
					ys.add(yl);
				}else{
					XLine xl = new XLine(before[0],x,y);
					l = xl;
					xs.add(xl);
				}
				before[0] =x;
				before[1] = y;
				if(beforel==null){
					moveline=l;
					beforel = l;
				}
				else {
					if(beforel.isHorizon() == l.isHorizon()){
						if(beforel.isHorizon()){
							beforel.setEnd(x);
							xs.remove(l);
						}else{
							beforel.setEnd(y);
							ys.remove(l);
						}
					}else{
						beforel.setJoinEnd(l);
						l.setJoinStart(beforel);
						beforel = l;
					}
				}
				break;
			case PathIterator.SEG_CLOSE:
				x = lastmove[0];
				y = lastmove[1];
				if(before[0]==x){
					YLine yl = new YLine(before[1], y, x);
					l = yl;
					ys.add(yl);
				}else{
					XLine xl = new XLine(before[0],x,y);
					l = xl;
					xs.add(xl);
				}
				if(beforel!=null){
					beforel.setJoinStart(l);
					l.setJoinStart(beforel);
				}
				if(moveline!=null){
					moveline.setJoinStart(l);
					l.setJoinEnd(moveline);
					moveline=null;
				}
				beforel = null;
				break;
			}
			p.next();
		}
	}

}

abstract class Line{

	int start,end;
	Line joinA,joinB;
	boolean increase;

	public Line(int s,int e) {
		start = s;
		end= e;
		increase = start<end;
	}

	public void setJoinStart(Line y){
		joinA = y;
	}

	public void setJoinEnd(Line y){
		joinB =y;
	}
	public Line getJoinStart() {
		return joinA;
	}
	public Line getJoinEnd() {
		return joinB;
	}

	public void setStart(int v){
		start = v;
		increase = start<end;
	}

	public void setEnd(int v){
		end = v;
		increase = start<end;
	}

	public boolean isIncluded(int v){
		double aa = (v-start)/(double)(end-start);
		return   (0<=aa&& aa<=1);
	}
	abstract public boolean isHorizon();
}

class XLine extends Line implements Comparable<XLine>{
	int y;
	public XLine(int x0,int x1,int y) {
		super(x0,x1);
		this.y = y;
	}

	@Override
	public int compareTo(XLine o) {
		return o.y-y;
	}

	@Override
	public void setJoinStart(Line y) {
		if(y.isHorizon())throw new Error();
		super.setJoinStart(y);
	}

	@Override
	public void setJoinEnd(Line y) {
		if(y.isHorizon())throw new Error();
		super.setJoinEnd(y);
	}

	@Override
	public YLine getJoinStart() {
		return (YLine)super.getJoinStart();
	}
	@Override
	public YLine getJoinEnd() {
		return (YLine)super.getJoinEnd();
	}

	@Override
	public boolean isHorizon() {
		return true;
	}
}

class YLine extends Line{
	int x;
	public YLine(int y0,int y1,int x) {
		super(y0,y1);
		this.x = x;
	}
	@Override
	public boolean isHorizon() {
		return false;
	}
	@Override
	public void setJoinStart(Line y) {
		if(!y.isHorizon())throw new Error();
		super.setJoinStart(y);
	}

	@Override
	public void setJoinEnd(Line y) {
		if(!y.isHorizon()){

			throw new Error(String.format("this :(%d->%d) x:%d,join :(%d->%d) x:%d",start,end,x,y.start,y.end,((YLine)y).x));
		}
		super.setJoinEnd(y);
	}
}

class YLines implements Iterable<YLine>{
	YLine[] array;
	int size=0;

	public YLines(int size) {
		array = new YLine[size];
	}

	public void add(YLine y){
		int x = y.x;
		YLine imp,imp2;
		int i=0;
		for(;i<size;i++){
			if(array[i].x > x){
				break;
			}
		}
		imp = array[i];
		array[i] = y;
		for(i++;i<size+1;i++){
			imp2 = array[i];
			array[i]=imp;
			imp=imp2;
		}
		size++;
	}

	public void remove(YLine y){
		int i;
		for(i=0;i<size;i++){
			if(array[i] == y){
				array[i] = null;
				break;
			}
		}
		for(i++;i<size;i++){
			array[i-1] = array[i];
		}
		size--;
	}

	@Override
	public Iterator<YLine> iterator() {
		return new Iterator<YLine>() {
			int i=0,s = size;
			public void remove() {}
			public YLine next() {return array[i++];}
			public boolean hasNext() {return i<s && array[i]!=null;}
		};
	}
}