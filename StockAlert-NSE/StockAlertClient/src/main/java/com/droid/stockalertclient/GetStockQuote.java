package com.droid.stockalertclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Random;

import helper.CommonFuncs;
import helper.Stock;
import helper.StockQuote;
import reusabletasks.GetQuote;

public class GetStockQuote extends ActionBarActivity {

    private final String TAG=GetStockQuote.class.getSimpleName();
    private final String ACTION_NOTIFICATION_UPDATE="stockapp.action.START_NOTIFICATION_UPDATE_ALARM";
    //The stock symbol needs to be appended to this URL
    private final String NSEURL="http://www.nseindia.com/live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=";
    private String stockCode; //This is passed as an IntentExtra to this activity
    private Stock stock;      //This is passed as an IntentExtra to this activity

    /* The layout Fields and the progress bar in which the stock values have to be populated*/
    private TextView currentPrice,dayHighLow,yearHighLow,tradedVolume,lastUpdatedTime,openPrice;
    private ProgressBar progressBar;
    /*menu displayed in the activity*/
    Menu menu;
    /*GetQuote object. To be used when pinning the stocke*/
    GetQuote gGetQuote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);


        /*Get the Stock passed on to this activity as IntentExtra*/
          stock=(Stock) getIntent().getExtras().get("stock");
          stockCode=stock.getStockCode();



        /*Set the Layout and set Stock Symbol as the label of this activity*/
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.stock_quote_activity);
        setTitle(stockCode);

        /*Assign the text views to the global variables*/
        currentPrice=(TextView) findViewById(R.id.currentPriceValue);
        yearHighLow=(TextView) findViewById(R.id.yearHighLowValue);
        dayHighLow=(TextView) findViewById(R.id.dayHighLowValue);
        lastUpdatedTime=(TextView) findViewById(R.id.lastUpdatedTime);
        tradedVolume=(TextView) findViewById(R.id.dayTradedVolumeValue);
        openPrice=(TextView) findViewById(R.id.dayOpenPriceValue);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);

        /*Make the progressBar visible - This is to be done because teh starting of async task executino is getting delayed
         because of the multiple async tasks in the watchlist activity*/
        progressBar.setVisibility(View.VISIBLE);

        /*Run the Async Task*/
        GetQuote getQuote= new GetQuote(currentPrice,dayHighLow,yearHighLow,lastUpdatedTime,tradedVolume,openPrice,progressBar,getApplicationContext());
        getQuote.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,stockCode);
        gGetQuote=getQuote;


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG,"onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock_quote, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG,"onPrepareOptionsMenu");
          /*Show the appropriate icon in the menu bar - if the stock is pinned, show pinit icon
          Else show the unpin icon
         */
        SharedPreferences prefs = this.getSharedPreferences("pinned_stocklist.xml", Context.MODE_PRIVATE);
        if(prefs.contains(stockCode))
        {

            MenuItem unpinItem=menu.findItem(R.id.action_unpinStock);
            unpinItem.setVisible(true);
            MenuItem pinitItem=menu.findItem(R.id.action_pinStock);
            pinitItem.setVisible(false);

        }
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.action_pinStock)
        {
            /*Check if the call to NSE has worked. Otherwise no point in Pinning the stock*/
            if(currentPrice.getText().equals(""))
            {
                Toast.makeText(getApplicationContext(),"Unable to connect to Network. Cannot Pin the stock.",Toast.LENGTH_LONG).show();
                return true;
            }
            /*Check if the stock has been already added to notification service. IF so
            do NOT pin it.
             */
            SharedPreferences intervalPrefs = this.getSharedPreferences("stock_notification_list.xml", Context.MODE_PRIVATE);
            if(intervalPrefs.contains(stockCode))
            {
                Toast.makeText(getApplicationContext(),"Cannot be pinned. You are already receiving notification for this Scrip",Toast.LENGTH_LONG).show();
                return true;
            }
            /*Check if there are already 5 stocks present in the pinned_stocklist.xml
            Add this stock to the Shared Preference - pinned_stocklist.xml ONLY IF NO OF STOCKS < 5
              Key - stockCode, Value - Random number - which will be the notification ID*/
            SharedPreferences prefs = this.getSharedPreferences("pinned_stocklist.xml", Context.MODE_PRIVATE);
            if(prefs.getAll().size()>=5)
            {
                Toast.makeText(getApplicationContext(),R.string.max_pinned_stock_reached,Toast.LENGTH_LONG).show();
                return true;
            }
            SharedPreferences.Editor editor=prefs.edit();
            Random random=new Random();
            int notificationID=random.nextInt();
            editor.putInt(stockCode,notificationID);
            editor.commit();

            /*Make the pinit icon invisible and make the unpin icon visible*/
            MenuItem unpinItem=menu.findItem(R.id.action_unpinStock);
            unpinItem.setVisible(true);
            item.setVisible(false);



            /*Define intent to specify where to navigate on  clicking the notification*/
            Intent intent = new Intent(this, GetStockQuote.class);
            intent.putExtra("stock",stock);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(GetStockQuote.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(intent);
            PendingIntent pIntent = stackBuilder.getPendingIntent(notificationID,0);
            /*Determine if the change in current price is positive or negative*/
            String lastPrice=gGetQuote.getStockQuote().getLastPrice();
            if(Float.parseFloat(gGetQuote.getStockQuote().getpChange())>0)
                lastPrice=lastPrice+" (+"+gGetQuote.getStockQuote().getpChange()+")";
            else
                lastPrice=lastPrice+"("+gGetQuote.getStockQuote().getpChange()+")";

            /*Set the Refresh action to call the intent service
              UpdateIndividualPinnedStockService
             */
            Intent refreshIntent=new Intent(this,UpdateIndividualPinnedStockService.class);
            refreshIntent.putExtra("stockQuote",gGetQuote.getStockQuote());
            refreshIntent.putExtra("notificationID",notificationID);
            PendingIntent refreshPendingIntent=PendingIntent.getService(this,notificationID+1,refreshIntent,0);

            /*Get the Content of the notification to be displayed*/
            StockQuote temp=gGetQuote.getStockQuote();
            String notificationContent=new CommonFuncs().constructNotificationContent(temp.getLastPrice(),temp.getpChange(),temp.getSharesTraded(),temp.getDayHigh(),temp.getDayLow());

            /*Build the notification*/
            String pChange=temp.getpChange();
            if(Float.parseFloat(pChange)>0)
                pChange="+"+pChange;
            NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this)
                                     .setContentTitle(stock.getStockCode())
                                     .setContentText(temp.getLastPrice()+" ("+pChange+")")
                                     .setAutoCancel(false)
                                     .setSmallIcon(R.drawable.ic_launcher)
                                     .setOngoing(true)

                                     .setContentInfo(lastUpdatedTime.getText())
                                     .setContentIntent(pIntent).
                            addAction(R.drawable.configure,"Refresh",refreshPendingIntent);

            /*Big Notification*/
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            inboxStyle.addLine("Price : "+temp.getLastPrice()+" ("+pChange+")");
            inboxStyle.addLine("Volumes : "+temp.getSharesTraded());
            inboxStyle.addLine("Day High/Low : "+temp.getDayHigh()+"/"+temp.getDayLow());

            notificationBuilder.setStyle(inboxStyle);
            Log.i(TAG,"notification built");

            /*Get instance of Notification Manager and show the notification*/
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID,notificationBuilder.build());
            Log.i(TAG,"notification.notified");
            /*Make a Call to a BroadCast Receiver - NotificationUpdateReceiver
              To make a call to the alarm manager
             */
            Intent intent1 = new Intent();
            intent1.setAction(ACTION_NOTIFICATION_UPDATE);
            sendBroadcast(intent1);

        }
        else if(id==R.id.action_unpinStock)
        {
            /*Remove the stock from notification bar and from pinned_stocklist.xml*/
            SharedPreferences prefs = this.getSharedPreferences("pinned_stocklist.xml", Context.MODE_PRIVATE);
            int notificationID=(Integer) prefs.getInt(stockCode,-1);
            SharedPreferences.Editor editor=prefs.edit();
            editor.remove(stockCode);
            editor.commit();

             /*Get instance of Notification Manager and cancel the notification*/
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationID);

            /*Make the unpin icon invisible and make the pinit icon visible*/
            MenuItem pinitItem=menu.findItem(R.id.action_pinStock);
            pinitItem.setVisible(true);
            item.setVisible(false);

        }
        return super.onOptionsItemSelected(item);
    }



}
