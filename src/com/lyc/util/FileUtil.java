package com.lyc.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileUtil {
	//����һЩ����
	public static final int SHOWMENU1=1;//��ʾ�����ơ����С�ɾ�������ࡱ�˵�
	public static final int SHOWMENU2=2;//
	public static final int HIDEMENU=4;//�������в˵�
	/**
	 * �����ļ����ļ����б�
	 * @param files
	 * @return
	 */
	public static File[] sortFiles(File[] files){
		List<File> list=Arrays.asList(files);
		Collections.sort(list, new MyFileComparator());
		File[] array=list.toArray(new File[list.size()]);
		return array;
	}
	/**
	 * ��ԭͼƬ��СΪָ����С
	 * @param path
	 * @return
	 */
	public static Bitmap getShinkBitmap(String path){
//		int flag=30;
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inSampleSize=1;
//		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//		if(bitmap.getWidth()>=bitmap.getHeight()){
//			options.inSampleSize=bitmap.getWidth()/flag;
//		}else{
//			options.inSampleSize=bitmap.getHeight()/flag;
//		}
//		bitmap = BitmapFactory.decodeFile(path, options);
//		return bitmap;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}
	
	/**
	 * �ж��ļ�����չ��
	 * @param fileName
	 * @param fileType
	 * @return
	 */
	public static boolean endWith(String fileName,String...fileType){
		if(fileType.length>0){
			for(String type:fileType){
				if(fileName.endsWith(type)){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	/**
	 * �õ�һ���ļ���·��������/mnt/sdcard/Downloads/file1���õ���·����/mnt/sdcard/Downloads
	 * @param file
	 * @return
	 */
	public static String getPathInfo(File file){
		int index=file.getPath().lastIndexOf("/");
		return file.getPath().substring(0, index);
	}
}
