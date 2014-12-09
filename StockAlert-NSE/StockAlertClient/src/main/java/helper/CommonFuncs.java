package helper;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import	org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ashwin on 2/8/14.
 */
public class CommonFuncs {

       private String TAG=CommonFuncs.class.getSimpleName();

    //Declare constants
    private static final String STOCK_CODE="stockCode";
    private static final String STOCK_NAME="stockName";
    private static final String STOCK_EXCHANGE="stockExchange";
    private static final String STOCK_NOTIFICATION_SETTING="stockNotificationSetting";
    private static final String STOCK_NOTIFICATION_PRICE="stockNotificationPrice";
    private static final String STOCK_FLUCTUATION_LEVEL="stockFluctuationLevel";
    private static final String STOCK_UPDATE_INTERVAL="stockUpdateInterval";

    /*Constants for partialStringConfig function - To make a string partially bold, italics
     or chagne partial color etc
      */
//    private final String

    public String contructJSONString(String stockName,String stockCode,String exchange) throws JSONException

    {
            JSONObject stockDetails=new JSONObject();
            stockDetails.put(STOCK_CODE,stockCode);
            stockDetails.put(STOCK_NAME,stockName);
            stockDetails.put(STOCK_EXCHANGE,exchange);
            stockDetails.put(STOCK_NOTIFICATION_SETTING,"");//Default is Null until the user sets a value thorugh setting
            stockDetails.put(STOCK_NOTIFICATION_PRICE,""); //Default is Null until the user sets a value through setting
            stockDetails.put(STOCK_FLUCTUATION_LEVEL,""); //Default is Null until the user sets a value through setting
            stockDetails.put(STOCK_UPDATE_INTERVAL,"");//Default is Null until the user sets a value through setting


         return stockDetails.toString();
    }
    public Stock getStock(String stockSymbol,Context context) throws JSONException
    {
        SharedPreferences prefs=context.getSharedPreferences("stocklist.xml", Context.MODE_PRIVATE);
        String stockString=prefs.getString(stockSymbol,"");
        JSONObject jsonObject1=new JSONObject(stockString);

        String stockCode=jsonObject1.getString(STOCK_CODE);
        String stockName=jsonObject1.getString(STOCK_NAME);
        String stockExchange=jsonObject1.getString(STOCK_EXCHANGE);
        String stockNotificationSetting=jsonObject1.getString(STOCK_NOTIFICATION_SETTING);
        String stockNotificationPrice=jsonObject1.getString(STOCK_NOTIFICATION_PRICE);
        String stockFluctuationLevel=jsonObject1.getString(STOCK_FLUCTUATION_LEVEL);
        String stockUpdateInterval=jsonObject1.getString(STOCK_UPDATE_INTERVAL);

        Stock object=new Stock(stockName,stockCode,stockExchange,stockNotificationPrice,stockFluctuationLevel,stockUpdateInterval,stockNotificationSetting);
        return object;

    }
    public ArrayList<Stock> getStockArray(Map<String,?> stockDetails) throws JSONException
    {
        ArrayList<Stock> stocks=new ArrayList<Stock>();
       for(Map.Entry<String,?> entry:stockDetails.entrySet())
       {
           if(entry.getValue()!=null)
           {
               //Create Stock  Objects
               String value=(String) entry.getValue(); //value is the stock details
               JSONObject jsonObject1=new JSONObject(value);

                String stockCode=jsonObject1.getString(STOCK_CODE);
                String stockName=jsonObject1.getString(STOCK_NAME);
               String stockExchange=jsonObject1.getString(STOCK_EXCHANGE);
               String stockNotificationSetting=jsonObject1.getString(STOCK_NOTIFICATION_SETTING);
               String stockNotificationPrice=jsonObject1.getString(STOCK_NOTIFICATION_PRICE);
               String stockFluctuationLevel=jsonObject1.getString(STOCK_FLUCTUATION_LEVEL);
               String stockUpdateInterval=jsonObject1.getString(STOCK_UPDATE_INTERVAL);

                Stock object=new Stock(stockName,stockCode,stockExchange,stockNotificationPrice,stockFluctuationLevel,stockUpdateInterval,stockNotificationSetting);
               stocks.add(object);
           }
        }
        return stocks;
    }

    //return the object of Stock class in JSON format
    public String convertToJSON(Stock stock) throws JSONException
    {
        JSONObject stockDetails=new JSONObject();
        stockDetails.put(STOCK_CODE,stock.getStockCode());
        stockDetails.put(STOCK_NAME,stock.getStockName());
        stockDetails.put(STOCK_EXCHANGE,stock.getStockExchange());
        stockDetails.put(STOCK_NOTIFICATION_SETTING,stock.getNotificationSetting());
        stockDetails.put(STOCK_NOTIFICATION_PRICE,stock.getStockNotificationPrice());
        stockDetails.put(STOCK_FLUCTUATION_LEVEL,stock.getStockFluctuationLevel());
        stockDetails.put(STOCK_UPDATE_INTERVAL,stock.getUpdateInterval());

        return stockDetails.toString();
    }

   /* public SpannableStringBuilder partialStringConfig(String text,int start,int end,String what)
    {
        SpannableStringBuilder sb = new SpannableStringBuilder(toBeBold+" - "+normal);
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bss, 0, toBeBold.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold
        return sb;
    }*/

    public void removeStock(Context context,String stockCode)
    {
        //stocklist
        SharedPreferences prefs = context.getSharedPreferences("stocklist.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.remove(stockCode);
        editor.commit();

        //pinned stocklist
        SharedPreferences prefs1 = context.getSharedPreferences("pinned_stocklist.xml", Context.MODE_PRIVATE);
        editor=prefs1.edit();
        /*Make sure that if the stock is present in pinned stocklist, then the notification is
         also dismissed
         */
        int pinnedNotificationID=prefs1.getInt(stockCode,-1);   //-1 is the value returned if the stock is not present in the list
        if(!(pinnedNotificationID==-1))
        {
             /*Get instance of Notification Manager and cancel the notification*/
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.cancel(pinnedNotificationID);
        }
        editor.remove(stockCode);
        editor.commit();

        //service stocklist
        SharedPreferences prefs2 = context.getSharedPreferences("stock_notification_list.xml", Context.MODE_PRIVATE);
        /*Make sure that if the stock is present in service stocklist, then the notification is
         also dismissed
         */
        String serviceInterval=prefs2.getString(stockCode,"");   //empty value is returned if the stock is not present in the list
        if(!(serviceInterval.equals("")))
        {
             /*Get instance of Notification Manager and cancel the notification*/
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.cancel(stockCode.hashCode());
        }
        editor=prefs2.edit();
        editor.remove(stockCode);
        editor.commit();


    }

    public String constructNotificationContent(String lastPrice, String pChange,String volume,String dayHigh,String dayLow)
    {
        /*Constructs the content text to be used in the notification
        Uses GetQuote object (a member of this class) to get the stock quote info)
         */
        StringBuilder sb=new StringBuilder("");
        if(Float.parseFloat(pChange)>0)
            sb.append("Price : "+lastPrice+" (+"+pChange+")");  //Display '+' sign
        else
            sb.append("Price : "+lastPrice+" ("+pChange+")");
        sb.append("\n");
        sb.append("Day High/Low"+dayHigh+"/"+dayLow);
        sb.append("\n");
        sb.append("Volume : "+volume);


        return sb.toString();
    }
}
