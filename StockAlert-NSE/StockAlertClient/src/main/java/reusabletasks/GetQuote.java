package reusabletasks;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droid.stockalertclient.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import helper.Stock;
import helper.StockQuote;

/**
 * Created by ashwin on 19/10/14.
 */
public class GetQuote extends AsyncTask<String,Void, StockQuote>
{
    private final String TAG=GetQuote.class.getSimpleName();
    private final String NSEURL="http://www.nseindia.com/live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=";
    private TextView currentPrice,dayHighLow,yearHighLow,lastUpdatedTime,openPrice,tradedVolume;
    private ProgressBar progressBar;
    private Context context;
    private StockQuote stockQuote;
    public GetQuote(TextView currentPrice,TextView dayHighLow,TextView yearHighLow,TextView lastUpdatedTime,TextView tradedVolume,TextView openPrice,ProgressBar progressBar,Context context)
    {
        this.currentPrice=currentPrice;
        this.dayHighLow=dayHighLow;
        this.yearHighLow=yearHighLow;
        this.lastUpdatedTime=lastUpdatedTime;
        this.openPrice=openPrice;
        this.tradedVolume=tradedVolume;
        this.progressBar=progressBar;
        this.context=context;
    }
    public StockQuote getStockQuote()
    {
        if(stockQuote!=null)
            return stockQuote;
        return null;
    }
    private void setStockQuote(StockQuote stockQuote)
    {
        this.stockQuote=stockQuote;
    }
    @Override
    protected StockQuote doInBackground(String...stockCode) {
        Log.i(TAG, System.getProperty("http.agent"));
        // System.setProperty("http.agent","");
        Log.i(TAG,System.getProperty("http.agent"));
        StockQuote stockQuote;
        //start the progress bar
        publishProgress();
        try{
            //fetch details from NSE site
            Connection connection= Jsoup.connect(NSEURL + stockCode[0]);
            connection.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n");
//               connection.header("Accept-Encoding","gzip,deflate,sdch");
//               connection.header("Accept-Language","en-US,en;q=0.8");
//               connection.header("Cache-Control","max-age=0");
//               connection.header("Connection","keep-alive");
//               connection.header("Connection","keep-alive");
//               connection.header("Host","www.nseindia.com");
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
            String change=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("change");
            String sharesTraded=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("quantityTraded");
            String lastUpdatedTime=(String) stockDetails.get("lastUpdateTime");

            stockQuote=new StockQuote(stockCode[0],lastPrice,dayHigh,dayLow,yearHigh,yearLow,openPrice,previousClose,change,sharesTraded,lastUpdatedTime);
            setStockQuote(stockQuote);
        }
        catch(IOException ex1)
        {
            stockQuote=null;
            Log.e(TAG,ex1.toString());
        }
        catch(JSONException ex2)
        {
            stockQuote=null;
            Log.e(TAG,ex2.toString());
        }

        return stockQuote;
    }

    @Override
    protected void onPostExecute(StockQuote stockQuote) {
            /*Populate the layout field the the stock values and GONE the progressBar*/
        progressBar.setVisibility(View.GONE);
        if(stockQuote==null)
            Toast.makeText(context, R.string.stockDetailFetchError, Toast.LENGTH_LONG).show();
        else
        {
            String text= "As on "+stockQuote.getLastUpdatedTime();
            Typeface tf=Typeface.createFromAsset(context.getAssets(), "DroidSerif-Regular.ttf");
            Spannable sbLastUpdated = new SpannableString(text);
            sbLastUpdated.setSpan(new StyleSpan(tf.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
            sbLastUpdated.setSpan(new StyleSpan(tf.ITALIC), 5, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//resize size
            ImageSpan is=null;

            String strChange=stockQuote.getpChange();
            if(strChange.equals("-"))     //- implies no change
                strChange="0.00";
            else {
                float change = Float.parseFloat(stockQuote.getpChange());
                if (change > 0)
                    strChange = "+" + strChange;
            }
            Spannable sbLastPrice = new SpannableString(stockQuote.getLastPrice()+" ("+strChange+"  )");
            int start=stockQuote.getLastPrice().length()+stockQuote.getpChange().length()+1;
            int end=start+1;


            lastUpdatedTime.setText(sbLastUpdated);
            currentPrice.setText(stockQuote.getLastPrice()+" ("+strChange+"  )");
            openPrice.setText(stockQuote.getOpenPrice());
            dayHighLow.setText(stockQuote.getDayHigh()+"/"+stockQuote.getDayLow());
            tradedVolume.setText(stockQuote.getSharesTraded());
            yearHighLow.setText(stockQuote.getYearHigh()+"/"+stockQuote.getYearLow());

        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        progressBar.setVisibility(View.VISIBLE);
    }
}