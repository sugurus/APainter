package apainter.data;

import java.io.Serializable;

/**
 * チャット形式でペイントしたいな～、という願望を実現するために、
 * 実際にデーターを送るのではなくて、それのコンテナーを送って復元しようと考えている。<br>
 * 考えているだけで実際には何もしていないので、現状ナゾに組み込まれているインターフェース。<br>
 * @author nodamushi
 *
 */
public interface PixelDataContainer extends Serializable{
	public PixelData getPixelData();
	public int getWidth();
	public int getHeight();
	/**
	 * データの復元をします
	 */
	public void restore();
}
