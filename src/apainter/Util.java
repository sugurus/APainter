package apainter;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;

public class Util {

	public static boolean bitFlag(int data,int flag){
		return (data&flag)!=0;
	}

	public static Dimension getImageSize(Image img){
		return new Dimension(img.getWidth(null),img.getHeight(null));
	}

	public static Dimension getImageSize(Image img,ImageObserver ob){
		return new Dimension(img.getWidth(ob),img.getHeight(null));
	}


	/**
	 * 渡されたフラグの内どれかがたっていればtrue
	 * @param data
	 * @param flags
	 * @return
	 */
	public static boolean bitFlagsOr(int data,int... flag){
		int t=0;
		for(int f:flag){
			t |=f;
		}
		return (data & t)!=0;
	}
	/**
	 * 渡されたフラグが全て立っていたらtrue
	 * @param data
	 * @param flag
	 * @return
	 */
	public static boolean bitFlagsAnd(int data,int... flag){
		int t=0;
		for(int f:flag){
			t |=f;
		}
		return (data & t)==t;
	}

	public static int abs(int a){
		return a < 0 ? -a:a;
	}

	/**
	 * a mod b
	 * @param a
	 * @param b
	 * @return 正数値
	 */
	public static int mod(int a,int b){
		if(a < 0){
			return a%b + b;
		}else return a %b;
	}

	/**
	 * a mod b
	 * @param a
	 * @param b
	 * @return 正数値
	 */
	public static double mod(double a,int b){
		if(a < 0){
			return a %b +b;
		}
		else{
			return a %b;
		}
	}


	/**
	 * 最大値とその場所を返します
	 * @param arr 0番目に最大値、それ以降は最大値のあった場所を返します。
	 * @return
	 */
	public static int[] max(int... arr){
		int m = Integer.MIN_VALUE;
		int t =0;
		for(int a:arr)
			if(a > m){
				m = a;
				t=0;
			}else if(m==a){
				t++;
			}
		int[] ret = new int[t+1];
		ret[0] = m;
		for(int i=0,l=1;i<ret.length;i++){
			if(arr[i]==m)ret[l++]=i;
		}
		return ret;
	}

	/**
	 * 最小値とその場所を返します
	 * @param arr 0番目に最小値、それ以降は最小値のあった場所を返します。
	 * @return
	 */
	public static int[] min(int... arr){
		int m = Integer.MAX_VALUE;
		int t =0;
		for(int a:arr)
			if(a < m){
				m = a;
				t=0;
			}else if(m==a){
				t++;
			}
		int[] ret = new int[t+1];
		ret[0] = m;
		for(int i=0,l=1;i<ret.length;i++){
			if(arr[i]==m)ret[l++]=i;
		}
		return ret;
	}

	/**
	 * 最大値と最小値を返します
	 * @param arr 順に最大値、最小値
	 * @return
	 */
	public static int[] max_min(int... arr){
		int mi = Integer.MAX_VALUE,ma = Integer.MIN_VALUE;
		for(int a:arr){
			if(a > ma){
				ma = a;
			}else if(a < mi){
				mi = a;
			}
		}
		return new int[]{ma,mi};
	}



	/**
	 * ファイル名から拡張子を返します。
	 * @param fileName ファイル名
	 * @return ファイルの拡張子
	 * @see <a href="http://sattontanabe.blog86.fc2.com/blog-entry-38.html">Java ファイル名から拡張子を取得 /Chat&Messenger</a>
	 */
	public static String getSuffix(String fileName) {
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point + 1);
	    }
	    return fileName;
	}

	/**
	 * ファイル名から拡張子を返します。
	 * @param fileName ファイル
	 * @return ファイルの拡張子
	 */
	public static String getSuffix(File f){
		if(f==null)return null;
		return getSuffix(f.toString());
	}



	//XXX 誰か教えて！うまい領域分割の方法
	/**
	 * m分割した領域を返します。
	 * @param r
	 * @param m
	 * @return
	 */
	public static Rectangle[] partition(Rectangle r,int m){
		if(m <1)return null;
		if(r.height%m==0){
			Rectangle[] ret = new Rectangle[m];
			int h = r.height/m;
			for(int i=0;i<m;i++){
				ret[i] = new Rectangle(0,i*h,r.width,h);
			}
			return ret;
		}else if(r.width%m ==0){
			Rectangle[] ret = new Rectangle[m];
			int w = r.width/m;
			for(int i=0;i<m;i++){
				ret[i] = new Rectangle(i*w,0,w,r.height);
			}
			return ret;
		}else{

			//ここひどいよなぁ。
			Rectangle[] ret = new Rectangle[m];

			if(r.width  < r.height){
				int ii = 0;
				for(int i=0;i<m;i++){
					int i1 = (i+1)*r.height/m;
					ret[i] = new Rectangle(0,ii,r.width,i1-ii);
					ii = i1;
				}
			}else{
				int ii = 0;
				for(int i=0;i<m;i++){
					int i1 = (i+1)*r.width/m;
					ret[i] = new Rectangle(ii,0,i1-ii,r.height);
					ii = i1;
				}
			}

			return ret;
		}
	}










	private Util(){}

}
