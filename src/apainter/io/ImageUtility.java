package apainter.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import apainter.misc.Util;

public class ImageUtility {


	public static Toolkit getToolkit(){
		return Toolkit.getDefaultToolkit();
	}

	public static Image createImage(URL url){
		return getToolkit().createImage(url);
	}

	public static Image createImage(String filepath){
		return getToolkit().createImage(filepath);
	}

	public static Image createImage(int[] pixel,int w,int h,boolean transparency){
		if(pixel.length < w*h)
			throw new RuntimeException
			(String.format("pixel size(%d) is small!needs %d",pixel.length,w*h));
		MemoryImageSource m;
		if(transparency){
			m = new MemoryImageSource(w, h, ColorModel.getRGBdefault(), pixel, 0, w);
		}else{
			ColorModel model = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
			m = new MemoryImageSource(w, h, model, pixel, 0, w);
		}
		return getToolkit().createImage(m);
	}

	public static Image createSubImage(int[] pixel,int fullwidth,int fullheight,
			int x,int y,int subwidth,int subheight,boolean transparency){
		if(pixel.length < fullwidth*fullheight)
			throw new RuntimeException
			(String.format("pixel size(%d) is small!needs %d",pixel.length,fullwidth*fullheight));
		MemoryImageSource m;
		if(transparency){
			m = new MemoryImageSource(subwidth, subheight, ColorModel.getRGBdefault(), pixel, x+y*fullwidth, fullwidth);
		}else{
			ColorModel model = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
			m = new MemoryImageSource(subwidth, subheight,model, pixel, x+y*fullwidth, fullwidth);
		}
		return getToolkit().createImage(m);
	}

	public static BufferedImage copyToBufferedImage(Image img){
		int w = img.getWidth(null),h=img.getHeight(null);
		if(w == -1)throw new RuntimeException("incomplete image");
		BufferedImage bf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bf.createGraphics();
		g.setBackground(new Color(0,true));
		g.clearRect(0,0,w,h);
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return bf;
	}









	///////////////////////////////////for debag


	public static void save(Image img,String filapath){
		File  f = new File(filapath);
		save(img, f);
	}

	public static void save(Image img,File filepath){
		if(img ==null)throw new NullPointerException("img");
		if(filepath==null)throw new NullPointerException("filepath");
		if (img instanceof BufferedImage) {
			saveBufferedImage((BufferedImage) img, filepath);
		}else{
			saveBufferedImage(copyToBufferedImage(img), filepath);
		}
	}

	private static void saveBufferedImage(BufferedImage img,File filepath){
		String sf = Util.getSuffix(filepath);
		try {
			ImageIO.write(img, sf, filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private ImageUtility(){}
}
