package apainter.drawer.event;

import static apainter.misc.Util.*;

import java.awt.Point;
import java.awt.Rectangle;

import apainter.data.PixelDataBuffer;
import apainter.drawer.DrawTarget;
import apainter.drawer.Drawer;
import apainter.rendering.Renderer;
import apainter.rendering.RenderingOption;

/**
 * ターゲットに対してフットプリントを書き込むイベントです。
 */
public class DrawEvent extends DrawerEvent{

	private Rectangle rect;
	private Point footpoint;
	private PixelDataBuffer footprint;
	private RenderingOption option;
	private Renderer renderer;

	public DrawEvent(Drawer source,
			DrawTarget target,Rectangle bounds,Point footpoint,
			Renderer renderer,PixelDataBuffer footprint,RenderingOption option) {
		super(source,target);
		this.renderer = nullCheack(renderer,"renderer");
		this.rect = nullCheack(bounds,"bounds");
		this.footprint = nullCheack(footprint,"footprint");
		this.option = nullCheack(option,"option");
		this.footpoint  =nullCheack(footpoint,"footpoint");
	}

	/**
	 * このイベントのレンダリング領域に含まれるrの部分集合をレンダリング範囲とする部分イベントを生成します。
	 * @param r 部分イベントのレンダリング範囲
	 * @throws RuntimeException rが部分集合でないとき例外が発生します。
	 * @return 部分イベント
	 * @see DrawEvent#getBounds()
	 */
	public DrawEvent subsetEvent(Rectangle r){
		if(!rect.contains(r)){
			throw new RuntimeException("r isn't subset");
		}
		return new DrawEvent(drawer, target, r,footpoint, renderer,  footprint, option);
	}


	/**
	 * レンダラーを返します。
	 */
	public Renderer getRenderer(){
		return renderer;
	}

	/**
	 * レンダリング範囲を返します。
	 */
	public Rectangle getBounds(){
		return (Rectangle) rect.clone();
	}

	/**
	 * フットプリントの左上の設置位置を返します。
	 * @return
	 */
	public Point getLocation(){
		return footpoint;
	}

	/**
	 * レンダリングのオプション項目を返します。
	 * @return
	 */
	public RenderingOption getOption(){
		return option;
	}

	/**
	 * フットプリントを返します
	 */
	public PixelDataBuffer getFootprint(){
		return footprint;
	}
	@Override
	public String toString() {
		return "draw event:Drawer["+drawer+"],DrawTarget["+target+"],Bounds["
				+rect+"],locate("+footpoint.x+","+footpoint.y+"),footprint["+footprint+"]"+
				"renderer["+renderer+"]";
	}
}

