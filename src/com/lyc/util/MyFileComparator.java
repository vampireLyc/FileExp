package com.lyc.util;

import java.io.File;
import java.util.Comparator;

public class MyFileComparator implements Comparator<File> {

	public int compare(File f1, File f2) {
		if(f1.isDirectory() && f2.isFile()){//如果f1是文件夹，f2是文件
			return -1;
		}else if(f1.isFile() && f2.isDirectory()){//如果f1是文件，f2是文件夹
			return 1;
		}else{//如果f1和f2的类型相同
			return f1.getName().compareToIgnoreCase(f2.getName().toLowerCase());
		}
	}

}
