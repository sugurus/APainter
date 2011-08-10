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

	{{functions}}



	private ColorOperations(){}
}
