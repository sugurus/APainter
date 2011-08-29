package apainter.annotation;

import static java.lang.Math.*;

import java.util.Comparator;


@Version("1.0.0")
public class VersionUtillity {

	public static final int
	/**値が同じ事を示す*/
		EQUAL=0,
	/**値が大きい事を示す*/
		LARGE=1,
	/**値が同じ事を示す*/
		SMALL=-1,
	/**バージョンが定義されていない事を示す*/
		UNDEFINED=-2;

	/**
	 * バージョン文字列を取り出します。
	 * @param claz
	 * @return バージョン文字列　設定されていない場合はnullが返る。
	 */
	public static String getVersion(Class<?> claz){
		Version v = claz.getAnnotation(Version.class);
		return v!=null? v.value():null;
	}


	private final static Comparator<String> comparator= new Comparator<String>() {
		public int compare(String o1, String o2) {
			if("".equals(o1))o1="0";
			if("".equals(o2))o2="0";
			int a = Integer.parseInt(o1),b=Integer.parseInt(o2);
			return a-b;
		}
	};

	/**
	 * バージョンの大小を比較します。数値比較、分割文字「.」で比較します。
	 * @param a バージョンを示す文字列
	 * @param b バージョンを示す文字列
	 * @return バージョンが定義されていないとき、UNDEFINED,aがbより大きいバージョンの場合LARGE、小さい場合SMALL、同じ時EQUAL
	 */
	public static int compare(Class<?> claz,String b){
		return compare(claz,b,"\\.");
	}

	/**
	 * バージョンの大小を比較します。数値比較で比較します。
	 * @param a バージョンを示す文字列
	 * @param b バージョンを示す文字列
	 * @param splitRegex バージョン文字列を分割する正規表現文字列。
	 * @return バージョンが定義されていないとき、UNDEFINED,aがbより大きいバージョンの場合LARGE、小さい場合SMALL、同じ時EQUAL
	 */
	public static int compare(Class<?> claz,String b,String splitRegex){
		return compare(claz,b,splitRegex,comparator);
	}

	/**
	 * バージョンの大小を比較します。
	 * @param a バージョンを示す文字列
	 * @param b バージョンを示す文字列
	 * @param splitRegex バージョン文字列を分割する正規表現文字列。
	 * @param compare 比較関数。1.0と1.0.0といった区切りの数が異なる値を比較するとき、足りない方は""が渡されます。
	 * @return バージョンが定義されていないとき、UNDEFINED,aがbより大きいバージョンの場合LARGE、小さい場合SMALL、同じ時EQUAL
	 */
	public static int compare(Class<?> claz,String b,String splitRegex,Comparator<String> compare){
		String a  = getVersion(claz);
		if(a==null){
			return UNDEFINED;
		}
		return compare(a, b, splitRegex,compare);
	}

	/**
	 * バージョンの大小を、数値比較、分割文字「.」で比較します。
	 * @param a バージョンを示す文字列
	 * @param b バージョンを示す文字列
	 * @return aがbより大きいバージョンの場合LARGE、小さい場合SMALL、同じ時EQUAL
	 * @see VersionUtillity#compare(String,String,String,Comparator)
	 */
	public static int compare(String a,String b){
		return compare(a, b, "\\.");
	}

	/**
	 * バージョンの大小を、数値比較で比較します。
	 * @param a バージョンを示す文字列
	 * @param b バージョンを示す文字列
	 * @param splitRegex バージョン文字列を分割する正規表現文字列。
	 * @return aがbより大きいバージョンの場合LARGE、小さい場合SMALL、同じ時EQUAL
	 * @see VersionUtillity#compare(String,String,String,Comparator)
	 */
	public static int compare(String a,String b,String splitRegex){
		return compare(a, b, splitRegex, comparator);
	}

	/**
	 * バージョンの大小を比較します。
	 * @param a バージョンを示す文字列
	 * @param b バージョンを示す文字列
	 * @param splitRegex バージョン文字列を分割する正規表現文字列。
	 * @param compare 比較関数。1.0と1.0.0といった区切りの数が異なる値を比較するとき、足りない方は""が渡されます。
	 * @return aがbより大きいバージョンの場合LARGE、小さい場合SMALL、同じ時EQUAL
	 */
	public static int compare(String a,String b,String splitRegex,Comparator<String> compare){
		String[] aa = a.split(splitRegex);
		String[] bb= b.split(splitRegex);
		int max = max(aa.length, bb.length);

		int ret=0;
		for(int i=0;i<max;i++){
			String A = i<aa.length?aa[i]:"";
			String B = i<bb.length?bb[i]:"";

			int c = compare.compare(A, B);
			if(c<0){
				ret = -1;
				break;
			}else if(c>0){
				ret = 1;
				break;
			}
		}

		return ret;
	}

	private VersionUtillity() {}

}
