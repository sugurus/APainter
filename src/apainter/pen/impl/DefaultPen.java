package apainter.pen.impl;

import java.awt.Dimension;

import apainter.construct.DimensionDouble;
import apainter.data.PixelDataBuffer;
import apainter.pen.PenShape;

//TODO DefaultPenの作成
public class DefaultPen implements PenShape{

	@Override
	public PixelDataBuffer getFootPrint(double x, double y, double w, double h) {

		return null;
	}

	@Override
	public Dimension getMapSize(double w, double h) {
		return null;
	}

	@Override
	public DimensionDouble getSize() {
		return null;
	}

	@Override
	public double getIntervalLength(double w, double h) {
		return 0;
	}

	@Override
	public void setIntervalLengthPercent(double percent) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public long getID() {
		return 0;
	}

}
