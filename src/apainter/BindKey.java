package apainter;

import apainter.misc.Angle;

public enum BindKey {
	/**
	 * 使いません。
	 */
	Null(false),
	/**
	 * 描画の際の前景色の束縛鍵です。<br><br>
	 * <b>＜条件＞</b><br>
	 * Object =>float[]<br>
	 * 順にa,r,g,bが格納されており0～1の範囲の値。その範囲を超える場合は自動でその範囲に収められます。<br>
	 * 配列長が4未満の場合は設定はされない。<br>
	 * また、5以上の配列長の時は、余分な部分は無視されます。
	 */
	FrontColorBIND(false),
	/**
	 * 描画の際の背景色の束縛鍵です。<br><br>
	 * <b>＜条件＞</b><br>
	 * Object =>float[]<br>
	 * 順にa,r,g,bが格納されており0～1の範囲の値。その範囲を超える場合は自動でその範囲に収められます。<br>
	 * 配列長が4未満の場合は設定はされない。<br>
	 * また、5以上の配列長の時は、余分な部分は無視されます。
	 */
	BackColorBIND(false),
	PenModeBIND(false),
	/**
	 * キャンパスのguiの回転状態の束縛鍵です。<br>
	 * CanvasHandler#bindで使えます<br><br>
	 * <b>＜条件＞</b><br>
	 * Object=>Angle<br>
	 * @see apainter.misc.Angle
	 */
	AngleBIND(false),
	/**
	 * キャンパスのguiにおいて、キャンパスの中心がどこかの束縛鍵です。<br>
	 * この座標系は<b>コンポーネント</b>の中心を原点としています。<br>
	 * CanvasHandler#bindで使えます<br><br>
	 * <b>＜条件＞</b><br>
	 * Object => Point2D<br>
	 */
	CanvasPositionBIND(false),
	/**
	 * キャンパスのguiの反転状態の束縛鍵です<br>
	 * CanvasHandler#bindで使えます<br><br>
	 * <b>＜条件＞</b><br>
	 * Object => boolean(Boolean)<br>
	 * この値がtrueの時、左右に反転表示されます。
	 */
	ReverseBIND(false),
	/**
	 * キャンパスのguiの拡大状態の束縛鍵です。<br>
	 * CanvasHandler#bindで使えます<br><br>
	 * <b>＜条件＞</b><br>
	 * Object=>double(Double)<br>
	 * 値は0.05～16の間でとることができます<br>
	 */
	ZoomBIND(false)
	;


	final boolean change;
	private BindKey(boolean b) {
		change = b;
	}
	private BindKey() {
		change = true;
	}

}
