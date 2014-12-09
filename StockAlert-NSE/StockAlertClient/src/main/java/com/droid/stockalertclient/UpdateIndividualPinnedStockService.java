package com.droid.stockalertclient;

/**
 * Created by ashwin on 26/10/14.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

import helper.CommonFuncs;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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
import helper.Stock;
import helper.StockQuote;

/**
 * Created by ashwin on 26/9/14.
 */
public class UpdateIndividualPinnedStockService extends IntentService {

    private static final String TAG=UpdateIndividualPinnedStockService.class.getSimpleName();
    private final String pinnedStocks="pinnedStocks";
    private final String NSEURL="http://www.nseindia.com/live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=";

    public UpdateIndividualPinnedStockService()
    {
        super("UpdateIndividualPinnedStockService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "");

       StockQuote stockQuote=(StockQuote)intent.getExtras().get("stockQuote");
        String stockCode=stockQuote.getStockCode();
        Integer notificationID=intent.getExtras().getInt("notificationID");

        /*Update the text of this notificaiton as "Refreshing..."
        So that the user knows that it is being refreshed
         */
        NotificationCompat.Builder refreshBuilder=new NotificationCompat.Builder(this)
                .setContentTitle("Refreshing....")
                .setContentText("Please Wait..")
                .setSmallIcon(R.drawable.ic_launcher);
        NotificationManager refreshNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        refreshNotificationManager.notify(notificationID,refreshBuilder.build());
            try{

                //fetch details from NSE site
                Connection connection= Jsoup.connect(NSEURL + stockCode);
                connection.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n");
                connection.header("User-Agent","Mozilla/5.0 (X11; Linux i686 (x86_64)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36");
                Document doc = connection.get();
                Element livePrice=doc.getElementById("responseDiv");
                JSONObject stockDetails = new JSONObject(livePrice.text());

                String lastPrice = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("lastPrice");
                String yearHigh = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("high52");
                String dayHigh = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("dayHigh");
                String yearLow = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("low52");
                String dayLow = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("dayLow");
                String openPrice = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("open");
                String previousClose = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("open");
                String change = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("change");
                String sharesTraded = (String) stockDetails.getJSONArray("data").getJSONObject(0).get("quantityTraded");
                String lastUpdatedTime = (String) stockDetails.get("lastUpdateTime");

                stockQuote=new StockQuote(stockCode,lastPrice,dayHigh,dayLow,yearHigh,yearLow,openPrice,previousClose,change,sharesTraded,lastUpdatedTime);
                NotificationCompat.Builder notificationBuilder=extractStockInfoAndBuildNotification(stockQuote,notificationID);

            /*Get instance of Notification Manager and show the notification*/
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(notificationID,notificationBuilder.build());


            }
            catch(IOException ex1)
            {
                Log.e(TAG,ex1.toString());
                /*Display the old stock values along with a toast describing the error*/

                NotificationCompat.Builder notificationBuilder=extractStockInfoAndBuildNotification(stockQuote,notificationID);
                /*Get instance of Notification Manager and show the notification*/
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(notificationID,notificationBuilder.build());

                /*Toast Describing the error*/
                Toast.makeText(this,"Unable to Refresh. Check your Internet Connection",Toast.LENGTH_SHORT).show();


            }
            catch(JSONException ex2)
            {
                Log.e(TAG,ex2.toString());

            }



    }
    private  NotificationCompat.Builder extractStockInfoAndBuildNotification(StockQuote stockQuote,int notificationID)
    {


            Intent intent1 = new Intent(this, GetStockQuote.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pIntent = PendingIntent.getActivity(this, notificationID, intent1, 0);
                    /* Update the Notification with the ID sama as this stock*/

                      /*Set the Refresh action to call the intent service
                      * Request Code is set as notificationID+1 when creating the notification
                      * Hence using the same here*/
            Intent refreshIntent = new Intent(this, UpdateIndividualPinnedStockService.class);
            PendingIntent refreshPendingIntent = PendingIntent.getService(this, notificationID + 1, refreshIntent, 0);

            String stockCode=stockQuote.getStockCode();
            String lastPrice=stockQuote.getLastPrice();
            String sharesTraded=stockQuote.getSharesTraded();
            String change=stockQuote.getpChange();
            String dayHigh=stockQuote.getDayHigh();
            String dayLow=stockQuote.getDayLow();
            String lastUpdatedTime=stockQuote.getLastUpdatedTime();
                    /*Construct the data to be displayed in the notification content*/
            String notificationContent = new CommonFuncs().constructNotificationContent(lastPrice, change, sharesTraded, dayHigh, dayLow);

                      /*Build the notification*/
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(stockCode)
                    .setContentText(notificationContent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pIntent)
                    .setOngoing(true)
                    .setContentInfo(lastUpdatedTime)
                    .addAction(R.drawable.configure, "Refresh", refreshPendingIntent);

                     /*Big Notification*/
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            if (Float.parseFloat(change) > 0)
                change = "+" + change;
            inboxStyle.addLine("Price : " + lastPrice + " (" + change + ")");
            inboxStyle.addLine("Volumes : " + sharesTraded);
            inboxStyle.addLine("Day High/Low : " + dayHigh + "/" + dayLow);


                    /*Set the BigNotifiaciont as the style*/
            notificationBuilder.setStyle(inboxStyle);

            return notificationBuilder;


    }
}
