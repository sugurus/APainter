package apainter.drawer.painttool;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalValue;
import apainter.rendering.ColorMode;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

public class Pen extends BasicDrawer{

	Renderer cpu8bit = new PenCPUDefaultRendering();
	private Renderer[] cpuren = new Renderer[]{
{{cpurenderers}}
	};


	private ColorMode mode = ColorMode.Default;
	public Pen(GlobalValue global) {
		super(global);
//		Device d = getDevice();
//		if(d==Device.GPU){
//			//GPU
//		}
	}
	
	private static Renderer getCPURenderer(ColorMode mode){
		Renderer r = null;
		switch(mode){
{{switchblock}}
		}
		return r;
	}

	@Override
	protected Renderer getRenderer(Device d) {
		if(d == Device.CPU){
			return getCPURenderer(mode);
		}
		return null;
	}

	@Override
	protected Device[] getUsableDevices() {
		//TODO GPU対応したいね。
		return CPUOnly();
	}

	@Override
	protected void setOption(RenderingOption option, PenTabletMouseEvent e) {

	}

}