package com.droid.stockalertclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import dialog.DialogCancelListener;
import dialog.DialogNoOnClickListener;
import dialog.DialogOkOnClickListener;

public class AddStock extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       getActionBar().setDisplayHomeAsUpEnabled(true);
        final Context  context=this;
        setContentView(R.layout.addstock_layout);
        final AutoCompleteTextView stock = (AutoCompleteTextView) findViewById(R.id.addStock);
        final String[] stocks = getResources().getStringArray(R.array.stock_names);
        stock.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                String item = (String) parent.getItemAtPosition(pos);
                //your stuff
              //  Toast.makeText(getApplicationContext(),item,Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle(item);
                builder1.setMessage(R.string.addstock_dialog_message);
                builder1.setCancelable(true);
                builder1.setPositiveButton(R.string.addstock_dialog_yes,new DialogOkOnClickListener(context,item,context.getResources().getString(R.string.nse)));
                builder1.setNegativeButton(R.string.addstock_dialog_no,new DialogNoOnClickListener());

                AlertDialog alert11 = builder1.create();
                alert11.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
                alert11.setOnCancelListener(new DialogCancelListener(stock));// DialogCancelListener contains the action to be performed once this dialog is cancelled
                alert11.show();
            }
        });

        ArrayAdapter<String> stockAdapter = new ArrayAdapter<String>(this, R.layout.autocomplete_layout, stocks);
        stock.setThreshold(2);
        stock.setAdapter(stockAdapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_stock, menu);
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
