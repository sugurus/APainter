package apainter.canvas.layerdata;

import apainter.Handler;
import apainter.canvas.CanvasHandler;
import apainter.data.PixelData;
import apainter.drawer.DrawTarget;
import apainter.hierarchy.Element;
import apainter.rendering.ColorMode;

/**
 * APainter内部で使うLayerHandler
 * @author nodamushi
 *
 */
public abstract class InnerLayerHandler implements MaskContainer,PixelContainer,Handler,LayerHandle,DrawTarget{

	/**
	 * 現在のレンダリングモードに使っているColorModeを返します
	 * @return
	 */
	public abstract ColorMode getRenderingMode();
	/**
	 * レンダリングに使うColorModeを設定します。<br>
	 * 利用不可能なColorModeが来た場合例外を発生させます。
	 * @param mode
	 */
	public abstract void setRenderingMode(ColorMode mode);
	/**
	 * 利用可能なColorModeを返します。
	 * @return
	 */
	public abstract ColorMode[] getUsableModes();
	public abstract String getLayerTypeName();

	/**
	 * ピクセルに対し変更を加えることを通知します。<br>
	 * @param source
	 */
	public abstract void startPaint(Object source);

	/**
	 * イベントの生成がされずペイントが終わるときに呼び出されます。<br>
	 * @param source
	 */
	public abstract void endPaint(Object source);


	abstract Layer getLayer();
	abstract PixelData getOriginalData();
	abstract PixelData getMaskOriginalData();

	private Element<InnerLayerHandler> thiselement=null;
	final synchronized void setElement(Element<InnerLayerHandler> e){
		if(thiselement==null){
			thiselement=e;
		}
	}

	final Element<InnerLayerHandler> getElement(){
		return thiselement;
	}

	final boolean hasElement(){
		return thiselement!=null;
	}

	private static final String p ="%s[ID:%d,Name:%s,ColorMode:%s,hasMask:%s]";
	@Override
	public String toString() {
		return String.format(p, getLayerTypeName(),getID(),getName(),getRenderingMode(),isEnableMask()?"T":"F");
	}

	private LH2 lh = new LH2(this);

	public LayerHandler getLayerHandler(){
		return lh;
	}

	@Override
	public void dispose() {
		lh.l=null;
	}


	private static class LH2 implements LayerHandler{


		@Override
		public boolean isMaskContainer() {
			return l.isMaskContainer();
		}


		@Override
		public String getHandlerName() {
			return "layerhandler";
		}

		private InnerLayerHandler l;
		public LH2(InnerLayerHandler l) {
			this.l =l;
		}

		@Override
		public int getID() {
			return l.getID();
		}

		@Override
		public String getName() {
			return l.getName();
		}

		@Override
		public void setName(String name) {
			l.setName(name);
		}

		@Override
		public void setTransparent(int transparent) {
			l.setTransparent(transparent);
		}

		@Override
		public int getTransparent() {
			return l.getTransparent();
		}

		@Override
		public boolean isVisible() {
			return l.isVisible();
		}

		@Override
		public void setVisible(boolean b) {
			l.setVisible(b);
		}

		@Override
		public boolean isDrawable() {
			return l.isDrawable();
		}

		@Override
		public boolean isEnableMask() {
			return l.isEnableMask();
		}

		@Override
		public void setEnableMask(boolean b) {
			l.setEnableMask(b);
		}

		@Override
		public void createMask() {
			l.createMask();
		}

		@Override
		public boolean isPixelContainer() {
			return l.isPixelContainer();
		}

		@Override
		public boolean isGroup() {
			return l.isGroup();
		}

		@Override
		public boolean isLayer() {
			return l.isLayer();
		}

		@Override
		public CanvasHandler getCanvas() {
			return l.getCanvas().getHandler();
		}

		@Override
		public ColorMode getRenderingMode() {
			return l.getRenderingMode();
		}

		@Override
		public void setRenderingMode(ColorMode mode) {
			l.setRenderingMode(mode);
		}

		@Override
		public ColorMode[] getUsableModes() {
			return l.getUsableModes();
		}

		@Override
		public String getLayerTypeName() {
			return l.getLayerTypeName();
		}

		@Override
		public String toString() {
			return l.toString();
		}

	}

}
