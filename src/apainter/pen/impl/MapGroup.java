package apainter.pen.impl;

abstract class MapGroup {

	final int size;
	MapData[] datas;
	final int lengthx,lengthy;
	Object[] lock;
	public MapGroup(int size,int lengthx,int lengthy) {
		this.size = size;
		datas = new MapData[lengthx*lengthy];
		lock = new Object[lengthx*lengthy];
		for(int i=0;i<lengthx*lengthy;i++){
			lock[i]=new Object();
		}
		this.lengthx =lengthy;
		this.lengthy =lengthy;
	}
	@Override
	public int hashCode() {
		return size;
	}
	
	public void release(){
		for(int i=0;i<datas.length;i++){
			datas[i]=null;
		}
	}
	
	public final int lengthX(){return lengthx;}
	public final int lengthY(){return lengthy;}
	public final int size(){return size;}
	public final int arrayPosition(int x,int y){
		return x+y*lengthx;
	}
	
	abstract public MapData createData(float cx,float cy);
	
	public MapData getData(float cx,float cy){
		float tx = 1f/lengthx,ty=1f/lengthy;
		int cenx=(int)(cx/tx);
		int ceny=(int)(cy/ty);
		MapData md = datas[arrayPosition(cenx,ceny)];
		if(md==null) synchronized (lock[arrayPosition(cenx,ceny)]){
			if(datas[arrayPosition(cenx,ceny)]==null){
				md = createData(cx,cy);
			}
			datas[arrayPosition(cenx,ceny)]=md;
		}
		return md;
	}
}
