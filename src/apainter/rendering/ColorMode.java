package apainter.rendering;

import javax.swing.Timer;

import apainter.rendering.impl.cpu.*;


public enum ColorMode {
	Default(0,"default",new DefaultRenderer()) ,Add(1,"add",new PlusRenderer()) ,
	Subtractive(2,"subtractive",new SubtractiveRenderer()) ,Multiplication(3,"multiplication",new MultipleRenderer()) ,
	Bright(4,"bright",new LightRenderer()) ,Darken(5,"darken",new DarkenRenderer()),Burn(6,"burn",new BurnRenderer()),
	Dodge(7,"dodge",new DodgeRenderer()),Softlight(8,"softlight",new SoftlightRenderer()),
	Hardlight(9,"hardlight",new HardlightRenderer()),Screen(10,"screen",new ScreenRenderer()),
	Overlay(11,"overlay",new OverlayRenderer()),
	Del(100) , AlphaDawn(101) ,
	AlphaPlus(102),
	/**グループ効果がないことを示す*/NONGROUPEFFECT(31,"nongroupeffect"),
	/**色を置き換える*/REPLACEMENT(-1),
	/**nullの代用*/NULL(-1000);

	/**
	 * 文字列からColorModeを取得します。
	 * @param s
	 * @return
	 */
	//java7から使えるようになったswitch文を逆コンパイルしてコピーした物。
	//Indigoが正式にjava7に対応したら書き換えます。
	//コンパイル前の元ファイルはelseフォルダーのColorMode.java。
	//ところで、このbyte0ってなんのためにあるんだろ。
	public static ColorMode getColorMode(String s)
    {
		if(s==null)return null;
        s = s.toLowerCase();
        ColorMode colormode = null;
        String s1 = s;
        byte byte0 = -1;
        switch(s1.hashCode())
        {
        case 1544803905:
            if(s1.equals("default"))
                byte0 = 0;
            break;

        case 96417:
            if(s1.equals("add"))
                byte0 = 1;
            break;

        case -1774340796:
            if(s1.equals("subtractive"))
                byte0 = 2;
            break;

        case 668845958:
            if(s1.equals("multiplication"))
                byte0 = 3;
            break;

        case -1380798726:
            if(s1.equals("bright"))
                byte0 = 4;
            break;

        case -1338968417:
            if(s1.equals("darken"))
                byte0 = 5;
            break;

        case 3035599:
            if(s1.equals("burn"))
                byte0 = 6;
            break;

        case 95758295:
            if(s1.equals("dodge"))
                byte0 = 7;
            break;

        case -2060367060:
            if(s1.equals("softlight"))
                byte0 = 8;
            break;

        case -680702197:
            if(s1.equals("hardlight"))
                byte0 = 9;
            break;

        case -907689876:
            if(s1.equals("screen"))
                byte0 = 10;
            break;

        case -1091287984:
            if(s1.equals("overlay"))
                byte0 = 11;
            break;
        }
        switch(byte0)
        {
        case 0: // '\0'
            colormode = Default;
            break;

        case 1: // '\001'
            colormode = Add;
            break;

        case 2: // '\002'
            colormode = Subtractive;
            break;

        case 3: // '\003'
            colormode = Multiplication;
            break;

        case 4: // '\004'
            colormode = Bright;
            break;

        case 5: // '\005'
            colormode = Darken;
            break;

        case 6: // '\006'
            colormode = Burn;
            break;

        case 7: // '\007'
            colormode = Dodge;
            break;

        case 8: // '\b'
            colormode = Softlight;
            break;

        case 9: // '\t'
            colormode = Hardlight;
            break;

        case 10: // '\n'
            colormode = Screen;
            break;

        case 11: // '\013'
            colormode = Overlay;
            break;
        }
        return colormode;
    }

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
