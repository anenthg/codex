package com.droid.stockalertclient;

import android.app.IntentService;
import android.app.Notification;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import helper.CommonFuncs;
import helper.StockQuote;

/**
 * Created by ashwin on 26/9/14.
 */
public class UpdatePinnedStocksService extends IntentService {

    private static final String TAG=UpdatePinnedStocksService.class.getSimpleName();
    private final String pinnedStocks="pinnedStocks";
    private final String NSEURL="http://www.nseindia.com/live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=";

    public UpdatePinnedStocksService()
    {
        super("UpdatePinnedStocksService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"");
        SharedPreferences prefs = this.getSharedPreferences("pinned_stocklist.xml", Context.MODE_PRIVATE);
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
                    int notificationID=(Integer) entry.getValue();
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
                    String pChange=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("pChange");
                    String sharesTraded=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("quantityTraded");
                    String lastUpdatedTime=(String) stockDetails.get("lastUpdateTime");
                    Intent intent1 = new Intent(this, GetStockQuote.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pIntent = PendingIntent.getActivity(this, notificationID, intent1, 0);
                    /* Update the Notification with the ID sama as this stock*/

                      /*Set the Refresh action to call the intent service
                      * Request Code is set as notificationID+1 when creating the notification
                      * Hence using the same here.*/
                    Intent refreshIntent=new Intent(this,UpdateIndividualPinnedStockService.class);
                    PendingIntent refreshPendingIntent=PendingIntent.getService(this,notificationID+1,refreshIntent,0);

                    /*Construct the data to be displayed in the notification content*/
                    String notificationContent=new CommonFuncs().constructNotificationContent(lastPrice,pChange,sharesTraded,dayHigh,dayLow);

                      /*Build the notification*/
                    NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this)
                            .setContentTitle(stockCode)
                            .setContentText(notificationContent)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(pIntent)
                            .setOngoing(true)
                            .setContentInfo(lastUpdatedTime)
                            .addAction(R.drawable.configure,"Refresh",refreshPendingIntent);

                     /*Big Notification*/
                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    if(Float.parseFloat(pChange)>0)
                        pChange="+"+pChange;
                    inboxStyle.addLine("Price : "+lastPrice+" ("+pChange+")");
                    inboxStyle.addLine("Volumes : "+sharesTraded);
                    inboxStyle.addLine("Day High/Low : "+dayHigh+"/"+dayLow);


                    /*Set the BigNotifiaciont as the style*/
                    notificationBuilder.setStyle(inboxStyle);

            /*Get instance of Notification Manager and show the notification*/
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
