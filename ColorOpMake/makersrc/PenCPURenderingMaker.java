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



public class PenCPURenderingMaker {
	static String p = "PenCPURendering.java",p2="keys.txt",p3="../src/apainter/drawer/painttool/PenCPUDefaultRendering.java",penjava="Pen.java",outpenjava="../src/apainter/drawer/painttool/Pen.java";
	static String imports="package apainter.drawer.painttool;\n\nimport static apainter.misc.Utility_PixelFunction.*;\nimport static apainter.rendering.ColorOperations.*;\n\nimport java.awt.Point;\nimport java.awt.Rectangle;\n\nimport apainter.data.PixelDataByteBuffer;\nimport apainter.data.PixelDataIntBuffer;\nimport apainter.rendering.RenderingOption;\n\n";

	public static void main(String[] args) throws IOException {
		String javaf =  load(p);
		String[] keys = loadKeys(p2);
		HashMap<String, String> data = new HashMap<String, String>();
		Template templ = Mustache.compiler().compile(javaf);
		StringBuilder sb = new StringBuilder(imports),cpurenderers=new StringBuilder(),
		switchblock = new StringBuilder();
		int ii=0;
		for(String key:keys){
			String clname = key.substring(0,1).toUpperCase()+key.substring(1);
			data.put("OpClassName",clname);
			data.put("opename",key);
			String ret=templ.execute(data);
			sb.append(ret).append("\n");
			cpurenderers.append("			new PenCPU").append(clname).append("Rendering(),\n");
			switchblock.append("		case ").append(clname).append(":\n			r=cpuren[").append(ii++).append("];\n			break;\n");
		}
		writeFile(sb.toString().replaceAll("&gt;", ">").replaceAll("&lt;", "<"),p3.replace('/',File.separatorChar));
		
		javaf = load(penjava);
		templ = Mustache.compiler().compile(javaf);
		data.clear();
		String t = cpurenderers.toString();
		t = t.substring(0,t.length()-2);
		data.put("cpurenderers",t);
		data.put("switchblock",switchblock.toString());
		t = templ.execute(data);
		writeFile(t.replaceAll("&gt;", ">").replaceAll("&lt;", "<"),outpenjava.replace('/',File.separatorChar));
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