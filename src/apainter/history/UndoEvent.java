package apainter.history;

import static apainter.PropertyChangeNames.*;
import apainter.canvas.Canvas;
import apainter.canvas.event.CanvasEvent;

public class UndoEvent extends CanvasEvent{

	public UndoEvent(int id, History source, Canvas canvas) {
		super(id, source, canvas);
	}

	public void undo(){
		History h = getSource();
		boolean b=h.hasNextHistory();
		if(!h.current._undo())return;
		System.out.print("undo 実行="+h.current);
		h.current = h.current.before;
		if(h.current==h.top){
			h.firePropertyChange(HaveUndoHistoryChangeProperty,
					true, false);
		}
		if(!b){
			h.firePropertyChange(HaveRedoHistoryChangeProperty,
					false, true);
		}
		System.out.println(" current="+h.current);
	}


	public History getSource(){
		return (History)super.getSource();
	}

}
