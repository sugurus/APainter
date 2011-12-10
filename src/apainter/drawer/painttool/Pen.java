package apainter.drawer.painttool;

import apainter.Device;
import apainter.GlobalValue;
import apainter.drawer.DrawPoint;
import apainter.drawer.DrawTarget;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;
public class Pen extends BasicDrawer{

	Renderer cpu8bit = new PenCPUDefaultRendering();
	private static final Renderer cpuren = new PenCPUDefaultRendering();


	public Pen(GlobalValue global,int id) {
		super(global,id);
		setDensity(0.5);
//		Device d = getDevice();
//		if(d==Device.GPU){
//			//GPU
//		}
	}

	@Override
	protected boolean isDrawable(DrawTarget dt) {
		String[] s = dt.getDrawTargetName().split(" ");
		boolean ret = true;
		if(!(s[0].equals("cpulayer")||"cpumask".equals(s[0]))){
			ret = false;
		}
		return ret;
	}



	@Override
	protected Renderer getRenderer(Device d) {
		if(d == Device.CPU){
			return  cpuren;
		}
		return null;
	}

	@Override
	protected Device[] getUsableDevices() {
		//TODO GPU対応したいね。
		return CPUOnly();
	}

	@Override
	protected void setOption(RenderingOption option, DrawPoint e) {

	}

	
	//以下当初実装しようと思って、結局破棄された物。
	//実装見込み現在1%程残っているので、0%になるまでは放置
//	public ColorMode getColorMode() {
//		return mode;
//	}

//	public void setColorMode(ColorMode m){
//		if(m==null|| m==ColorMode.AlphaDawn || m==ColorMode.AlphaPlus || m==ColorMode.Del || m==mode)return;
//		ColorMode old = mode;
//		mode = m;
//		firePropertyChange(ColorModeChangeProperty, old, m);
//	}

//	public void setColorMode(String colormode){
//		ColorMode m = ColorMode.getColorMode(colormode);
//		setColorMode(m);
//	}
//	private static Renderer getCPURenderer(ColorMode mode){
//	Renderer r;
//	switch(mode){
//	case Default:
//		r=cpuren[0];
//		break;
//	case Add:
//		r=cpuren[1];
//		break;
//	case Subtractive:
//		r=cpuren[2];
//		break;
//	case Multiplication:
//		r=cpuren[3];
//		break;
//	case Screen:
//		r=cpuren[4];
//		break;
//	case Overlay:
//		r=cpuren[5];
//		break;
//	case Softlight:
//		r=cpuren[6];
//		break;
//	case Hardlight:
//		r=cpuren[7];
//		break;
//	case Dodge:
//		r=cpuren[8];
//		break;
//	case Burn:
//		r=cpuren[9];
//		break;
//	case Darken:
//		r=cpuren[10];
//		break;
//	case Light:
//		r=cpuren[11];
//		break;
//	case Difference:
//		r=cpuren[12];
//		break;
//	case Exclusion:
//		r=cpuren[13];
//		break;
//
//	}
//	return r;
//}
//	private ColorMode mode = ColorMode.Default;
//	private static Renderer[] cpuren = new Renderer[]{
//			new PenCPUDefaultRendering(),
//			new PenCPUAddRendering(),
//			new PenCPUSubtractiveRendering(),
//			new PenCPUMultiplicationRendering(),
//			new PenCPUScreenRendering(),
//			new PenCPUOverlayRendering(),
//			new PenCPUSoftlightRendering(),
//			new PenCPUHardlightRendering(),
//			new PenCPUDodgeRendering(),
//			new PenCPUBurnRendering(),
//			new PenCPUDarkenRendering(),
//			new PenCPULightRendering(),
//			new PenCPUDifferenceRendering(),
//			new PenCPUExclusionRendering()
//	};

}
