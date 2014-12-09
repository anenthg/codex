package com.droid.stockalertclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import helper.Stock;

public class IndividualSetting extends ActionBarActivity {
    private final String TAG=IndividualSetting.class.getSimpleName();
    private final String ACTION_INTERVAL_ALARM="stockapp.action.START_NOTIFICATION_INTERVAL_ALARM";
    private final String ACTION_NOTIFICATION_UPDATE="stockapp.action.START_NOTIFICATION_UPDATE_ALARM";
    private SeekBar seekBar;
    private SharedPreferences updateStockPrefs, pinnedStockPrefs;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final Stock stock=(Stock) getIntent().getExtras().get("stock");
        updateStockPrefs = this.getSharedPreferences("stock_notification_list.xml", Context.MODE_PRIVATE);
        pinnedStockPrefs =this.getSharedPreferences("pinned_stocklist.xml",Context.MODE_PRIVATE);
        setContentView(R.layout.individual_setting);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        /*Check if the stock is already pinned. If so
        disable the seekbar and do no let this be added to notification service
         */

        if(pinnedStockPrefs.contains(stock.getStockCode()))
        {
           // Toast.makeText(getApplicationContext(),"This Scrip is already pinned. Not candidate for receiving notification",Toast.LENGTH_LONG).show();
            seekBar.setEnabled(false);
        }
        /*if the Stock has an interval set, then disable
        the pin icon
         */
        context=this;
        final TextView timer=(TextView) findViewById(R.id.textViewTime);

        //SET THE INITIAL SEEK BAR VALUE FROM THE USER SETTING IF IT WAS SET

        String interval= updateStockPrefs.getString(stock.getStockCode(), "");
        if(!interval.equals(""))
        {

            if(interval.equals(Stock.MINUTES15))
            {
             seekBar.setProgress(25);
                timer.setText(Stock.MINUTES15);
            }
            else if(interval.equals(Stock.MINUTES30))
            {
                seekBar.setProgress(50);
                timer.setText(Stock.MINUTES30);
            }
            else if(interval.equals(Stock.MINUTES45))
            {
                seekBar.setProgress(75);
                timer.setText(Stock.MINUTES45);
            }
            else
            {
                seekBar.setProgress(100);
                timer.setText(Stock.MINUTES60);
            }

        }

        seekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                  int time=(i/25)*15;
                    timer.setText(Integer.toString(time)+" minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor= updateStockPrefs.edit();
                //Update the stock object with the new notification interval if the interval is NOT zero minutes
                if(seekBar.getProgress()/25==0)
                {
                    editor.remove(stock.getStockCode());
                    Toast.makeText(context,"This stock will be removed from notification service",Toast.LENGTH_LONG).show();
                }
                else if(seekBar.getProgress()/25==1)
                {
                    editor.putString(stock.getStockCode(),Stock.MINUTES15);
                    Toast.makeText(context,Stock.MINUTES15,Toast.LENGTH_LONG).show();
                }
                else if(seekBar.getProgress()/25==2)
                {
                    editor.putString(stock.getStockCode(), Stock.MINUTES30);
                    Toast.makeText(context,Stock.MINUTES30,Toast.LENGTH_LONG).show();
                }
                else if(seekBar.getProgress()/25==3)
                    editor.putString(stock.getStockCode(), Stock.MINUTES45);
                else
                    editor.putString(stock.getStockCode(), Stock.MINUTES60);


                editor.commit();

            }
        });

        final ImageButton pin_unpin=(ImageButton) findViewById(R.id.imageButton);
        pin_unpin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ////if(pin_unpin.getImag)
                if(updateStockPrefs.contains(stock.getStockCode()))
                {
                    Toast.makeText(getApplicationContext(),"You are receiving updates for this stock. Cannot be pinned.",Toast.LENGTH_LONG).show();
                }
                else if(pinnedStockPrefs.getAll().size()>5)
                {
                    Toast.makeText(getApplicationContext(),R.string.max_pinned_stock_reached,Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Pin this stock to the notification bar
                    SharedPreferences.Editor editor=pinnedStockPrefs.edit();
                    Random random=new Random();
                    int notificationID=random.nextInt();
                    editor.putInt(stock.getStockCode(),notificationID);
                    editor.commit();

                    //SEND A BROADCAST TO FORCE INVOKE THE PINNED STOCK SERVICE
                    Intent intent1 = new Intent();
                    intent1.setAction(ACTION_NOTIFICATION_UPDATE);
                    sendBroadcast(intent1);

                    /*The stock will be pinned in a few seconds when the update service runs and
                     pins the notification. Toast a message to tell the user the same.
                      */
                    Toast.makeText(getApplicationContext(),"The stock will be pinned in a few seconds..",Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        /*Call the braodcast receiver to start IntentService  for the stocks with notification update
         set
          */
        Intent intent1 = new Intent();
        intent1.setAction(ACTION_INTERVAL_ALARM);
        sendBroadcast(intent1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.individual_setting, menu);
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
        return super.onOptionsItemSelected(item);
    }


}


