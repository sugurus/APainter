import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;



public class ColorOpMaker {
	static String p = "ColorOperations.java",p2="keys.txt",p3="../src/apainter/rendering/ColorOperations.java"
		,tempf ="template.txt";

	public static void main(String[] args) throws IOException {
		String javaf =  load(p);
		String temp = load(tempf);
		String[] keys = loadKeys(p2);
		HashMap<String, String> data = new HashMap<String, String>();
		Template templ = Mustache.compiler().compile(temp);
		StringBuilder sb = new StringBuilder();
		for(String key:keys){
			data.put("opename",key);
			sb.append(templ.execute(data));
		}
		data.clear();
		data.put("functions", sb.toString());
		templ = Mustache.compiler().compile(javaf);
		String s = templ.execute(data);
		s = s.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
		
		writeFile(s, p3.replace('/',File.separatorChar));
	}


	private static String load(String path) throws IOException{
		File f = new File(path);
		Scanner scan = new Scanner(f,"utf-8");
		StringBuilder sb = new StringBuilder();
		while(scan.hasNext()){
			sb.append(scan.nextLine()).append("\n");
		}
		scan.close();
		return sb.toString();
	}

	private static String[] loadKeys(String path) throws FileNotFoundException{
		File f = new File(path);
		Scanner scan = new Scanner(f,"utf-8");
		ArrayList<String> s = new ArrayList<String>();
		while(scan.hasNext()){
			s.add(scan.nextLine());
		}
		scan.close();
		return s.toArray(new String[s.size()]);
	}

	private static void writeFile(String str,String p) throws IOException{
		File f = new File(p);
		FileOutputStream w = new FileOutputStream(f);
		OutputStreamWriter os = new  OutputStreamWriter(w, "utf-8");
		os.write(str);
		os.close();
	}
}


