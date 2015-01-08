package com.lyc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * ʹ�ò���ANT��ant.jar����������ļ�ZIP��ʽѹ�������Խ��������������
 * @author lyc
 *
 */
public class ZipFileUtil {

	/**
	 * ѹ���ļ�
	 * @param zipFile ѹ������ļ�
	 * @param files ��ѹ�����ļ�
	 * @exception ��ʱû�д����쳣
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
			// �����ļ�·������һ���ļ������
			out = new FileOutputStream(zipFile);
			// �����ļ����������,����ZIP�������������
			zipOut = new ZipOutputStream(out);
			// ѭ����ѹ�����ļ��б�
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (!f.exists()) {
					flag = false;
				}
				// �����ļ�����������
				FileInputStream in = new FileInputStream(f);
				// �õ���ǰ�ļ����ļ�����
				String fileName = f.getName();
				// ����ָ��ѹ��ԭʼ�ļ������
				ZipEntry entry = new ZipEntry(fileName);
				zipOut.putNextEntry(entry);
				// ��ѹ���ļ����������
				int nNumber = 0;
				byte[] buffer = new byte[512];
				while ((nNumber = in.read(buffer)) != -1) {
					zipOut.write(buffer, 0, nNumber);
				}
				// �رմ�����������
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