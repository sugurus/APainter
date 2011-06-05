package apainter.canvas.layerdata;

interface PixelContainer {
	/**
	 * 点(x,y)の色を返します。
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPixel(int x,int y);
	/**
	 * 範囲の色データを入れた配列を返します。
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public int[] getPixels(int x,int y,int width,int height);
	/**
	 * 指定された範囲内の色を渡された配列にコピーします。
	 * @param x 始点
	 * @param y 始点
	 * @param width 幅
	 * @param height 高さ
	 * @param distenation コピー先の配列
	 * @return
	 */
	public int[] copyPixels(int x,int y,int width,int height,int[] distenation);
}
