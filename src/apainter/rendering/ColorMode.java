package apainter.rendering;


public enum ColorMode {
	Default(0,"default") ,Plus(1,"plus") ,
	Subtractive(2,"subtractive") ,Multip(3,"multip") ,
	Bright(4,"bright") ,Darken(5,"darken"),Burn(6,"burn"),
	Dodge(7,"dodge"),SoftLight(8,"softlight"),
	HardLight(9,"hardlight"),Screen(10,"screen"),
	OverLay(11,"overlay"),
	BoolAnd(12,"and") ,BoolOr(13,"or") ,
	BoolExOR(14,"exor") ,
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
	private ColorMode(int n) {num = n;config="unknown";}
	private ColorMode(int n,String s){num = n;config=s;}
	static public ColorMode getEffects(int num){
		for(ColorMode e : effects)if(num == e.num)return e;
		return NULL;
	}

}