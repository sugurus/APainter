package apainter.debug;

import apainter.GlobalValue;

/**
 * デバッグ用の機能を諸々初期化したり起動したりしていくところ。
 */
public class DebugMain {
	private static GlobalValue gv;
	public static void init(){
		gv=GlobalValue.getInstance();
		initCUI();

	}


	private static void initCUI(){
		CUI cui = CUI.cui;
		cui.setGlobalValue(gv);

		cui.setCommand(new Exit());
		cui.setCommand(new Rotation());
		cui.setCommand(new Zoom());


		cui.start();

	}

}

