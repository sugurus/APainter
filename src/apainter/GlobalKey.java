package apainter;

public enum GlobalKey{
	APainter(false),
	FrontColor(false),BackColor(false),
	CanvasList(false),CurrentCanvas,
	CanvasActionList(false),CanvasHeadAction,CanvasTailAction,
	PenFactoryCenter(false),OnDevice(false),

	CommandCenter(false),
	CommandPrintStream,
	CommandErrorPrintStream,
	Property(false),


	;


	final boolean change;
	private GlobalKey(boolean b) {
		change = b;
	}
	private GlobalKey() {
		change = true;
	}

}
