package apainter;

public interface PropertyChangeNames {

	public static final String

	FrontColorChangeProperty ="frontcolorchange",
	BackColorChangeProperty="backcolorchange",
	SelectedLayerPropertyChange = "selectedlayerchange",
	SelectedMaskPropertyChange = "selectedmaskchange",
	PenModePropertyChange="penmodechange",
	AuthorNameChangeProperty ="authornamechange",
	CanvasNameChangeProperty="canvasnamechange",

	/**
	 * マウスカーソルがキャンパスコンポーネント上にあるとき、マウスカーソルを操作する装置が変更されたときに通知されます。<br>
	 * このイベントはOldvalueにnullが常に渡されます。<br>
	 * 渡されるnewvalueは"mouse","head","tail"の文字列です。
	 */
	CursorTypeChangeProperty="cursortypechange",

	//drawer系統
	PenShapeChangeProperty="penshapechange",
	PlotEndPointChangeProperty="plotendpointchange",
	PlotChangeProperty="plotchange",
	PressureAdjusterChangeProperty="penadjusterchange",
	DrawerDensityChangeProperty="drawerdensitychange",
	DrawerMinDensityChangeProperty="drawermindensitychange",
	DrawerMinPenSizeChangeProperty="drawerminpensizechange"

	;

}
