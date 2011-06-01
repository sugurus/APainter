package apainter;

import java.io.File;

public class Util {

	public static boolean bitFlag(int data,int flag){
		return (data&flag)!=0;
	}

	/**
	 * 渡されたフラグの内どれかがたっていればtrue
	 * @param data
	 * @param flag
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











	private Util(){}

}
