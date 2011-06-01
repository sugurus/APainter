package apainter.rendering;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;

public interface Renderer2 extends Renderer{

	/**
	 * baseにoverおよびover2で与えられた画素をbase上の点pを左上（原点）の位置とし、
	 * clipで与えられる範囲に順に描き込みます。<br>
	 * clipの座標はbaseの座標系です。<br>
	 * 配列への書き込みを削減し高速化するための関数です。<br>
	 * overおよびover2の位置をずらして描画するようなことはできません。
	 * <font color="red">与えられた範囲のチェックはこの関数は行いません。</font>
	 * また、実装においてする必要もありません。<br>
	 * clipの範囲がbaseの外だったり、範囲内にoverがいないなどの状況ではNullPointerException等の
	 * 予期せぬ例外が発生します。<br>
	 * <font color="red"><b>範囲チェックの責任は呼び出し側にあります。</b></font>
	 * 必ず範囲のチェックを怠らないでください。<br>
	 * RenderingUtilities#getEnableClipBoundsが利用できます。
	 * Rendererが受け付けることのできるPixelDataBufferは決まっていることがあります。<br>
	 * 受け付けることができるタイプ以外を受け付けた場合の挙動は定義していません。<br><br>
	 * また、overとover2の型は必ず同じでなくてはなりません
	 * @see RenderingUtilities#getEnableClipBounds(PixelDataBuffer,PixelDataBuffer,Point,Rectangle) getEnableClipBounds
	 * @param base 描き込まれる画像
	 * @param over 描き込む画像
	 * @param over2 描き込む画像2
	 * @param p overの左上の点のbase上での位置
	 * @param clip baseのどの範囲をレンダリングするか
	 * @param option レンダリングオプション　使い方はRenderの実装によります。
	 */
	public void rendering(PixelDataBuffer base,PixelDataBuffer over,PixelDataBuffer over2,
			Point p,Rectangle clip,RenderingOption option);

}
