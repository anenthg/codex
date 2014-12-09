package com.rss.ashwin.myyc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.rss.ashwin.myyc.dataobjects.RSSListItem;

/**
 * Created by ashwin on 8/12/14.
 */
public class IndividualNews extends Activity {
    ListView rssListView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        RSSListItem item=(RSSListItem) getIntent().getSerializableExtra("item");
        setContentView(R.layout.webview);
        WebView webview=(WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setWebViewClient(new MyBrowser());
        //Toast.makeText(getApplicationContext(),item.getLink(),Toast.LENGTH_LONG).show();
        progressBar=(ProgressBar) findViewById(R.id.pb);
        webview.loadUrl(item.getLink());
        progressBar.setVisibility(View.VISIBLE);


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

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            view.loadUrl("document.getElementsByClassName(\"wx-googleAdSense\").style.display='none';");//"javascript:your javascript");
        }
    }
}
