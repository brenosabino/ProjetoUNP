package com.breno.projeto;

import android.content.Context;
import android.net.ConnectivityManager;

public class ChecaInternet {

    /**
     * Checks if there's internet connection on the phone
     * @param context To initiate {@link ConnectivityManager}
     * @return True if network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnected();
    }
}
