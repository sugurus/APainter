package apainter;

import java.util.HashMap;

public class GlobalValue extends HashMap<Object, Object>{
	
	public static final GlobalValue instance = new GlobalValue();
	
	
	
	private GlobalValue() {
	}
	

}
