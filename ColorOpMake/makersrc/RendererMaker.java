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



public class RendererMaker {
	static String p = "Renderer.java",p2="keys.txt",p3="../src/apainter/rendering/impl/cpu/{{OpClassName}}Renderer.java";

	public static void main(String[] args) throws IOException {
		String javaf =  load(p);
		String[] keys = loadKeys(p2);
		HashMap<String, String> data = new HashMap<String, String>();
		Template templ = Mustache.compiler().compile(javaf),
		templ2 = Mustache.compiler().compile(p3);
		for(String key:keys){
			String clname = key.substring(0,1).toUpperCase()+key.substring(1);
			data.put("OpClassName",clname);
			data.put("opename",key);
			String ret=templ.execute(data);
			String path = templ2.execute(data).replace('/',File.separatorChar);
			writeFile(ret,path);
		}
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


