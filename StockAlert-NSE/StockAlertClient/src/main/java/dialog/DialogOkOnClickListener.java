package dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.droid.stockalertclient.R;

import org.json.JSONException;

import helper.CommonFuncs;

/**
 * Created by ashwin on 2/8/14.
 */
final public class DialogOkOnClickListener implements DialogInterface.OnClickListener{
    private final Context context;
    private final String scrip;
    private final String exchange;

    public DialogOkOnClickListener(Context context, String scrip,String exchange)
    {
        this.context=context;
        this.scrip=scrip;
        this.exchange=exchange;
    }

 public void onClick(DialogInterface dialog, int which)
 {

     //Put this script in the shared preferences
     SharedPreferences prefs = context.getSharedPreferences("stocklist.xml", Context.MODE_PRIVATE);
     SharedPreferences.Editor editor=prefs.edit();
     editor.putString("abc",null);
     String[] stock=scrip.split("-");
     String stockName=stock[0].trim();
     String stockCode=stock[1].trim();

     //check if the stock is already present in the watchlist
     if(prefs.contains(stockCode))
     {
         Toast.makeText(context, R.string.stockAlreadyPresent,Toast.LENGTH_LONG).show();
         dialog.cancel();
         return;
     }

     CommonFuncs func=new CommonFuncs();
     try{
     String jsonString=func.contructJSONString(stockName,stockCode,exchange);
     editor.putString(stockCode,jsonString);
     editor.commit();
         Toast.makeText(context, R.string.stockAddSuccess,Toast.LENGTH_LONG).show();
     }catch(JSONException e)
     {
         Log.e("AddStockDialogOk",e.toString());
     }
     finally{
     dialog.cancel();

     }
 }


}
