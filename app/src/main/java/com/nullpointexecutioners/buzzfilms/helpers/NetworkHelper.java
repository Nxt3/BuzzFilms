package com.nullpointexecutioners.buzzfilms.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkHelper {

    /**
     * Helper method for determining if the device has a network connection
     * This does NOT check whether we can actually access the Internet, though
     * @return true or false depending on whether or not the device has a network connection
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    /**
     * Helper method for determining if the device has a network connection
     * Pings www.google.com to check for network connectivity
     * @return true or false depending on whether or not the device has a network connection
     */
    public static boolean isInternetAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1    www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            if(reachable) {
                return reachable;
            }
            else {
                Log.e("isInternetAvailable()", "No Internet access");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
