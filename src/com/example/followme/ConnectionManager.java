package com.example.followme;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ConnectionManager {
	
	public static boolean connectionPresent(ConnectivityManager cMgr)
	{
	      if (cMgr != null) {
	         NetworkInfo netInfo = cMgr.getActiveNetworkInfo();
	         if ((netInfo != null) && (netInfo.getState() != null)) {
	            return netInfo.getState().equals(State.CONNECTED);
	         } else {
	            return false;
	         }
	      }
	      return false;
	   }
	
	 public static String getIpAddress(ConnectivityManager connMgr, WifiManager wifiMgr) 
	 {
		 NetworkInfo mWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		if(mWifi.isConnected())
		{
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			   int ip = wifiInfo.getIpAddress();

			   String ipString = String.format(
			   "%d.%d.%d.%d",
			   (ip & 0xff),
			   (ip >> 8 & 0xff),
			   (ip >> 16 & 0xff),
			   (ip >> 24 & 0xff));

			   return ipString;
		}
		else
		{
	      try {
	         for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	               InetAddress inetAddress = enumIpAddr.nextElement();
	               if (!inetAddress.isLoopbackAddress()) {
	                  return inetAddress.getHostAddress().toString();
	               }
	            }
	         }
	      } catch (SocketException e) {
	        // Log.e(Constants.LOG_TAG, e.getMessage(), e);
	      }
	      return null;
	   }
	 }
}
