package apainter.drawer;

import java.awt.geom.Point2D;

import nodamushi.pentablet.PenTabletMouseEvent;

/**
 * Drawerが描画する線を定義するためのクラスです。<br>
 * Drawerはこの情報を元に、線を生成します。<br>
 * 位置情報、筆圧、ペンの傾き、回転角度を保持します。
 *
 */
public class DrawPoint {


	public final double x,y;
	public final double pressure,
	alititudex,alititudey,//ペンの傾き
	rotation;//回転角度;


	@Override
	public String toString() {
		return String.format("Location(%.3f,%.3f),Pressrue %.3f,Alititude(%.3f,%.3f),Rotation %.3f", x,y,pressure,alititudex,alititudey,rotation);
	}
	/**
	 * 位置情報からDrawPointを生成します。筆圧は1、傾き回転は0で設定されます。
	 * @param x x座標
	 * @param y y座標
	 */
	public DrawPoint(double x,double y){
		this(x,y,1,0,0,0);
	}

	/**
	 * 位置情報からDrawPointを生成します。筆圧は1、傾き回転は0で設定されます。
	 * @param location
	 */
	public DrawPoint(Point2D location){
		this(location.getX(),location.getY());
	}

	/**
	 * 位置情報と筆圧でDrawPointを生成します。
	 * @param x x座標
	 * @param y y座標
	 * @param pressure 筆圧
	 */
	public DrawPoint(double x,double y,double pressure){
		this(x,y,pressure,0,0,0);
	}

	/**
	 * 位置情報と筆圧でDrawPointを生成します。
	 * @param location
	 * @param pressure 筆圧
	 */
	public DrawPoint(Point2D location,double pressure){
		this(location.getX(),location.getY(),pressure);
	}

	/**
	 *
	 * @param x x座標
	 * @param y y座標
	 * @param pressure 筆圧
	 * @param alititudex x軸方向の傾き
	 * @param alititudey y軸方向の傾き
	 * @param rotation ペンの回転角度
	 */
	public DrawPoint(double x,double y,double pressure,double alititudex,
			double alititudey,double rotation) {
		if(pressure > 1)pressure = 1;
		else if(pressure < 0)pressure = 0;
		this.x = x;
		this.y = y;
		this.pressure = pressure;
		this.alititudex = alititudex;
		this.alititudey = alititudey;
		this.rotation = rotation;
	}

	public Point2D getLocation(){
		return new Point2D.Double(x,y);
	}

	public double getX() {
		return x;
	}


	public double getY() {
		return y;
	}

	/**筆圧を返します*/
	public double getPressure() {
		return pressure;
	}

	/**ペンの傾きx軸方向を返します*/
	public double getAlititudex() {
		return alititudex;
	}

	/**ペンの傾きy軸方向を返します*/
	public double getAlititudey() {
		return alititudey;
	}

	/**ペンの回転角度を返します*/
	public double getRotation() {
		return rotation;
	}

	/**
	 * PenTabletMouseEventからDrawPointに変換します。
	 * @param e
	 * @return
	 */
	public static DrawPoint convert(PenTabletMouseEvent e){
		Point2D.Double d =e.getPointDouble();
		return new DrawPoint(d.x, d.y, e.getPressure(),
				e.getAlititudeX(), e.getAlititudeY(), e.getRotation());
	}
}
