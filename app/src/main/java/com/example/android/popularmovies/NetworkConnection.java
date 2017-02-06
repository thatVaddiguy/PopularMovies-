package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by BOX on 9/11/2016.
 */
public class NetworkConnection {

    public static Boolean checkNetwork(Context context){

        Boolean returnValue = false;
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()){

            returnValue = true;
        }

        return returnValue;
    }

}
