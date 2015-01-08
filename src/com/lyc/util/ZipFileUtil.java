package com.lyc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 使用采用ANT中ant.jar包的类进行文件ZIP格式压缩，可以解决中文乱码问题
 * @author lyc
 *
 */
public class ZipFileUtil {

	/**
	 * 压缩文件
	 * @param zipFile 压缩后的文件
	 * @param files 待压缩的文件
	 * @exception 暂时没有处理异常
	 * @return
	 */
	public static boolean zipFile(File zipFile, File... files){
		boolean flag = true;

		if (!zipFile.exists()) {
			try {
				zipFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				flag=false;
			}
		}
		
		FileOutputStream out = null;
		ZipOutputStream zipOut = null;
		try {
			// 根据文件路径构造一个文件输出流
			out = new FileOutputStream(zipFile);
			// 传入文件输出流对象,创建ZIP数据输出流对象
			zipOut = new ZipOutputStream(out);
			// 循环待压缩的文件列表
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (!f.exists()) {
					flag = false;
				}
				// 创建文件输入流对象
				FileInputStream in = new FileInputStream(f);
				// 得到当前文件的文件名称
				String fileName = f.getName();
				// 创建指向压缩原始文件的入口
				ZipEntry entry = new ZipEntry(fileName);
				zipOut.putNextEntry(entry);
				// 向压缩文件中输出数据
				int nNumber = 0;
				byte[] buffer = new byte[512];
				while ((nNumber = in.read(buffer)) != -1) {
					zipOut.write(buffer, 0, nNumber);
				}
				// 关闭创建的流对象
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag=false;
		} finally {
			try {
				if(zipOut!=null){
					zipOut.close();
				}
				if(out!=null){
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

}