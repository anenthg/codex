package com.droid.stockalertclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import dialog.DialogNoOnClickListener;
import dialog.DialogOkOnClickListener;
import helper.CommonFuncs;
import helper.Stock;
import helper.StockAdapter;
import reusabletasks.GetQuote;

public class WatchListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    Context context;
    AutoCompleteTextView autoCompleteTextView;
    final String TAG=WatchListActivity.class.getSimpleName();
    private StockAdapter globalStockAdapter;  //MADE AVAILABLE FOR SHAREDPREFERENCE ON CHANGE LISTENER
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        //Code to be executed onl when the user accesses the app for the first time
         //Check if the shared preferneces file is present in the applciation

        SharedPreferences prefs = this.getSharedPreferences("stocklist.xml", Context.MODE_PRIVATE);
       prefs.registerOnSharedPreferenceChangeListener(this);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.clear();
//        editor.commit();
        if(prefs.contains("abc"))
        {
            //Toast.makeText(getApplicationContext(),"exists!",Toast.LENGTH_LONG).show();
            setContentView(R.layout.fragment_watch_list);
           //iterate through all the stocks in shared preferences for populating in list view
            Map<String,?> stockDetails=prefs.getAll();

            ArrayList<Stock> stocks=new ArrayList<Stock>();
            try
            {
            CommonFuncs help=new CommonFuncs();
           stocks= help.getStockArray(stockDetails);
            }catch(JSONException e)
            {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
            finally {
               // Toast.makeText(getApplicationContext(),R.string.error,Toast.LENGTH_LONG).show();
            }


            //ListView header Layout

            RelativeLayout listHeaderView = (RelativeLayout) findViewById(R.id.header_layout);
            /*Attach the stocks to the autocomplete text view
            Attach item click listener to the autocomplete text view
             */
            String[] getQuoteStocks = getResources().getStringArray(R.array.stock_names);
            AutoCompleteTextView getQuote=(AutoCompleteTextView) listHeaderView.findViewById(R.id.stocks);
            autoCompleteTextView=getQuote;  //To be used by the function onitemclick()
            getQuote.setOnItemClickListener(this);
            ArrayAdapter<String> getQuoteAdapter = new ArrayAdapter<String>(this, R.layout.autocomplete_layout, getQuoteStocks);
            getQuote.setThreshold(2);
            getQuote.setAdapter(getQuoteAdapter);

            ListView stockView=(ListView) findViewById(R.id.listView);
            StockAdapter stockAdapter=new StockAdapter(getApplicationContext(),R.layout.stocklist_row,stocks,stockView);
            globalStockAdapter=stockAdapter;
            stockView.setAdapter(stockAdapter);
            //stockView.addHeaderView(listHeaderView);





        }
        else
        {
            //Display a different layout
           // Toast.makeText(getApplicationContext(),"doesn't exist!",Toast.LENGTH_LONG).show();
            //setContentView(R.layout.watchlist);
        }







    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = this.getSharedPreferences("stocklist.xml", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = prefs.edit();
        if(prefs.contains("abc"))
        {
           // Toast.makeText(getApplicationContext(),"exists!",Toast.LENGTH_LONG).show();
            //setContentView(R.layout.watchlist);
        }
        else
        {
            //Display a different layout
            //Toast.makeText(getApplicationContext(),"doesn't exist!",Toast.LENGTH_LONG).show();
            //setContentView(R.layout.watchlist);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.watch_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            //Open a custom dialog box to add scrip to watchlist
               Intent addStock=new Intent(WatchListActivity.this,AddStock.class);
                startActivity(addStock);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*This method will be called whenever there is a change in the shared preference
        containing stock list. The idea is to refresh the list view when this happens.
         */
        Log.i(TAG, "onSharedPreferenceChanged()");
        if(globalStockAdapter==null)
        {
            Log.i(TAG,"Returning as the globalStockAdapter is null");
            return;
        }
        Log.i(TAG, key);
        try{
        globalStockAdapter.add(new CommonFuncs().getStock(key, getApplicationContext()));
        globalStockAdapter.refreshItemIDs();
        }
        catch(JSONException e)
        {
            Log.i(TAG,e.toString());
        }
        globalStockAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        String item = (String) parent.getItemAtPosition(pos);
        String stockCode=item.split("-")[1].trim();
        Log.i(TAG,item);
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout stockQuote=(LinearLayout) inflater.inflate(R.layout.stock_quote_dialog,null);
        TextView currentPrice,dayHighLow,yearHighLow,tradedVolume,lastUpdatedTime,openPrice;
        ProgressBar progressBar;
        currentPrice=(TextView) stockQuote.findViewById(R.id.currentPriceValue);
        yearHighLow=(TextView) stockQuote.findViewById(R.id.yearHighLowValue);
        dayHighLow=(TextView) stockQuote.findViewById(R.id.dayHighLowValue);
        lastUpdatedTime=(TextView) stockQuote.findViewById(R.id.lastUpdatedTime);
        tradedVolume=(TextView) stockQuote.findViewById(R.id.dayTradedVolumeValue);
        openPrice=(TextView) stockQuote.findViewById(R.id.dayOpenPriceValue);
        progressBar=(ProgressBar) stockQuote.findViewById(R.id.progressBar);



        //  Toast.makeText(getApplicationContext(),item,Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(item);
        builder1.setCancelable(true);
        builder1.setView(stockQuote);
        builder1.setPositiveButton(R.string.addstock_to_watchlist,new DialogOkOnClickListener(context,item,context.getResources().getString(R.string.nse)));
        builder1.setNegativeButton(R.string.cancel,new DialogNoOnClickListener());

        AlertDialog alert11 = builder1.create();
        alert11.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                /*Hide the Keyboard. Keyboard was popped up by the DialogOkOnClickListener
                * Will be called by DialogOkOnCLickListener and DialogNoOnClickListener*/
                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                if (imm.isActive()){
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
                }
                autoCompleteTextView.getText().clear();
            }
        });

        alert11.show();

        new GetQuote(currentPrice,dayHighLow,yearHighLow,lastUpdatedTime,tradedVolume,openPrice,progressBar,context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,stockCode);
    }

}
