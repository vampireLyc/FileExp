package com.lyc.util;

import java.util.LinkedHashMap;

/**
 * 这是一个基于LinkedHashMap的缓存类，功能的实现基本是基于LinkHashMap的原有功能
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
			 * 返回true，表示插入元素时，移除使用频率最低的元素
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
