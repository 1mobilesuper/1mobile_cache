package me.onemobile.cache;

import java.io.File;
import java.util.LinkedHashMap;


public class CacheLRUHashMap extends LinkedHashMap<String, Long> {

	private static final long serialVersionUID = -9025334126811426780L;
	
	private int maxSize = 100;
	
	private CacheManagement cacheManagement;
	
	public CacheLRUHashMap (CacheManagement cacheManagement, int max) {
		super(70, 0.75F, true);
		this.maxSize = max;
		this.cacheManagement = cacheManagement;
	}
	
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, Long> eldest) {
		if (eldest != null && eldest.getValue() != null) {
			if (size() > maxSize) {
				try {
					File file = new File(cacheManagement.getCacheFileDir(), eldest.getKey());
					if (file.exists()) {
						file.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}
	
}
