package apainter.pen;

public interface PenShapeFactory {

	public String getPenName();
	public Class<? extends PenShape> getPenShapeClass();
	public PenShape createPenShape(double width,double height);
	public long getID();

}
