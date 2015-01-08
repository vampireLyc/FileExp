package com.lyc.domain;

import java.io.File;

public class CheckedFile {
	private File file;
	private int position;
	
	public CheckedFile(){
		
	}
	public CheckedFile(File file,int position){
		this.file=file;
		this.position=position;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
}
