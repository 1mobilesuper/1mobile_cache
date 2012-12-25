package me.onemobile.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataCacheUtil {
	
	private static DataCacheManagement dataCacheManagement = DataCacheManagement.getInstance();
	
	private static ExecutorService executor;
	
	public static void init() {
		dataCacheManagement.scanCacheFiles();
	}
	
	public static void clearAll() {
		dataCacheManagement.cleanup();
	}
	
	public static void clearTimeoutCache() {
		dataCacheManagement.clearTimeoutCache();
	}
	
	
	/**
	 * restore from cache file
	 */
	public static InputStream restore(String fnBinder) {
		return restoreInCustomTimeout(fnBinder, 0);
	}
	
	/**
	 * restore from cache file. Assemble FileNameBinder by args.
	 * @param apiName
	 * @param params
	 * @return
	 */
	public static InputStream restore(String apiName, String... params) {
		return restore(FileNameFactory.getFileNameBinder(apiName, params));
	}
	
	/**
	 * restore from cache file
	 * @param tiemout  If tiemout < 0, never out off.
	 */
	public static InputStream restoreInCustomTimeout(long timeout, String apiName, String... params) {
		return restoreInCustomTimeout(FileNameFactory.getFileNameBinder(apiName, params), timeout);
	}
	
	/**
	 * restore from cache file
	 * @param tiemout  If tiemout < 0, never out off.
	 */
	public static InputStream restoreInCustomTimeout(String fnBinder, long timeout) {
		try {
			File file = timeout == 0 ? dataCacheManagement.getCacheFileNotTimeout(fnBinder) :
				dataCacheManagement.getCacheFileNotTimeout(fnBinder, timeout);
			if (file.exists()) {
				return new FileInputStream(file);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param ctype
	 * @param params
	 * @return return false if timeout.
	 */
	public static boolean isCacheExists(String ctype, String... params) {
		return isCacheExists(0, ctype, params);
	}
	
	/**
	 * @param ctype
	 * @param params
	 * @return return true if cache exits no mater timeout. Will change last modified time.
	 */
	public static boolean isCacheExists_notTimeout(String ctype, String... params) {
		return isCacheExists(-1, ctype, params);
	}
	
	/**
	 * 
	 * @param timeout
	 * @param ctype
	 * @param params
	 * @return return boolean by custom timeout.
	 */
	public static boolean isCacheExists(long timeout, String ctype, String... params) {
		String fnBinder = FileNameFactory.getFileNameBinder(ctype, params);
		File file = timeout == 0 ? dataCacheManagement.getCacheFileNotTimeout(fnBinder) :
			dataCacheManagement.getCacheFileNotTimeout(fnBinder, timeout);
		return file != null && file.exists();
	}
	
	/**
	 * backup data in cache file
	 */
	public static void backup(com.google.protobuf.GeneratedMessage obj, String ctype, String... params) {
		backup(obj, FileNameFactory.getFileNameBinder(ctype, params));
	}
	
	/**
	 * backup data in cache file
	 */
	public static void backup(com.google.protobuf.GeneratedMessage obj, String fnBinder) {
		File file = dataCacheManagement.newCacheFile(fnBinder);
		WriteCacheThread run = new WriteCacheThread(file, obj);
		try {
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
		} catch (Exception e) {
		}
		if (executor == null) {
			executor = Executors.newSingleThreadExecutor();
		}
		executor.execute(run);
	}
	
	
	private static class WriteCacheThread implements Runnable {
		File file;
		com.google.protobuf.GeneratedMessage obj;

		public WriteCacheThread(File file, com.google.protobuf.GeneratedMessage obj) {
			this.file = file;
			this.obj = obj;
		}

		public void run() {
			try {
				obj.writeTo(new FileOutputStream(file));
				dataCacheManagement.putCache(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
