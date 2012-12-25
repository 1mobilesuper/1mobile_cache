package me.onemobile.cache;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.os.Environment;

public abstract class CacheManagement implements Cache {

	private static final int MAX_SIZE_DEFAULT = 100;

	private Map<String, Long> cacheMap;

	public abstract File getCacheFileDir();
	
	/**
	 * -1: Unknown
	 * 0:  Error.
	 * 1:  Connected.
	 */
	public static int networkState = -1;

	public CacheManagement(int maxSize) {
		cacheMap = Collections.synchronizedMap(new CacheLRUHashMap(this, maxSize <= 0 ? MAX_SIZE_DEFAULT : maxSize));
	}

	@Override
	public void cleanup() {
		Set<String> fileNames = cacheMap.keySet();
		if (fileNames != null) {
			synchronized (cacheMap) {
				Iterator<String> itert = fileNames.iterator();
				for (; itert.hasNext();) {
					String fileName = itert.next();
					try {
						File file = new File(getCacheFileDir(), fileName);
						if (file.exists()) {
							file.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				cacheMap.clear();
			}
		}
	}

	@Override
	public void clearTimeoutCache() {
		Set<String> fileNames = cacheMap.keySet();
		if (fileNames != null) {
			synchronized (cacheMap) {
				Iterator<String> itert = fileNames.iterator();
				for (; itert.hasNext();) {
					String fileName = itert.next();
					try {
						File file = new File(getCacheFileDir(), fileName);
						if (file.exists() && isTimeout(file.lastModified(), getTimeout())) {
							file.delete();
							itert.remove();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void delete(String paramString) {
		if (containsCache(paramString)) {
			File f = getCacheFile(paramString);
			try {
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			cacheMap.remove(paramString);
		}
	}

	@Override
	public boolean containsCache(String paramString) {
		boolean contains = cacheMap.containsKey(paramString);
		File file = new File(getCacheFileDir(), paramString);
		return (contains && file.exists());
	}

	@Override
	public File getCacheFile(String paramString) {
		return new File(getCacheFileDir(), paramString);
	}

	@Override
	public File newCacheFile(String paramString) {
		return new File(getCacheFileDir(), paramString);
	}

	@Override
	public File getCacheFileNotTimeout(String paramString) {
		return getCacheFileNotTimeout(paramString, getTimeout());
	}

	/**
	 * If tiemout < 0, never out off.
	 */

	@Override
	public File getCacheFileNotTimeout(String paramString, long timeout) {
		File f = getCacheFile(paramString);
		if (f.exists()) {
			if (isTimeout(f.lastModified(), timeout)) {
				try {
					f.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
				cacheMap.remove(paramString);
			} else {
				cacheMap.put(paramString, System.currentTimeMillis());
			}
		}
		return f;
	}

	@Override
	public void putCache(File file) {
		if (file.exists()) {
			cacheMap.put(file.getName(), file.lastModified());
		}
	}

	public void scanCacheFiles() {
		if (getCacheFileDir().exists()) {
			File[] files = getCacheFileDir().listFiles();
			if (files != null && files.length > 0) {
				// Arrays.sort(files, new FilesComparator());
				for (int i = 0; i < files.length; i++) {
					putCache(files[i]);
				}
			}
		}
	}

	public abstract long getTimeout();

	/**
	 * If Network is not connected, never timeout. Or judge by timeout.
	 * @param time
	 * @param timeout
	 * @return
	 */
	public static boolean isTimeout(long time, long timeout) {
		if (networkState <= 0) {
			return false;
		}
		if (timeout <= 0) {
			return false;
		}
		return (System.currentTimeMillis() - time > timeout) ? true : false;
	}

	public static boolean isSDCardAvailable() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private class FilesComparator implements Comparator<File> {
		@Override
		public int compare(File arg0, File arg1) {
			return Long.valueOf(arg0.lastModified()).compareTo(Long.valueOf(arg1.lastModified()));
		}
	}
}
