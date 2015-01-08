package com.lyc.util;

import java.util.LinkedHashMap;

/**
 * ����һ������LinkedHashMap�Ļ����࣬���ܵ�ʵ�ֻ����ǻ���LinkHashMap��ԭ�й���
 * @author lyc
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<K,V> cache=null;
	private int cacheSize=0;
	
	public LRUCache(int cacheSize){
		this.cacheSize=cacheSize;
		int hashTableCapacity = (int) Math.ceil (cacheSize / 0.75f) + 1;
		this.cache=new LinkedHashMap<K, V>(hashTableCapacity, 0.75f, true){
			private static final long serialVersionUID = 1;
			
			/**
			 * ����true����ʾ����Ԫ��ʱ���Ƴ�ʹ��Ƶ����͵�Ԫ��
			 */
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
				System.out.println("size="+size());
				return size()>LRUCache.this.cacheSize;
			}
		};
	}
	
	public V put(K key,V value){
		return cache.put(key,value);
	}
	
	public V get(Object key){
		return cache.get(key);
	}
}
