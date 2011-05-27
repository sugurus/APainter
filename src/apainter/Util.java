package apainter;

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














	private Util(){}

}
