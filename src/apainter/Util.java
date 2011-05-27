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
