package apainter;

public enum GlobalBindKey {
	FrontColorBIND(false),FrontColor16bitBIND(false),
	BackColorBIND(false),BackColor16bitBIND(false),
	PenModeBIND(false),
	;


	final boolean change;
	private GlobalBindKey(boolean b) {
		change = b;
	}
	private GlobalBindKey() {
		change = true;
	}

}
