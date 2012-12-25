package me.onemobile.cache;

import java.io.File;

public abstract interface Cache {

	/**
	 * Clear all cache files
	 */
	public abstract void cleanup();

	/**
	 * Clear timeout cache
	 */
	public abstract void clearTimeoutCache();

	/**
	 * Delete cache file by name
	 */
	public abstract void delete(String paramString);

	/**
	 * Whether does the cache file exist
	 */
	public abstract boolean containsCache(String paramString);

	/**
	 * Get cache file
	 */
	public abstract File getCacheFile(String paramString);

	/**
	 * New file
	 */
	public abstract File newCacheFile(String paramString);

	/**
	 * Get cache file if exists & not timeout . If timeout, this cache file will
	 * be deleted.
	 */
	public abstract File getCacheFileNotTimeout(String paramString);

	/**
	 * Get cache file if exists & not timeout . If timeout, this cache file will
	 * be deleted.
	 */
	public abstract File getCacheFileNotTimeout(String paramString, long timeout);

	/**
	 * Not writing cache file, but putting into map. And if out of capability ,
	 * the eldest will be remove and the corresponding file will be deleted.
	 */
	public abstract void putCache(File file);

}
