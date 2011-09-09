package apainter.history;

import java.awt.Rectangle;

import apainter.canvas.Canvas;
import apainter.canvas.cedt.cpu.CPUParallelWorkThread;
import apainter.data.CompressedPixelData;
import apainter.data.PixelDataBuffer;
import apainter.data.PixelDataByteBuffer;
import apainter.data.PixelDataIntBuffer;
import apainter.misc.Util;
import apainter.resorce.LimitedResource;
import apainter.resorce.Resource;

public abstract class PixelChangeHistory extends HistoryObject{

	//準備用
	protected Canvas canvas;
	private Resource<PixelDataIntBuffer> resource;
	private PixelDataBuffer copy;
	private int width,height;
	private boolean inited1=false;


	//基本データ////////////////
	private boolean inited=false;
	protected boolean compressed=false;

	protected Rectangle bounds;
	protected PixelDataBuffer beforebuffer,afterbuffer;
	protected CompressedPixelData compressbefore,compressafter;
	protected Class<?> dataclass;

	public PixelChangeHistory(Canvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * 画素を変更する前にコピーを作成します<br>
	 * この関数は一度しか呼べません。<br>
	 * beforeがPixeldataIntBufferかPixelDataByteBuffer以外のクラスが
	 * 渡されたとき、例外を投げます。
	 * @param before 更新前の画素の状態
	 */
	public synchronized void copyBeforePixel(PixelDataBuffer before){
		if(inited1)return;
		if(!(before instanceof PixelDataByteBuffer || before instanceof PixelDataIntBuffer)){
			throw new RuntimeException("before class is not PixelDataByteBuffer or PixelDataIntBuffer: "+before.getClass());
		}
		inited1=true;
		width =before.getWidth();
		height=before.getHeight();
		dataclass = before.getClass();
		if(canvas!=null)LABEL:{
			int w = canvas.getWidth(),h=canvas.getHeight();
			if(w>=width&&h>=height){
				break LABEL;
			}
			if(before instanceof PixelDataIntBuffer){
				LimitedResource<PixelDataIntBuffer> r = canvas.getPixelDataIntBufferResource();
				Resource<PixelDataIntBuffer> res = r.tryGetResource();
				if(res==null){
					break LABEL;
				}
				PixelDataIntBuffer bf = res.getResource();
				bf.draw((PixelDataIntBuffer)before);
				copy=bf;
				return;
			}else if(before instanceof PixelDataByteBuffer){
				LimitedResource<PixelDataByteBuffer> r = canvas.getPixelDataByteBuffereResource();
				Resource<PixelDataByteBuffer> res = r.tryGetResource();
				if(res==null){
					break LABEL;
				}
				PixelDataByteBuffer bf = res.getResource();
				bf.draw((PixelDataByteBuffer)before);
				copy=bf;
				return;
			}
		}
		copy = before.clone();
	}

	/**
	 * 更新後の画素の状態をセットし、この履歴を使えるようにします。<br>
	 * この関数はcopyBeforePixelが完了していない場合と、
	 * copyBeforePixelで渡されたPixelDataBufferのクラスとafterが一致しない場合例外を投げます。
	 * @param after 更新後の画素の状態
	 * @param r 変化した領域
	 * @throws RuntimeException
	 */
	public synchronized void setAfterPixel(PixelDataBuffer after,Rectangle r)throws RuntimeException{
		if(!inited1){
			throw new RuntimeException("copyBeforePixel method does not finish!");
		}
		if(!dataclass.equals(after.getClass())){
			throw new RuntimeException("classes are not same!"+dataclass+"  after:"+after.getClass());
		}
		if(inited)return;
		if(r==null){
			r = after.getBounds();
		}
		r = after.getBounds().intersection(r);
		bounds = r;
		if(copy instanceof PixelDataIntBuffer){
			beforebuffer = new PixelDataIntBuffer(r.width, r.height, ((PixelDataIntBuffer)copy).copy(null, r));
		}else if(copy instanceof PixelDataByteBuffer){
			beforebuffer = new PixelDataByteBuffer(r.width, r.height, ((PixelDataByteBuffer)copy).copy((byte[])null, r));
		}
		if(resource!=null){
			resource.unlock();
			resource=null;
		}else{
			copy.dispose();
		}
		if(copy instanceof PixelDataIntBuffer){
			afterbuffer = new PixelDataIntBuffer(r.width, r.height, ((PixelDataIntBuffer)after).copy(null, r));
		}else if(copy instanceof PixelDataByteBuffer){
			afterbuffer = new PixelDataByteBuffer(r.width, r.height, ((PixelDataByteBuffer)after).copy((byte[])null, r));
		}
		copy=null;
		inited = true;
	}

	@Override
	public synchronized void compress(){
		if(compressed)return;
		compressafter = CompressedPixelData.compress(afterbuffer);
		compressbefore = CompressedPixelData.compress(beforebuffer);
		beforebuffer.dispose();
		afterbuffer.dispose();
		beforebuffer=afterbuffer=null;
		compressed=true;
	}

	@Override
	public boolean isCompressed() {
		return compressed;
	}

	protected void rendering(){
		if(!canvas.isGPUCanvas()){
			Rectangle[] rs = Util.partition(bounds, CPUParallelWorkThread.getThreadSize());
			canvas.rendering(rs);
		}else{
			canvas.rendering(bounds);
		}
	}

	/**
	 * 渡されたpとafterbufferのクラスが一致する場合、書き込みます。
	 * @param p
	 */
	protected void drawAfterData(PixelDataBuffer p){
		if(!isCorrect()||!dataclass.equals(p.getClass()))return;
		if(compressed){
			PixelDataBuffer k = compressafter.inflate();
			if(k instanceof PixelDataIntBuffer){
				((PixelDataIntBuffer)p).setData(((PixelDataIntBuffer)k).getData(), bounds);
			}else if(k instanceof PixelDataByteBuffer){
				((PixelDataByteBuffer)p).setData(((PixelDataByteBuffer)k).getData(), bounds);
			}
		}else{
			if(p instanceof PixelDataIntBuffer){
				((PixelDataIntBuffer)p).
				setData(((PixelDataIntBuffer)afterbuffer).getData(),
						bounds);
			}else if(p instanceof PixelDataByteBuffer){
				((PixelDataByteBuffer)p).setData(((PixelDataByteBuffer)afterbuffer).getData(), bounds);
			}
		}
	}

	protected void drawBeforeData(PixelDataBuffer p){
		if(!isCorrect()||!dataclass.equals(p.getClass()))return;
		if(compressed){
			PixelDataBuffer k = compressbefore.inflate();
			if(k instanceof PixelDataIntBuffer){
				((PixelDataIntBuffer)p).setData(((PixelDataIntBuffer)k).getData(), bounds);
			}else if(k instanceof PixelDataByteBuffer){
				((PixelDataByteBuffer)p).setData(((PixelDataByteBuffer)k).getData(), bounds);
			}
		}else{
			if(p instanceof PixelDataIntBuffer){
				((PixelDataIntBuffer)p).setData(((PixelDataIntBuffer)beforebuffer).getData(), bounds);
			}else if(p instanceof PixelDataByteBuffer){
				((PixelDataByteBuffer)p).setData(((PixelDataByteBuffer)beforebuffer).getData(), bounds);
			}
		}
	}

	@Override
	protected boolean isCorrect() {
		return inited;
	}

	@Override
	protected long memorySize() {
		if(compressed){
			long m = compressafter.dataSize()+compressbefore.dataSize();
			return m;
		}
		if(dataclass.equals(PixelDataIntBuffer.class))
			return bounds.width*bounds.height*4*2;
		else
			return bounds.width*bounds.height*2;
	}

	@Override
	protected void clear() {
		if(compressed){
			compressafter.flush();
			compressbefore.flush();
		}
		compressafter=compressbefore=null;
		afterbuffer=beforebuffer=copy=null;
		if(resource!=null){
			resource.unlock();
		}
		inited=false;
	}


}
