package com.lyc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.lyc.domain.CacheImage;
import com.lyc.main.R;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

public class AsynIcon {
//	private LRUCache<String,Bitmap> mapIcon;
//	private Map<String,Bitmap> mapIcons;
	private ConcurrentLinkedQueue<CacheImage> cacheImages;
	private Handler handler;
//	private List<ImageView> listIcons;
	
	public AsynIcon(Handler handler){
		this.handler=handler;
		this.cacheImages=new ConcurrentLinkedQueue<CacheImage>();
//		this.listIcons=new ArrayList<ImageView>();
//		mapIcons=new HashMap<String,Bitmap>();
//		this.mapIcon=new LRUCache<String,Bitmap>(12);
	}
	
//	public void getIcon(List<ImageView> listIcon){
//		for(ImageView icon:listIcon){
//			String path=(String) icon.getTag();
//			for(CacheImage img:this.cacheImages){
//				if(path.equals(img.getPath())){
//					Bitmap bitmap=img.getBitmap();
//					icon.setImageBitmap(bitmap);
//					System.out.println("�ӻ�����ȡ��");
//					return;
//				}else{
//					this.listIcons.add(icon);
//				}
//			}
//		}
//		//�¿��̣߳�һ���Ը���������Ҫ���ص�Icon
//		new IconThread(this.listIcons).start();
//	}
	
//	public class IconThread extends Thread{
//		private List<ImageView> list;
//		private ImageView imageView;
//		private Bitmap bitmap;
//		private String path;
//		
//		public IconThread(List<ImageView> list){
//			this.list=list;
//		}
//		
//		@Override
//		public void run(){
//			for(ImageView icon:list){
//				this.imageView=icon;
//				this.path=(String) this.imageView.getTag();
//				bitmap=FileUtil.getShinkBitmap(this.path);
//				System.out.println("bitmap.width="+bitmap.getWidth()+";height="+bitmap.getHeight());
//				
//				CacheImage cacheImage=new CacheImage();
//				cacheImage.setBitmap(bitmap);
//				cacheImage.setPath(this.path);
//				
//				if(cacheImages.size()>=50){//�������������������POLL��������ѭFIFO��
//					cacheImages.poll();
//				}
//				cacheImages.add(cacheImage);
//				
//				handler.post(new Runnable() {
//					public void run() {
//						imageView.setImageBitmap(bitmap);
//					}
//				});
//			}
//		}
//	}
	
	public void getIcons(ImageView imageView){
		String path=(String) imageView.getTag();
		imageView.setImageResource(R.drawable.target);
		
		for(CacheImage img:this.cacheImages){
			if(path.equals(img.getPath())){
				Bitmap bitmap=img.getBitmap();
				imageView.setImageBitmap(bitmap);
				System.out.println("�ӻ�����ȡ��");
				return;
			}
		}
		
		//�ڻ�����û���ҵ������¿��̼߳���ͼ��
		new IconsThread(imageView).start();
	}
	
	public class IconsThread extends Thread{
		private ImageView imageView;
		private Bitmap bitmap;
		private String path;
		public IconsThread(ImageView imageView){
			this.imageView=imageView;
			this.path=(String) this.imageView.getTag();
		}
		
		public void run(){
			bitmap=FileUtil.getShinkBitmap(this.path);
			
			CacheImage cacheImage=new CacheImage();
			cacheImage.setBitmap(bitmap);
			cacheImage.setPath(this.path);
			
			if(cacheImages.size()>=100){//�������������������POLL��������ѭFIFO��
				cacheImages.poll();
			}
			cacheImages.add(cacheImage);
			
			handler.post(new Runnable() {
				public void run() {
					imageView.setImageBitmap(bitmap);
				}
			});
		}
	}
	
//	public void getIcon(ImageView imageView){
//		String path=(String) imageView.getTag();
//		if(mapIcons.containsKey(path)){
//			imageView.setImageBitmap(mapIcons.get(path));
//			return;
//		}
		
//		if(mapIcon.containsKey(path)){
//			imageView.setImageBitmap(mapIcon.get(path));
//			System.out.println("�ӻ�����ȡ��");
//			return;
//		}
		
		//����ڻ�����û���ҵ������������߳�������ICON
//		new IconThread(imageView).start();
//	}
	
//	private class IconThread extends Thread{
//		private String path;
//		private ImageView imageView;
//		private Bitmap bitmap;
//		
//		public IconThread(ImageView imageView){
//			this.imageView=imageView;
//			this.path=(String) this.imageView.getTag();
//		}
//		
//		@Override
//		public void run() {
//			bitmap=FileUtil.getShinkBitmap(this.path);
//			if(mapIcons.size()<=50){//���ƻ���Ĵ�С����ֹ����Ļ���ͼƬ����
//				mapIcons.put(this.path, bitmap);
//			}
//			
////			mapIcon.put(this.path, bitmap);
//			handler.post(new Runnable() {
//				public void run() {
//					imageView.setImageBitmap(bitmap);
//				}
//			});
//		}
//	}
}
