package apainter;

public enum GlobalKey{
	FrontColor(false),BackColor(false),
	CanvasList(false),CurrentCanvas,
	CanvasActionList(false),CanvasHeadAction,CanvasTailAction,
	PenFactoryCenter(false),

	CommandCenter(false),

	NEWLayerDefaultName

	;


	final boolean change;
	private GlobalKey(boolean b) {
		change = b;
	}
	private GlobalKey() {
		change = true;
	}

}
