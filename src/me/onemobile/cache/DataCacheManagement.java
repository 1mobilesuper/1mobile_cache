package me.onemobile.cache;

import java.io.File;

import me.onemobile.client.image.DiskLruCache;
import android.content.Context;

public class DataCacheManagement extends CacheManagement {

	public static final int MAX_SIZE = 300;
	public static final long TIMEOUT = 3600000 * 6;

	public static File cacheDataDir;
	public static String versionCode;
	public static String locale;

	private static DataCacheManagement cacheManagement;

	/**
	 * Initial the cache file path
	 * 
	 * @param ctx
	 *            Context
	 * @param pathName
	 *            A unique directory name
	 * @param versionCode
	 *            Client's version code. Tell cache file name from different version.
	 * @param locale
	 *            Locale(en-US). Tell cache file name from different locale.
	 */
	public static void setup(Context ctx, String pathName, String versionCode, String locale) {
		DataCacheManagement.cacheDataDir = DiskLruCache.getDiskCacheDir(ctx, pathName);
		if (DataCacheManagement.cacheDataDir != null && !DataCacheManagement.cacheDataDir.exists()) {
			DataCacheManagement.cacheDataDir.mkdirs();
		}
		DataCacheManagement.versionCode = versionCode;
		DataCacheManagement.locale = locale;
		NetworkStateReceiver.setNetworkState(ctx);
	}

	private DataCacheManagement(int maxSize) {
		super(maxSize);
	}

	public static DataCacheManagement getInstance() {
		if (cacheManagement == null) {
			cacheManagement = new DataCacheManagement(MAX_SIZE);
		}
		return cacheManagement;
	}

	@Override
	public File getCacheFileDir() {
		return cacheDataDir == null ? new File("") : cacheDataDir;
	}

	@Override
	public long getTimeout() {
		return TIMEOUT;
	}

}
