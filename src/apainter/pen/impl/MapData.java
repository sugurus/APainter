package apainter.pen.impl;

import apainter.data.PixelData;

class MapData{
	PixelData data;
	int centerx,centery;
	public MapData(PixelData d,int x,int y) {
		data = d;
		centerx=x;
		centery=y;
	}
}