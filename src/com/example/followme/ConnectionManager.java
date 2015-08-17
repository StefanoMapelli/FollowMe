package com.example.followme;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * Class that concern in all connection management aspects.
 * @author maru
 *
 */
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
}
