package apainter;

public interface PropertyChangeNames {

	public static final String

	FrontColorChangeProperty ="frontcolor",
	BackColorChangeProperty="backcolor",
	SelectedLayerPropertyChange = "selectedlayer",
	SelectedMaskPropertyChange = "selectedmask",
	PenModePropertyChange="penmode",
	AuthorNameChangeProperty ="authorname",
	CanvasNameChangeProperty="canvasname",

	/**
	 * マウスカーソルがキャンパスコンポーネント上にあるとき、マウスカーソルを操作する装置が変更されたときに通知されます。<br>
	 * このイベントはOldvalueにnullが常に渡されます。<br>
	 * 渡されるnewvalueは"mouse","head","tail"の文字列です。
	 */
	CursorTypeChangeProperty="cursortype",

	//drawer系統
	PenShapeChangeProperty="penshape",
	PlotEndPointChangeProperty="plotendpoint",
	PlotChangeProperty="plot",
	PressureAdjusterChangeProperty="penadjuster",
	DrawerDensityChangeProperty="drawerdensity",
	DrawerMinDensityChangeProperty="drawermindensity",
	DrawerMinPenSizeChangeProperty="drawerminpensize",


	//履歴系統
	/**
	 * oldは常にnull<br>
	 * newvalueは履歴の名前が入っている。(String型)
	 */
	NewHiostoryAddProperty="newhistoryadd",
	/**
	 * 値はboolean(trueだとundoできる。falseだとできない)
	 */
	HaveUndoHistoryChangeProperty="haveundohistory",
	/**
	 * 値はboolean(trueだとredoできる。falseだとできない)
	 */
	HaveRedoHistoryChangeProperty="haveredohisotry"
	;

}
