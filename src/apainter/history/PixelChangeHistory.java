package apainter.history;

import java.awt.Rectangle;

import apainter.canvas.Canvas;
import apainter.canvas.cedt.cpu.CPUParallelWorkThread;
import apainter.data.CompressedPixelData;
import apainter.data.PixelData15BitColor;
import apainter.data.PixelData;
import apainter.data.PixelDataByte;
import apainter.data.PixelDataInt;
import apainter.misc.Util;
import apainter.resorce.LimitedResource;
import apainter.resorce.Resource;
//TODO 15BitDataの追加
public abstract class PixelChangeHistory extends HistoryObject{

	//準備用
	protected final Canvas canvas;
	private Resource<? extends PixelData> resource,
	resource2;//15bit用
	private PixelData copy;
	private int width,height;
	private boolean inited1=false;


	//基本データ////////////////
	private boolean inited=false;
	protected boolean compressed=false;

	protected Rectangle bounds;
	protected PixelData beforebuffer,afterbuffer;
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
	public synchronized void copyBeforePixel(PixelData before){
		if(inited1)return;
		if(!(before instanceof PixelDataByte ||
				before instanceof PixelDataInt||
				before instanceof PixelData15BitColor)){
			throw new RuntimeException("before class is not PixelDataByteBuffer or PixelDataIntBuffer: "+before.getClass());
		}
		inited1=true;
		width =before.getWidth();
		height=before.getHeight();
		dataclass = before.getClass();
		LABEL:if(canvas!=null){
			int w = canvas.getWidth(),h=canvas.getHeight();
			if(w>width&&h>height){
				break LABEL;
			}
			if(before instanceof PixelDataInt){
				LimitedResource<PixelDataInt> r = canvas.getPixelDataIntBufferResource();
				Resource<PixelDataInt> res = r.tryGetResource();
				if(res==null){
					break LABEL;
				}
				PixelDataInt bf = res.getResource();
				bf.draw((PixelDataInt)before);
				copy=bf;
				resource = res;
				return;
			}else if(before instanceof PixelDataByte){
				LimitedResource<PixelDataByte> r = canvas.getPixelDataByteBuffereResource();
				Resource<PixelDataByte> res = r.tryGetResource();
				if(res==null){
					break LABEL;
				}
				PixelDataByte bf = res.getResource();
				bf.draw((PixelDataByte)before);
				copy=bf;
				resource = res;
				return;
			}else if(before instanceof PixelData15BitColor){
				LimitedResource<PixelDataInt> r = canvas.getPixelDataIntBufferResource();
				Resource<PixelDataInt> res1 = r.tryGetResource();
				if(res1==null){
					break LABEL;
				}
				r = canvas.getPixelDataIntBufferResource();
				Resource<PixelDataInt> res2 = r.tryGetResource();
				if(res2==null){
					res1.unlock();
					break LABEL;
				}
				PixelData15BitColor b = (PixelData15BitColor) before;
				PixelDataInt i1 = res1.getResource(),i2=res2.getResource(),
						b1 = b.getIntegerBuffer(),b2 = b.getDecimalBuffer();
				i1.draw(b1);
				i2.draw(b2);
				copy = new PixelData15BitColor(width, height, i1, i2);
				resource = res1;
				resource2 = res2;
				return;
			}
		}
		//FIXME DEBUG
		System.out.println("Pixel Change History make clone");
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
	public synchronized void setAfterPixel(PixelData after,Rectangle r)throws RuntimeException{
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
		beforebuffer = copy.copy(r);
		afterbuffer = after.copy(r);
		if(resource!=null){
			resource.unlock();
			resource=null;
			if(resource2!=null){
				resource2.unlock();
				resource2=null;
			}
		}else{
			copy.dispose();
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
	protected void drawAfterData(PixelData p){
		if(!isCorrect()||!dataclass.equals(p.getClass()))return;
		if(compressed){
			PixelData k = compressafter.inflate();
			if(k instanceof PixelDataInt){
				((PixelDataInt)p).setData(((PixelDataInt)k).getData(), bounds);
			}else if(k instanceof PixelDataByte){
				((PixelDataByte)p).setData(((PixelDataByte)k).getData(), bounds);
			}else if(k instanceof PixelData15BitColor){
				((PixelData15BitColor)p).setData((PixelData15BitColor)k, bounds);
			}
		}else{
			if(p instanceof PixelDataInt){
				((PixelDataInt)p).
				setData(((PixelDataInt)afterbuffer).getData(),
						bounds);
			}else if(p instanceof PixelDataByte){
				((PixelDataByte)p).setData(((PixelDataByte)afterbuffer).getData(), bounds);
			}else if(p instanceof PixelData15BitColor){
				((PixelData15BitColor)p).setData((PixelData15BitColor)afterbuffer, bounds);
			}
		}
	}

	protected void drawBeforeData(PixelData p){
		if(!isCorrect()||!dataclass.equals(p.getClass()))return;
		if(compressed){
			PixelData k = compressbefore.inflate();
			if(k instanceof PixelDataInt){
				((PixelDataInt)p).setData(((PixelDataInt)k).getData(), bounds);
			}else if(k instanceof PixelDataByte){
				((PixelDataByte)p).setData(((PixelDataByte)k).getData(), bounds);
			}else if(k instanceof PixelData15BitColor){
				((PixelData15BitColor)p).setData((PixelData15BitColor)k, bounds);
			}
		}else{
			if(p instanceof PixelDataInt){
				((PixelDataInt)p).setData(((PixelDataInt)beforebuffer).getData(), bounds);
			}else if(p instanceof PixelDataByte){
				((PixelDataByte)p).setData(((PixelDataByte)beforebuffer).getData(), bounds);
			}else if(p instanceof PixelData15BitColor){
				((PixelData15BitColor)p).setData((PixelData15BitColor)beforebuffer, bounds);
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
		if(dataclass.equals(PixelData15BitColor.class)){
			return  bounds.width*bounds.height*8*2;
		}else if(dataclass.equals(PixelDataInt.class))
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
			if(resource2!=null){
				resource2.unlock();
			}
			resource=resource2=null;
		}
		inited=false;
	}


}
