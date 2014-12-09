package com.rss.ashwin.myyc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rss.ashwin.myyc.adapters.RSSListItemAdapter;
import com.rss.ashwin.myyc.dataobjects.RSSListItem;
import com.rss.ashwin.myyc.reusables.GetRSSList;

import java.util.ArrayList;


public class NewList extends Activity {

    ListView rssListView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        rssListView=(ListView)findViewById(R.id.rssListView);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        //Make a call to the async task to display the list view
        AsyncHandler handler=new AsyncHandler();
        new GetRSSList(handler).execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class AsyncHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0)
            {
                //When an error has occured
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Error in fetching the feed. Please check your internet connection",Toast.LENGTH_LONG).show();
                return;
            }
            if(msg.what==1)
            {

                //Display progress bar
                progressBar.setVisibility(View.VISIBLE);
                return;
            }
            progressBar.setVisibility(View.GONE);
           ArrayList<RSSListItem> rssList=(ArrayList<RSSListItem>)msg.obj;
            RSSListItemAdapter rssListViewAdapter=new RSSListItemAdapter(getApplicationContext(),R.layout.listitem_rss,rssList);
            rssListView.setAdapter(rssListViewAdapter);
        }
    }
}
