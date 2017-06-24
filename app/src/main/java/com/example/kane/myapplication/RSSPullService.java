package com.example.kane.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Veronika on 24.06.2017.
 */

public class RSSPullService extends IntentService {

    public final class Constants {
        public static final String BROADCAST_ACTION = "com.example.android.threadsample.BROADCAST";
        public static final String EXTENDED_DATA_STATUS = "com.example.android.threadsample.STATUS";
    }

    public RSSPullService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String dataString = intent.getDataString();

        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }
}
