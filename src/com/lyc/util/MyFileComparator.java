package com.lyc.util;

import java.io.File;
import java.util.Comparator;

public class MyFileComparator implements Comparator<File> {

	public int compare(File f1, File f2) {
		if(f1.isDirectory() && f2.isFile()){//���f1���ļ��У�f2���ļ�
			return -1;
		}else if(f1.isFile() && f2.isDirectory()){//���f1���ļ���f2���ļ���
			return 1;
		}else{//���f1��f2��������ͬ
			return f1.getName().compareToIgnoreCase(f2.getName().toLowerCase());
		}
	}

}
