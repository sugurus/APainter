package apainter.history;

import static apainter.PropertyChangeNames.*;
import apainter.canvas.Canvas;
import apainter.canvas.event.CanvasEvent;

public class RedoEvent extends CanvasEvent{

	public RedoEvent(int id, History source, Canvas canvas) {
		super(id, source, canvas);
	}

	public void redo(){
		History h = getSource();
		boolean b=h.hasBeforeHistory();
		if(!h.current.next._redo())return;
		h.current = h.current.next;
		if(!h.hasNextHistory()){
			h.firePropertyChange(HaveRedoHistoryChangeProperty,
					true, false);
		}
		if(!b){
			h.firePropertyChange(HaveUndoHistoryChangeProperty,
					false, true);
		}
	}


	public History getSource(){
		return (History)super.getSource();
	}

}
