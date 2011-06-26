package apainter.construct;

import static java.lang.Math.*;

import java.io.Serializable;

public final class Angle implements Serializable{

	private static final long serialVersionUID = -3757366464489953423L;

	public static Angle getAngle(double x,double y){
		if(x==0){
			if(y>0)return new Angle(90);
			else if(y<0)return new Angle(270);
			else return new Angle();
		}
		double tan = y/x;
		double atan = atan(tan)/2/PI;
		if(x<0)atan+=0.5;
		return new Angle(atan*360);
	}


	public static double degreesToRadian(double degree){
		return degree*Math.PI/180;
	}
	public static double radiansToDegree(double radian){
		return radian*180/Math.PI;
	}
	public static Angle createFromRadians(double radian){
		return new Angle(radiansToDegree(radian));
	}

	public static Angle createFromTan(double tan){
		return new Angle(radiansToDegree(Math.atan(tan)));
	}

	public static Angle createFromSin(double sin){
		return new Angle(radiansToDegree(Math.asin(sin)));
	}
	public static Angle createFromCos(double cos){
		return new Angle(radiansToDegree(Math.cos(cos)));
	}

	public final double degree;//°
	public final double radian;//弧度法
	public final double cos;
	public final double sin;
	public final double tan;
	/**
	 * @param degree 度数法（°）
	 */
	public Angle(double degree){
		if(Double.isNaN(degree))throw new RuntimeException("degree is not a Number!");
		if(degree<0){
			int t = (int)(-degree/360);
			degree += 360*t;
			if(degree < 0)degree+=360;
		}else if(degree >= 360){
			int t = (int)(degree/360);
			degree -= 360*t;
		}
		this.degree = degree;
		radian = degree*Math.PI/180;
		cos = Math.cos(radian);
		sin = Math.sin(radian);
		tan = Math.tan(radian);
	}
	public Angle(){
		this(0);
	}

	/**
	 * y軸周りに回転させた角度を返します。
	 * @return
	 */
	public Angle reverse_y(){
		if(degree <=180){
			return new Angle(180-degree);
		}else{
			return new Angle(180*3-degree);
		}
	}

	/**
	 * x軸周りに回転させた角度を返します。
	 * @return
	 */
	public Angle reverse_x(){
		return new Angle(360-degree);
	}

	public Angle add(double degrees){
		return new Angle(this.degree+degrees);
	}
	public Angle add(Angle angle){
		return add(angle.degree);
	}

	public Angle mult(double n){
		return new Angle(this.degree*n);
	}

	public boolean isZero(){return radian==0;}

}
