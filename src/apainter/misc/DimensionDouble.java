package apainter.misc;

import java.awt.geom.Dimension2D;

public class DimensionDouble extends Dimension2D{

	public double width,height;

	public DimensionDouble(double w,double h) {
		setSize(w,h);
	}
	public DimensionDouble() {
		this(0,0);
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

}
