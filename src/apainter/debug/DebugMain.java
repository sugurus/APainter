package apainter.debug;

import static apainter.misc.Util.*;
import apainter.GlobalValue;

/**
 * デバッグ用の機能を諸々初期化したり起動したりしていくところ。
 */
public class DebugMain {
	private GlobalValue gv;
	private CUI cui;
	private boolean on=false;

	public DebugMain(GlobalValue gv){
		this.gv = nullCheack(gv);
		cui = new CUI(this.gv);
	}

	public synchronized void debug(boolean t){
		if(!t^on)return;
		on = t;
		if(on){
			on();
		}else{
			off();
		}
	}

	private void on(){
		cui.start();
	}

	private void off(){
		cui.stop();
	}



}

