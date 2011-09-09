package apainter.history;

import java.util.Collection;

public final class GroupHistory extends HistoryObject{
	//動作の順に格納。
	//undo時は逆順で実行
	HistoryObject[] histories;
	boolean compressed=false;

	public GroupHistory(HistoryObject... history) {
		histories = history.clone();
	}

	public GroupHistory(Collection<HistoryObject> history){
		histories = history.toArray(new HistoryObject[history.size()]);
	}

	@Override
	public boolean undo() throws Exception {
		for(int i=histories.length-1;i>=0;i--){
			histories[i].undo();
		}
		return true;
	}

	@Override
	public boolean redo() throws Exception {
		for(HistoryObject h:histories){
			h.redo();
		}
		return false;
	}

	@Override
	public boolean isCorrect() {
		for(HistoryObject h:histories){
			if(!h.isCorrect())return false;
		}
		return true;
	}

	@Override
	public long memorySize() {
		long m=4*histories.length;
		for(HistoryObject h:histories){
			m+=h.memorySize();
		}
		return m;
	}

	@Override
	public void compress() {
		if(compressed)return;
		compressed=true;
		for(HistoryObject o:histories){
			o.compress();
		}
	}

	@Override
	public boolean isCompressed() {
		return compressed;
	}

	@Override
	public void clear() {
		for(HistoryObject h:histories){
			h._clear();
		}
	}
	@Override
	public String getHistoryName() {
		StringBuilder sb = new StringBuilder("group{size:"+histories.length+"\n");
		for(HistoryObject h:histories){
			sb.append(h.getHistoryName());
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}
}
