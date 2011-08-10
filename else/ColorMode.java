public enum ColorMode {
	Default,Add,
	Subtractive ,Multiplication ,
	Light ,Darken,Burn,
	Dodge,Softlight,
	Hardlight,Screen,
	Overlay,
	Del , AlphaDawn ,
	AlphaPlus,NONGROUPEFFECT,REPLACEMENT;
	
	public static ColorMode getColorMode(String str){
		if(str==null)return null;
		str  = str.toLowerCase();
		ColorMode m=null;
		switch(str){
		case "default":
			m=Default;
			break;
		case "add":
			m=Add;
			break;
		case "subtractive":
			m=Subtractive;
			break;
		case "multiplication":
			m = Multiplication;
			break;
		case "light":
			m=Light;
			break;
		case "darken":
			m=Darken;
			break;
		case "burn":
			m=Burn;
			break;
		case "dodge":
			m=Dodge;
			break;
		case "softlight":
			m=Softlight;
			break;
		case "hardlight":
			m=Hardlight;
			break;
		case "screen":
			m=Screen;
			break;
		case "overlay":
			m=Overlay;
			break;
		case "nongroupeffect":
			m=NONGROUPEFFECT;
			break;
		}
		return m;
	}
}