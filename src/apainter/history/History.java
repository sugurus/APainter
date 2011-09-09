package apainter.history;

import static apainter.PropertyChangeNames.*;

import java.util.ArrayList;

import apainter.canvas.Canvas;

public class History {



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


	public synchronized boolean undo(){
		if(hasBeforeHistory()){
			boolean b=hasNextHistory();
			if(!current._undo())return false;
			current = current.before;
			if(current==top){
				firePropertyChange(HaveUndoHistoryChangeProperty,
						true, false);
			}
			if(!b){
				firePropertyChange(HaveRedoHistoryChangeProperty,
						false, true);
			}
			return true;
		}
		return false;
	}

	public synchronized boolean redo(){
		if(hasNextHistory()){
			boolean b=hasBeforeHistory();
			if(!current._redo())return false;
			if(!hasNextHistory()){
				firePropertyChange(HaveRedoHistoryChangeProperty,
						true, false);
			}
			if(!b){
				firePropertyChange(HaveUndoHistoryChangeProperty,
						false, true);
			}
			current = current.next;
			return true;
		}
		return false;
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

	private void firePropertyChange(String name,Object oldValue,Object newValue){
		canvas.firePropertyChange(name, oldValue, newValue, canvas);
	}

	private Canvas canvas;
	private final TopHistory top = new TopHistory();
	private HistoryObject current = top;
	private ArrayList<HistoryObject> grouphistory = new ArrayList<HistoryObject>();
	private boolean nowGroup=false;
	private long maxMemory=20*1000*1000;//byte
}


class TopHistory extends HistoryObject{
	public boolean undo(){return false;}
	public boolean redo(){return false;}
	public boolean isCorrect() {return false;}
	public long memorySize() {return 0;}
	public void clear() {}
	public String getHistoryName() {return "top";}
}