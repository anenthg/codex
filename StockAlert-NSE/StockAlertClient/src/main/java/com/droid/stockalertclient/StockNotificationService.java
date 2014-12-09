package com.droid.stockalertclient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import helper.CommonFuncs;
import helper.StockQuote;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

/**
 * Created by ashwin on 26/9/14.
 */
public class StockNotificationService extends IntentService {

    private static final String TAG=StockNotificationService.class.getSimpleName();
    private final String NSEURL="http://www.nseindia.com/live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=";

    public StockNotificationService()
    {
        super("StockNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"");
        SharedPreferences prefs = this.getSharedPreferences("stock_notification_list.xml", Context.MODE_PRIVATE);
        Map<String,?> entries=prefs.getAll();
        if(entries.isEmpty())
            return;
        /* If the list is not empty, go through all the stocks there in the list
          In the list key is the StockCode and value is the Notification ID
              Fire requests for each of the stock in the pinnes stock list*/
        for(Map.Entry<String,?> entry:entries.entrySet())
        {
            try{
                String stockCode=entry.getKey();
                int notificationID=stockCode.hashCode();
                //fetch details from NSE site
                Connection connection= Jsoup.connect(NSEURL + stockCode);
                connection.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n");
                connection.header("User-Agent","Mozilla/5.0 (X11; Linux i686 (x86_64)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36");
                Document doc = connection.get();
                Element livePrice=doc.getElementById("responseDiv");
                JSONObject stockDetails=new JSONObject(livePrice.text());
                String lastPrice=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("lastPrice");
                String yearHigh=(String)stockDetails.getJSONArray("data").getJSONObject(0).get("high52");
                String dayHigh=(String)stockDetails.getJSONArray("data").getJSONObject(0).get("dayHigh");
                String yearLow=(String)stockDetails.getJSONArray("data").getJSONObject(0).get("low52");
                String dayLow=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("dayLow");
                String openPrice=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("open");
                String previousClose=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("open");

                Intent intent1 = new Intent(this, GetStockQuote.class);
                intent1.putExtra("stock",new CommonFuncs().getStock(stockCode,this));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pIntent = PendingIntent.getActivity(this, notificationID, intent1, 0);
                    /* Update the Notification with the ID same as this stock*/
                      /*Build the notification*/
                /*Notification notification=new Notification.Builder(this)*/
                /*USING NOTIFICATIONCOMPAT AND NOTIFICATIONMANAGERCOMPAT to make the notifications
                WEAR compatible
                 */
                NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this)
                        .setContentTitle(stockCode)
                        .setContentText(lastPrice)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);

            /*Get instance of Notification Manager and show the notification*/
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(notificationID,notificationBuilder.build());


            }
            catch(IOException ex1)
            {
                Log.e(TAG,ex1.toString());
            }
            catch(JSONException ex2)
            {
                Log.e(TAG,ex2.toString());
            }
        }


    }
}
