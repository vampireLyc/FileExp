package com.lyc.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

public class MyFileFilter implements FileFilter{
	
	/**
	 * ѡ���ļ������ļ��в����ԡ�.����ͷ
	 */
	public boolean accept(File pathname) {
		if(!pathname.getName().startsWith(".")){
			return true;
		}else{
			return false;
		}
	}

}
