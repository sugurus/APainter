package apainter.drawer.painttool;

import nodamushi.pentablet.PenTabletMouseEvent;
import apainter.Device;
import apainter.GlobalValue;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

public class Pen extends BasicDrawer{

	Renderer cpu8bit = new PenCPUDefaultRendering();

	public Pen(GlobalValue global) {
		super(global);
	}

	@Override
	protected Renderer getRenderer() {
		return cpu8bit;
	}

	@Override
	protected Device[] getUsableDevices() {
		//TODO GPU対応したいね。
		return CPUOnly();
	}

	@Override
	protected void setOption(RenderingOption option, PenTabletMouseEvent e) {}

}