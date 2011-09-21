package apainter.rendering;

import static apainter.misc.Utility_PixelFunction.*;
import static java.lang.Math.*;
import static apainter.data.PixelData15BitBuffer.*;

/**
 * 色の合算をする関数群です。
 * 編集するときはColorOpMakeディレクトリのColorOperations.javaを編集し、ColorOpMaker.classを実行してください。
 * @author nodamushi
 * @see http://ofo.jp/osakana/cgtips/blendmode.phtml
 * @see http://www.bea.hi-ho.ne.jp/gaku-iwa/color/conjn.html
 */
public final class ColorOperations {
	public static final void default15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int underalpha =  (ua*(32767-oa)/32767);
		int r = (oa*or+underalpha*ur)/a;
		int g = (oa*og+underalpha*ug)/a;
		int b = (oa*ob+underalpha*ub)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}

	public static final int add15bitOp(int under,int over){
		return under+over>32767?32767:under+over;
	}
	public static final void add15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*add15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*add15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*add15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}

	public static final int subtractive15bitOp(int under,int over){
		return under-over>=0?under-over:0;
	}
	public static final void subtractive15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*subtractive15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*subtractive15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*subtractive15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}


	public static final int screen15bitOp(int under,int over){
		return ((under+over)*32767-under*over)/32767;
	}
	public static final void screen15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*screen15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*screen15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*screen15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}

	public static final int overlay15bitOp(int under,int over){
		return under<16384?
				under*over*2/32767:
				(under+over-under*over/32767)*2-32767;
	}

	public static final void overlay15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*overlay15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*overlay15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*overlay15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}

	public static final int hardlight15bitOp(int under,int over){
		return over < 16384?
				under*over*2/32767:
				(under+over-under*over/32767)*2-32767;
	}

	public static final void hardlight15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*hardlight15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*hardlight15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*hardlight15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}

	public static final int darken15bitOp(int under,int over){
		return under>over?over:under;
	}
	public static final void darken15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*darken15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*darken15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*darken15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}

	public static final int light15bitOp(int under,int over){
		return under>over?under:over;
	}
	public static final void light15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*light15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*light15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*light15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}

	public static final int difference15bitOp(int under,int over){
		return under>over?under-over:over-under;
	}
	public static final void difference15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*difference15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*difference15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*difference15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}


	public static final int exclusion15bitOp(int under,int over){
		return under+over-2*under*over/32767;
	}
	public static final void exclusion15bitOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob,int[] integer,int[] decimal,int position){
		int a = calca15bit(ua, oa);
		int uoalpha = ua*oa/32767;
		int u_oalpha =  (ua*(32767-oa)/32767);
		int _uoalpha = (oa*(32767-ua)/32767);
		int r = (uoalpha*exclusion15bitOp(ur,or)+u_oalpha*ur+_uoalpha*or)/a;
		int g = (uoalpha*exclusion15bitOp(ug,og)+u_oalpha*ug+_uoalpha*og)/a;
		int b = (uoalpha*exclusion15bitOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)/a;
		integer[position] = argb(integer(a), integer(r), integer(g), integer(b));
		decimal[position] = argb(decimal(a),decimal(r),decimal(g),decimal(b));
	}
	//---singleOp----------------------------------------
	public static final int defaultOp(int under,int over){
		return over;
	}

	public static final int addOp(int under,int over){
		return under+over>255?255:under+over;
	}

	public static final int subtractiveOp(int under,int over){
		return under-over>=0?under-over:0;
	}

	public static final int multiplicationOp(int under,int over){
		return under*over*div255shift24>>>24;
	}


	public static final int screenOp(int under,int over){
		return ((under+over)*255-under*over)*div255shift24>>>24;
	}


	public static final int overlayOp(int under,int over){
		return under<128?
				under*over*2*div255shift24>>>24:
				((under*255+over*255-under*over  <<1) -255*255)*div255shift24>>>24;
	}

	public static final byte[] softlightTable = new byte[256*256];//ちょっとメモリをけちるためにbyte
	static{
		double k =32d/255;
		for(int under=0;under<256;under++){
			double u = under/255d;
			for(int over=0;over<128;over++){
				double o = over/255d;
				double dst = u+(u-u*u)*(2*o-1);
				dst *=255;
				softlightTable[under*256+over]=(byte) (int)dst;
			}
			for(int over=128;over<256;over++){
				double o = over/255d;
				double dst;
				if(u<=k){
					dst = u+(u-u*u)*(2*o-1)*(3-8*u);
				}else{
					dst = u+(sqrt(u)-u)*(2*o-1);
				}
				dst *=255;
				softlightTable[under*256+over]=(byte) (int)dst;
			}
		}
	}
	public static final int softlightOp(int under,int over){
		return softlightTable[under*256+over]&0xff;
	}


	public static final int hardlightOp(int under,int over){
		return over<128?
				under*over*2*div255shift24>>>24:
				2*(under+over-(under*over*div255shift24>>>24))-255;
	}

	private static final byte[] dodgeTable = new byte[256*256];
	static{
		for(int under=0;under<256;under++){
			for(int over=0;over<255;over++){
				int t = under*255/(255-over);
				if(t>255)t=255;
				dodgeTable[under*256+over]=(byte) t;
			}
			dodgeTable[under*256+255] = (byte) 255;
		}
	}
	public static final int dodgeOp(int under,int over){
		return dodgeTable[under*256+over]&0xff;
	}

	private static final byte[] burnTable = new byte[256*256];
	static{
		for(int under=0;under<256;under++){
			burnTable[under*256] = 0;
			for(int over=1;over<256;over++){
				int t = 255-((255-under)*255/over);
				if(t<0)t=0;
				burnTable[under*256+over]=(byte) t;
			}
		}
	}
	public static final int burnOp(int under,int over){
		return burnTable[under*256+over]&0xff;
	}

	public static final int darkenOp(int under,int over){
		return under<over?under:over;
	}

	public static final int lightOp(int under,int over){
		return under<over?over:under;
	}

	public static final int differenceOp(int under,int over){
		return under<over? over-under:under-over;
	}

	public static final int exclusionOp(int under,int over){
		return under+over-(2*under*over*div255shift24>>>24);
	}


	//----------------------------------------------------------------

	//argb------------------------------------------------------------


	public static final int defaultOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int u_oalpha = ua*(255-oa);
		return a<<24 |
		(oa*255*or+u_oalpha*ur)*div>>>24 << 16 |
		(oa*255*og+u_oalpha*ug)*div>>>24 <<8 |
		(oa*255*ob+u_oalpha*ub)*div>>>24;
	}

	public static final int addOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*addOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*addOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*addOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int subtractiveOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*subtractiveOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*subtractiveOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*subtractiveOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int multiplicationOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*multiplicationOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*multiplicationOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*multiplicationOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int screenOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*screenOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*screenOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*screenOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int overlayOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*overlayOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*overlayOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*overlayOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int softlightOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*softlightOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*softlightOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*softlightOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int hardlightOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*hardlightOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*hardlightOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*hardlightOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int dodgeOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*dodgeOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*dodgeOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*dodgeOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int burnOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*burnOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*burnOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*burnOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int darkenOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*darkenOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*darkenOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*darkenOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int lightOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*lightOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*lightOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*lightOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int differenceOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*differenceOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*differenceOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*differenceOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int exclusionOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*exclusionOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*exclusionOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*exclusionOp(ub,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}




	private ColorOperations(){}
}
