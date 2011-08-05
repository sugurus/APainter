package apainter.command;

import apainter.APainter;
import apainter.GlobalKey;
import apainter.GlobalValue;

/**
 * APainterに対して操作を実行するオブジェクトです。<br>
 * 同じインスタンスはある状態のAPainterに操作を行った場合、同じ結果にならないといけません。
 * @author nodamushi
 *
 */
public abstract class Command{

	private APainter targetAPainter;
	final void setAPainter(APainter a){
		targetAPainter = a;
	}

	/**
	 * 操作ターゲットとなるAPainterを返します。
	 * @return
	 */
	final public APainter getTargetAPainter(){
		return targetAPainter;
	}

	/**
	 * コマンドを実行します。<br>
	 * 操作ターゲットインスタンスが異なる場合は何も行いません。
	 * @param global
	 */
	public Object exe(GlobalValue global){
		if(targetAPainter==null||targetAPainter!=global.get(GlobalKey.APainter))return null;
		return execution(global);
	}
	/**
	 * 実行する処理内容。exeから呼ばれます。
	 * @param global
	 */
	protected abstract Object execution(GlobalValue global);
}
