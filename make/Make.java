import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;



public class Make {

	public static File src = new File("src");
	public static void main(String[] args) throws IOException {
		Runtime r = Runtime.getRuntime();
		String cldir=args[0],jpenjar=args[1];
		try{
			Class<?> clz = Class.forName("jpen.PenManager");
		}catch (Exception e) {
			jpenjar =downloadJPen(jpenjar);
		}
		File f = new File(cldir);
		if(!f.exists()){
			f.mkdirs();
		}
		String cp = "-classpath "+jpenjar;
		String com = "javac -Xlint:unchecked "+cp+" -encoding utf-8 -d "+f.getPath()+" -sourcepath "+src.getPath()+" src/demo/APainterDemo.java";
		System.out.println(com);
		Process p =r.exec(com);
		InputStream in = p.getErrorStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "sjis"));
		String s;
		while((s=br.readLine())!=null)System.out.println(s);
		br.close();
		in = p.getInputStream();
		br = new BufferedReader(new InputStreamReader(in, "sjis"));
		while((s=br.readLine())!=null)System.out.println(s);


		moveToclassdir(src, cldir);

	}

	static private void moveToclassdir(File f,String classdir){
		File[] fs = f.listFiles();
		for(File ff:fs){
			if(ff.isDirectory()){
				moveToclassdir(ff, classdir);
			}if(ff.isFile()){
				String s = ff.getName();
				if(!s.endsWith("java")){
					String path =ff.getPath();
					path = path.replace("src"+File.separator, classdir+File.separator);
					System.out.println("copy to "+path);
					File copy = new File(path);
					FileInputStream in=null;
					FileOutputStream out=null;
					try {
						in = new FileInputStream(ff);
						out = new FileOutputStream(copy);
						byte[] b = new byte[100000];
						int w = 1;
						while(true){
							try {
								w = in.read(b, 0, b.length);
								if(w==-1)break;
								out.write(b, 0, w);
							} catch (IOException e) {
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}finally{
							try {
								if(in!=null)in.close();
								if(out!=null)out.close();
							} catch (IOException e) {
							}
					}
				}
			}
		}
	}




	static URL url;
	static{
		try {
			url = new URL("http://sourceforge.net/projects/jpen/files/");
		} catch (MalformedURLException e) {
			System.exit(1);
		}
	}
	static Pattern p = Pattern.compile("<a href=\"(.*?download)\" title=\"/jpen");


	public static String downloadJPen(String savefolder) throws IOException {
		if(savefolder==null)savefolder = ".";
		File ff = new File(savefolder);
		File zipfolder = new File(ff.getParent(), "jpen.zip");
		if(!ff.getParentFile().exists()){
			ff.getParentFile().mkdirs();
		}
		URLConnection c=null;
		InputStream in=null;
		OutputStream out=null;
		try{
			c = url.openConnection();

			in = c.getInputStream();
			Scanner s  = new Scanner(in);
			StringBuilder sb  = new StringBuilder();
			while(s.hasNext()){
				sb.append(s.nextLine()).append("\n");
			}
			in.close();
			in =null;
			String html = sb.toString();
			Matcher m = p.matcher(html);
			if(m.find()){
				String u = m.group(1);
				String d = u.replace("/download","").replace("http://", "http://downloads.").replace("src.zip","lib.zip")
						.replace("projects","project").replace("files/","");

				String path = d+"?r=&ts="+System.currentTimeMillis()/1000+"&use_mirror=cdnetworks-kr-2";
				URL zipurl = new URL(path);
				out = new FileOutputStream(zipfolder);
				in = zipurl.openStream();
				byte[] b = new byte[10000];
				int write = 1;
				while(true){
					write = in.read(b);
					if(write==-1)break;
					out.write(b, 0, write);
				}
			}
		}finally{
			if(in!=null)in.close();
			if(out!=null)out.close();
		}


		String dir=unzip(zipfolder,ff);
		File dirf = new File(dir);
		File[] fs = dirf.listFiles();
		for(File f:fs){
			String name = f.getName();
			File f2 = new File(ff.getParentFile(), name);
			f.renameTo(f2);
		}
		dirf.delete();
		zipfolder.delete();
		return new File(ff.getParentFile(),"jpen-2.jar").getAbsolutePath();
	}

	public static String unzip(File file,File jarposition) throws ZipException, IOException {

		File baseDir =jarposition.getParentFile();
		String ret  =null;

		ZipFile zipFile = new ZipFile(file);
		try{
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();

				File outFile = new File(baseDir, ze.getName());
				if (ze.isDirectory()) {
					outFile.mkdirs();
					ret = outFile.getPath();
				} else {

					BufferedInputStream bis = null;
					BufferedOutputStream bos = null;
					try {
						InputStream is = zipFile.getInputStream(ze);
						bis = new BufferedInputStream(is);

						if (!outFile.getParentFile().exists()) {
							outFile.getParentFile().mkdirs();
						}

						bos =
								new BufferedOutputStream(new FileOutputStream(
										outFile));

						int ava;
						while ((ava = bis.available()) > 0) {
							byte[] bs = new byte[ava];
							bis.read(bs);
							bos.write(bs);
						}
					} catch (FileNotFoundException e) {
						throw e;
					} catch (IOException e) {
						throw e;
					} finally {
						try {
							if (bis != null)
								bis.close();
						} catch (IOException e) {
						}
						try {
							if (bos != null)
								bos.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}finally{
			zipFile.close();
		}
		return ret;
	}

}