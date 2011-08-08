package apainter.rendering;

import apainter.rendering.impl.cpu.*;


public enum ColorMode {
	Default(0,"default",new DefaultRenderer()) ,Plus(1,"plus",new PlusRenderer()) ,
	Subtractive(2,"subtractive",new SubtractiveRenderer()) ,Multip(3,"multip",new MultipleRenderer()) ,
	Bright(4,"bright",new LightRenderer()) ,Darken(5,"darken",new DarkenRenderer()),Burn(6,"burn",new BurnRenderer()),
	Dodge(7,"dodge",new DodgeRenderer()),SoftLight(8,"softlight",new SoftlightRenderer()),
	HardLight(9,"hardlight",new HardlightRenderer()),Screen(10,"screen",new ScreenRenderer()),
	OverLay(11,"overlay",new OverlayRenderer()),
	Del(100) , AlphaDawn(101) ,
	AlphaPlus(102),
	/**グループ効果がないことを示す*/NONGROUPEFFECT(31,"notlayergroup"),
	/**色を置き換える*/REPLACEMENT(-1),
	/**nullの代用*/NULL(-1000);

	static final public ColorMode[] effects=ColorMode.values();

	static final public int layereffectsum = 15;

	static public String[] getLayerColorModeNames(){
		String[] ret = new String[layereffectsum];
		for(int i=0;i<layereffectsum;i++){
			ret[i] = effects[i].toString();
		}
		return ret;
	}

	static public String[] getGroupColorModeNames(){
		String[] ret = new String[layereffectsum+1];
		for(int i=0;i<layereffectsum;i++){
			ret[i+1] = effects[i].toString();
		}
		ret[0] = NONGROUPEFFECT.toString();
		return ret;
	}

	public final int num;
	public final String config;
	private final Renderer cpuRender;
	private ColorMode(int n) {this(n,"unknown");}
	private ColorMode(int n,String s){this(n,s,null);}

	private ColorMode(int n,String s,Renderer cpuRender){
		num = n;config=s;
		this.cpuRender = cpuRender;
	}

	public Renderer getCPURenderer(){
		return cpuRender;
	}

	static public ColorMode getEffects(int num){
		for(ColorMode e : effects)if(num == e.num)return e;
		return NULL;
	}

}
