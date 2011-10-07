package apainter.data;

/**
 * チャット形式でペイントしたいな～、という願望を実現するために、
 * 実際にデーターを送るのではなくて、それのコンテナーを送って復元しようと考えている。<br>
 * 考えているだけで実際には何もしていないので、現状ナゾに組み込まれているインターフェース。<br>
 * @author nodamushi
 *
 */
public interface PixelDataContainer {
	public PixelData getPixelData();
	public int getWidth();
	public int getHeight();
}
