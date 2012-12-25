package me.onemobile.cache;


public class FileNameFactory {
	
	public static String getFileNameBinder(String ctype, String... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(ctype.hashCode());
		if (params.length > 0) {
			StringBuilder prms = new StringBuilder();
			if (params != null && params.length > 0) {
				for (String p : params) {
					prms.append(p);
				}
			}
			sb.append("-").append(prms.toString().hashCode());
		}
		sb.append("-").append(DataCacheManagement.versionCode).append("-").append(DataCacheManagement.locale);
		return sb.toString();
	}
}
