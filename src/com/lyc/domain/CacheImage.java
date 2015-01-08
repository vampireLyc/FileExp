package com.lyc.domain;

import android.graphics.Bitmap;

public class CacheImage {
	private String path;
	private Bitmap bitmap;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
