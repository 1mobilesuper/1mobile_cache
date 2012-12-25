package me.onemobile.cache;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Add configurations below to the target AndroidManifest.xml</p>
 * {@code
 * 
 * uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	
	<receiver android:name="me.onemobile.cache.NetworkStateReceiver"
	          android:label="NetworkConnection">
	  <intent-filter>
	    <action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
	  </intent-filter>
	</receiver>	
 * 
 * }
 * 
 */
public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		setNetworkState(context);
	}

	public static boolean checkNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	public static void setNetworkState(Context context) {
		if (checkNetwork(context)) {
			CacheManagement.networkState = 1;
		} else {
			CacheManagement.networkState = 0;
		}
	}

}