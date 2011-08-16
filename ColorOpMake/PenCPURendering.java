class PenCPU{{OpClassName}}Rendering extends PenCPURendering{
	@Override
	final protected void renderint(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);
				int oc = pixel(overp,x-px,y-py,overw);
				int oa = layeralph(oc,overalph);


				if(a !=0){
					set(basep,{{opename}}Op(a, r, g, b, oa, or, og, ob),x,y,basew);
					continue;
				}else{
					set(basep,oa,color,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer dmask){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] dmaskp = dmask.getData();
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;


		for(int x,y = clip.y;y<endy;y++){
			for(x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew)&0xff;
				if(dmaskv==0)continue;
				int rdmaskv = 255-dmaskv;
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);
				int oc = pixel(overp,x-px,y-py,overw);
				int oa = layeralph(oc,overalph);


				if(a !=0){
					int argb = {{opename}}Op(a, r, g, b, oa, or, og, ob);
					set(basep,argb(
							(a(argb)*dmaskv+a*rdmaskv)*div255shift24>>>24,
							(r(argb)*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(g(argb)*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(b(argb)*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}else{
					set(basep,argb(
							oa*dmaskv*div255shift24>>>24,
							(or*dmaskv+r*rdmaskv)*div255shift24>>>24,
							(og*dmaskv+g*rdmaskv)*div255shift24>>>24,
							(ob*dmaskv+b*rdmaskv)*div255shift24>>>24),x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;


		for(int y = clip.y;y<endy;y++){
			for(int x = clip.x;x<endx;x++){
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				if(a !=0){
					set(basep,{{opename}}Op(a, r(c), g(c), b(c), layeralph(pixel(overp,x-px,y-py,overw),overalph), or, og, ob)&0xffffff | a<<24,x,y,basew);
					continue;
				}
			}
		}
	}

	@Override
	final protected void renderint_alphfix_dmask(PixelDataIntBuffer base,PixelDataByteBuffer over,Point p,Rectangle clip,RenderingOption option,
			PixelDataByteBuffer mask){
		int[] basep = base.getData();
		int basew = base.width;
		byte[] dmaskp = mask.getData();
		byte[] overp = over.getData();
		int overw = over.width;
		int color = option.frontColor.getARGB();
		int or=r(color);
		int og=g(color);
		int ob=b(color);
		int overalph = option.overlayeralph;
		int endy = clip.height+clip.y,endx=clip.width+clip.x;
		int px=p.x,py=p.y;


		for(int y = clip.y;y<endy;y++){
			for(int x = clip.x;x<endx;x++){
				int dmaskv = pixel(dmaskp,x,y,basew);
				if(dmaskv==0)continue;
				int rdmaskv=255-dmaskv;
				int c = pixel(basep,x,y,basew);
				int a = a(c);
				int r = r(c);
				int g = g(c);
				int b = b(c);
				int oc =pixel(overp,x-px,y-py,overw);
				int oa = layeralph(a(oc),overalph);

				if(a !=0){
					int argb = {{opename}}Op(a, r, g, b, oa, or, og, ob);

					set(basep,
							argb(a,
									(r(argb)*dmaskv+r*rdmaskv)/255,
									(g(argb)*dmaskv+g*rdmaskv)/255,
									(b(argb)*dmaskv+b*rdmaskv)/255),
									x,y,basew);
					continue;
				}
			}
		}
	}
}