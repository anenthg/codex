package com.droid.stockalertclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by ashwin on 26/9/14.
 */
public class NotificationUpdateReceiver extends BroadcastReceiver {

    private final String TAG=NotificationUpdateReceiver.class.getSimpleName();
    private final String ACTION_NOTIFICATION_UPDATE="stockapp.action.START_NOTIFICATION_UPDATE_ALARM";
    private final String ACTION_BOOT_COMPLETED="android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        /*Configure AlarmManager to start the IntentService - UpdatePinnedStocksService

         */
        Log.i(TAG, intent.getAction());



            /* Check if the SharedPreferences contains any pinned stocks.
               If there are no pinned stocks do not start the alarm manager - just return
             */
            SharedPreferences prefs = context.getSharedPreferences("pinned_stocklist.xml", Context.MODE_PRIVATE);
            Map<String,?> entries=prefs.getAll();
            if(entries.isEmpty())
                return;


        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, UpdatePinnedStocksService.class);
        PendingIntent pIntent = PendingIntent.getService(context,0,serviceIntent, 0);
        long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long firstPoll = System.currentTimeMillis()+10;
        am.cancel(pIntent);//Cancel if an alarm already exits. Then start a new one with first poll as NOW
        am.setInexactRepeating(AlarmManager.RTC, firstPoll, interval, pIntent);
        Log.i(TAG,"Alarm Set");

    }
}
