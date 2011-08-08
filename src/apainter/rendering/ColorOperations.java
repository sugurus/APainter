package apainter.rendering;

import static apainter.misc.Utility_PixelFunction.*;
import static java.lang.Math.*;

/**
 * 色の合算をする関数群です。
 * 編集するときはColorOpMakeディレクトリのColorOperations.javaを編集し、ColorOpMaker.classを実行してください。
 * @author nodamushi
 * @see http://ofo.jp/osakana/cgtips/blendmode.phtml
 */
public final class ColorOperations {

	//---singleOp----------------------------------------
	public static final int defaultOp(int under,int over){
		return over;
	}

	public static final int plusOp(int under,int over){
		return under+over>255?255:under+over;
	}

	public static final int subtractiveOp(int under,int over){
		return under-over>=0?under-over:0;
	}

	public static final int multipleOp(int under,int over){
		return under*over*div255shift24>>>24;
	}


	public static final int screenOp(int under,int over){
		return ((under+over)*255-under*over)*div255shift24>>>24;
	}

	//TODO どっちの方が速いのか
//	public static final byte[] overlayTable = new byte[256*256];
//	static{
//		for(int under=0;under<256;under++){
//			for(int over=0;over<128;over++){
//				overlayTable[under*256+over] = (byte) (under*over*2/255);
//			}
//			for(int over=128;over<256;over++){
//				overlayTable[under*256+over] = (byte) ((2*(under+over-under*over)  -255*255)/255);
//			}
//		}
//	}
//	public static final int overlayOp(int under,int over){
//		return overlayTable[under*256+over]&0xff;
//	}

	public static final int overlayOp(int under,int over){
		return under<128?
				under*over*2*div255shift24>>>24:
				((under+over-under*over  <<1) -255*255)*div255shift24>>>24;
	}

	public static final byte[] softlightTable = new byte[256*256];//ちょっとメモリをけちるためにbyte
	static{
		for(int under=0;under<256;under++){
			for(int over=128;over<256;over++){
				softlightTable[under*256+255-over]=
				softlightTable[under*256+over]=(byte) (int)(pow(under/255d, over/128d)*255);
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
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*defaultOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*defaultOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*defaultOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int plusOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*plusOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*plusOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*plusOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*subtractiveOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}

	public static final int multipleOp(int ua,int ur,int ug,int ub,
			int oa,int or,int og,int ob){
		int a = calca(ua, oa);
		int div = ((1<<24)+a*255-1)/(a*255);
		int uoalpha = ua*oa;
		int u_oalpha = ua*(255-oa);
		int _uoalpha = (255-ua)*oa;
		return a<<24 |
		(uoalpha*multipleOp(ur,or)+u_oalpha*ur+_uoalpha*or)*div>>>24 << 16 |
		(uoalpha*multipleOp(ug,og)+u_oalpha*ug+_uoalpha*og)*div>>>24 <<8 |
		(uoalpha*multipleOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*screenOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*overlayOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*softlightOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*hardlightOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*dodgeOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*burnOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*darkenOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*lightOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*differenceOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
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
		(uoalpha*exclusionOp(ur,ob)+u_oalpha*ub+_uoalpha*ob)*div>>>24;
	}




	private ColorOperations(){}
}
