package apainter.misc;

import static java.lang.Math.*;

import java.io.Serializable;
/**
 * 角度を表すクラスです。<br>
 * フィールドはイミュータブルで書き換えることはできません。
 */
public final class Angle implements Serializable{

	private static final long serialVersionUID = -3757366464489953423L;

	/**
	 * 座標がx軸となす角度を返します。x=y=0の時は0の角度を返します。
	 * @param x
	 * @param y
	 * @return
	 */
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


	/**
	 * 度数法で表された角度を弧度法に変換します
	 * @param degree 度数
	 * @return 弧度法で表された角度
	 */
	public static double degreesToRadian(double degree){
		return degree*Math.PI/180;
	}
	/**
	 * 弧度法で表された角度を度数法に変換します
	 * @param radian 弧度法
	 * @return 度数法で表された角度
	 */
	public static double radiansToDegree(double radian){
		return radian*180/Math.PI;
	}
	/**
	 * 弧度法の値からAngleインスタンスを作成します。
	 * @param radian
	 * @return
	 */
	public static Angle createFromRadians(double radian){
		return new Angle(radiansToDegree(radian));
	}

	/**
	 * タンジェントの値からAngleを作成します<br>
	 * 作成されるAngleは0～90度、270～360度の範囲になります。<br>
	 * @param tan
	 * @return
	 */
	public static Angle createFromTan(double tan){
		return new Angle(radiansToDegree(Math.atan(tan)));
	}
	/**
	 * サインの値からAngleを作成します<br>
	 * 作成されるAngleは0～90度、270～360度の範囲になります。<br>
	 * @param tan
	 * @return
	 */
	public static Angle createFromSin(double sin){
		return new Angle(radiansToDegree(Math.asin(sin)));
	}
	/**
	 * コサインの値からAngleを作成します<br>
	 * 作成されるAngleは0～90度、270～360度の範囲になります。<br>
	 * @param tan
	 * @return
	 */
	public static Angle createFromCos(double cos){
		return new Angle(radiansToDegree(Math.cos(cos)));
	}

	/**
	 * 度数法で表された角度です
	 */
	public final double degree;//°
	/**
	 * 弧度法で表された角度です
	 */
	public final double radian;//弧度法
	/**
	 * cos値です
	 */
	public final double cos;
	/**
	 * sin値です
	 */
	public final double sin;
	/**
	 * tan値です
	 */
	public final double tan;
	/**
	 * 新たなインスタンスを作成します。
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
	/**
	 * 角度0の新たなインスタンスを作成します。
	 */
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

	/**
	 * 加算をします。
	 * @param degrees
	 * @return
	 */
	public Angle add(double degrees){
		return new Angle(this.degree+degrees);
	}
	/**
	 * 加算をします
	 * @param angle
	 * @return
	 */
	public Angle add(Angle angle){
		return add(angle.degree);
	}

	/**
	 * かけ算をします。
	 * @param n
	 * @return
	 */
	public Angle mult(double n){
		return new Angle(this.degree*n);
	}

	/**
	 * この角度が0かどうかを返します。
	 * @return
	 */
	public boolean isZero(){return radian==0;}

}
