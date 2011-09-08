package apainter.history;

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

	public void addHistory(HistoryObject o){
		if(current.hasNext()){
			current.removeNext();
		}
		current.next = o;
		o.before= current;
		current = o;
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


	public boolean undo(){
		if(hasBeforeHistory()){
			if(!current._undo())return false;
			current = current.before;
			return true;
		}
		return false;
	}

	public boolean redo(){
		if(hasNextHistory()){
			if(!current._redo())return false;
			current = current.next;
			return true;
		}
		return false;
	}

	private final TopHistory top = new TopHistory();
	private HistoryObject current = top;

}


class TopHistory extends HistoryObject{
	public boolean undo(){return false;}
	public boolean redo(){return false;}
	public boolean isCorrect() {return false;}
	public long memorySize() {return 0;}
	public void clear() {}
	public String getHistoryName() {return "";}
}