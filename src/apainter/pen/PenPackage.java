package apainter.pen;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


import static apainter.pen.APainterPen.*;
public class PenPackage{
	private ArrayList<Group> groups = new ArrayList<Group>();
	private String name;
	private int colormode;


	public PenPackage(String name,int mode) {
		this.name = name;
		if(mode<0 || mode >2)throw new RuntimeException("unsupertedMode "+mode);
		this.colormode = mode;
	}

	public ArrayList<Group> getGroups(){
		return new ArrayList<Group>(groups);
	}


	public Group createGroup(int size,int width,int height,int centerx,int centery,int xblocks,int yblocks){
		Group g = new Group(size, width, height,centerx,centery, xblocks, yblocks,colormode);
		groups.add(g);
		return g;
	}

	public String getName(){
		return name;
	}

	public void write(OutputStream out) throws IOException{
		if(groups.isEmpty())return;
		writeShort(out, 0xff20);
		out.write(colormode);
		writeShort(out, groups.size());
		if(name != null){
			byte[] b = name.getBytes("utf-8");
			writeShort(out,0xff21);
			out.write(b.length);
			out.write(b);
		}
		for(Group g :groups){
			g.write(out);
		}

		writeShort(out,0xffff);
	}
}