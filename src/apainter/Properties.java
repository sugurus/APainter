package apainter;

import java.util.HashMap;

/**
 *　
 * プロパティー名=値;プロパティー名=値;………の様な文字列からプロパティーを作成します。<br>
 * 利用可能なプロパティー名<br>
 * <dl>
 * <dt>newlayername</dt><dd>新しいレイヤーを作るときに使われるデフォルト名。<br>初期値はnewLayer</dd>
 * <dt>newgroupname</dt><dd>新しいグループを作るときに使われるデフォルト名。<br>初期値はnewGroup</dd>
 *
 * </dl>
 */
public class Properties{

	public static final String
		PropertyName_newlayername="newlayername",
		PropertyName_newgroupname="newgroupname"

	;


	private void init(){
		set(PropertyName_newlayername, "newLayer");
		set(PropertyName_newgroupname,"newGroup");
	}

	public static Properties decode(String property){
		String[] s = property.split(";");
		String[][] ss = new String[s.length][2];
		int i=0;
		for(String str:s){
			String[] st = str.split("=",2);
			if(st.length==1){
				ss[i][0]=str;
				ss[i][1]="";
			}else{
				ss[i][0]=st[0];
				ss[i][1]=st[1];
			}
			i++;
		}

		return new Properties(ss);
	}

	private HashMap<String, String> map = new HashMap<String, String>();


	private Properties(String[][] s){
		init();
		for(String[] ss:s){
			map.put(ss[0], ss[1]);
		}
	}

	public Properties(){
		this(new String[0][]);
	}



	public String get(String propertyname){
		return map.get( propertyname);
	}

	public String get(String propertyname,String defaultvalue){
		String s = map.get(propertyname);
		return s==null?defaultvalue:s;
	}

	/**
	 *
	 * @param propertyname
	 * @param value
	 * @return 古い値が入っていればそれが返ります。
	 */
	public String set(String propertyname,String value){
		String s =map.put(propertyname, value);
		return s;
	}

}
