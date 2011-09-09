package apainter.history;

import static apainter.PropertyChangeNames.*;

import java.util.ArrayList;

import apainter.canvas.Canvas;

public class History {

	private volatile int id=0;

	public void clear(){
		top.removeNext();
	}


	public long getMemorySize(){
		long m = 0;
		HistoryObject h = top;
		while(h.hasNext()){
			h=h.next;
			m+=h.memorySize();
		}
		return m;
	}

	public synchronized void addHistory(HistoryObject o){
		if(o==null)return;
		if(o.holded){
			throw new Error(o.toString());
		}
		o.id = id++;
		o.holded = true;
		if(nowGroup){
			addGrouop(o);
			return;
		}
		if(current.hasNext()){
			current.removeNext();
		}
		current.next = o;
		o.before= current;
		current = o;
		long m = getMemorySize();
		if(m>maxMemory){
			HisotryReduceEvent e = new  HisotryReduceEvent(0, this, canvas);
			canvas.dispatchEvent(e);
		}
		firePropertyChange(NewHiostoryAddProperty,
				null, o.toString());
	}


	public void tryCompress(){
		HistoryObject o = top;
		while(o.next!=null){
			o=o.next;
			if(!o.isCompressed()){
				o.compress();
			}
		}
	}

	public synchronized void reduceHistory(){
		boolean istop = current==top&&top.next!=null;
		long m = getMemorySize();
		long ex = m-maxMemory+maxMemory/10;
		if(ex<=0)return;
		while(ex>0 && top.next!=current){
			HistoryObject old = top.next;
			ex -=old.memorySize();
			top.next = old.next;
			if(top.next!=null)
				top.next.before = top;
			old.before=null;
			old.next = null;
			old._clear();
		}
		boolean notredo=top.next==null;
		if(istop&&notredo){
			firePropertyChange(HaveRedoHistoryChangeProperty,
					true, false);
		}
	}

	private void addGrouop(HistoryObject o){
		grouphistory.add(o);
	}

	public int length(){
		int i=0;
		HistoryObject h = top;
		while(h.hasNext()){
			i++;
			h=h.next;
		}
		return i;
	}

	public boolean hasNextHistory(){
		return current.hasNext();
	}

	public boolean hasBeforeHistory(){
		return current.hasBefore();
	}


	public synchronized void undo(){
		if(hasBeforeHistory()){
			UndoEvent e = new UndoEvent(0, this, canvas);
			canvas.dispatchEvent(e);
		}
	}

	public synchronized void redo(){
		if(hasNextHistory()){
			RedoEvent e = new RedoEvent(0, this, canvas);
			canvas.dispatchEvent(e);
		}
	}

	public void markGroup(){
		nowGroup=true;
	}
	public void finishGroup(){
		nowGroup=false;
		if(grouphistory.size()==0)return;
		GroupHistory g = new GroupHistory(grouphistory);
		grouphistory.clear();
		addHistory(g);
	}

	public void setMaxMemory(long maxbyte){
		if(maxbyte<=0)return;
		maxMemory = maxbyte;
	}

	public long getMaxMemory(){
		return maxMemory;
	}

	public History(Canvas c) {
		canvas = c;
	}

	void firePropertyChange(String name,Object oldValue,Object newValue){
		canvas.firePropertyChange(name, oldValue, newValue, canvas);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		HistoryObject o = top;
		while(o.next!=null){
			o = o.next;
			sb.append(o.toString()).append("\n");
		}
		return sb.toString();
	}

	Canvas canvas;
	final TopHistory top = new TopHistory();
	HistoryObject current = top;
	ArrayList<HistoryObject> grouphistory = new ArrayList<HistoryObject>();
	boolean nowGroup=false;
	long maxMemory=20*1000*1000;//byte
}


class TopHistory extends HistoryObject{
	public boolean undo(){return false;}
	public boolean redo(){return false;}
	public boolean isCorrect() {return false;}
	public long memorySize() {return 0;}
	public void clear() {}
	public String getHistoryName() {return "top";}
}