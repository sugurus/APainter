package demo;

import java.awt.Adjustable;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

public class GUIUtillity {

	public static AdjustmentListener[] removeAllAdjustmentListeners(JScrollBar s){
		AdjustmentListener[] ads;
		ads = s.getAdjustmentListeners();
		for(AdjustmentListener ad:ads){
			s.removeAdjustmentListener(ad);
		}
		return ads;
	}

	public static void addAllAdjustmentListenre(Adjustable adjustable,AdjustmentListener[] listeners){
		for(AdjustmentListener l:listeners){
			adjustable.addAdjustmentListener(l);
		}
	}

	public static ChangeListener[] removeAllChangeListeners(JSlider s){
		ChangeListener[] ls = s.getChangeListeners();
		for(ChangeListener l:ls){
			s.removeChangeListener(l);
		}
		return ls;
	}


	public static void addAllChangeListeners(JSlider s,ChangeListener[] listeners){
		for(ChangeListener l:listeners){
			s.addChangeListener(l);
		}
	}
}
