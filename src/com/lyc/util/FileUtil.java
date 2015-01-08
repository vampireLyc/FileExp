package com.lyc.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileUtil {
	//定义一些常量
	public static final int SHOWMENU1=1;//显示“复制、剪切、删除，更多”菜单
	public static final int SHOWMENU2=2;//
	public static final int HIDEMENU=4;//隐藏所有菜单
	/**
	 * 排列文件和文件夹列表
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
	 * 将原图片缩小为指定大小
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
	 * 判断文件的扩展名
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
	 * 得到一个文件的路径，比如/mnt/sdcard/Downloads/file1，得到的路径是/mnt/sdcard/Downloads
	 * @param file
	 * @return
	 */
	public static String getPathInfo(File file){
		int index=file.getPath().lastIndexOf("/");
		return file.getPath().substring(0, index);
	}
}
