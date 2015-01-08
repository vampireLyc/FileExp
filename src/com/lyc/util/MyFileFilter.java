package com.lyc.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

public class MyFileFilter implements FileFilter{
	
	/**
	 * 选择文件或者文件夹不是以“.”开头
	 */
	public boolean accept(File pathname) {
		if(!pathname.getName().startsWith(".")){
			return true;
		}else{
			return false;
		}
	}

}
