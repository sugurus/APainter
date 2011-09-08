package apainter.history;

public abstract class HistoryObject {

	HistoryObject next,before;


	final boolean _undo(){
		if(isCorrect()){
			boolean b=false;
			try{
				b=undo();
			}catch(Exception e){
				e.printStackTrace();
				b=false;
			}
			return b;
		}

		return false;
	}

	final boolean _redo(){
		if(isCorrect()){
			boolean b=false;
			try{
				b=redo();
			}catch(Exception e){
				e.printStackTrace();
				b=false;
			}
			return b;
		}
		return false;
	}

	/**
	 * nextがnullの時に呼ぶ。
	 */
	final void _clear(){
		if(next!=null){
			System.err.println("next!=null");
			return;
		}
		if(before!=null){
			before.next=null;
		}
		before = null;
		clear();
	}

	final void removeNext(){
		if(next!=null){
			next._removeNext();
		}
	}
	private void _removeNext(){
		if(next!=null){
			next._removeNext();
		}
		_clear();
	}

	/**
	 * 使用するメモリーが多く、圧縮できる場合はこれをオーバーライドし、実装してください。<br>
	 * 実装した場合はisCompressed関数もオーバーライドしてください。<br>
	 * この関数は必ず実装する必要はありません。
	 * 
	 */
	public void compress(){}
	/**
	 * 圧縮できる場合、データーが圧縮されているかどうかを返します。
	 * @return
	 */
	public boolean isCompressed(){
		return false;
	}
	
	/**
	 * 履歴のアンドゥを実装します。<br>
	 * Historyクラスが管理できなくなるので、この関数を呼び出さないでください。
	 * @return アンドゥに成功したかどうか
	 * @throws Exception
	 */
	protected abstract boolean undo() throws Exception;
	/**
	 * 履歴のリドゥを実装します<br>
	 * Historyクラスが管理できなくなるので、この関数を呼び出さないでください。
	 * @return リドゥに成功したかどうか
	 * @throws Exception
	 */
	protected abstract boolean redo() throws Exception;
	/**
	 * この履歴が正しく使える状態にあるかどうかを返します。
	 * @return
	 */
	protected abstract boolean isCorrect();
	/**
	 * この履歴が使用するおおよそのメモリーを返します。
	 * @return
	 */
	protected abstract long memorySize();
	/**
	 * この履歴が使用している領域を開放します。
	 */
	protected abstract void clear();
	/**
	 * 履歴の名前です。履歴固有の名前にしてください。
	 * @return
	 */
	public abstract String getHistoryName();
	boolean hasNext(){return next!=null;}
	boolean hasBefore(){return before!=null;}
}
