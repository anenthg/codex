package helper;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.droid.stockalertclient.GetStockQuote;
import com.droid.stockalertclient.IndividualSetting;
import com.droid.stockalertclient.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

import customlayouts.BackGroundContainer;

/**
 * Created by ashwin on 3/8/14.
 */
public class StockAdapter extends ArrayAdapter<Stock> implements Animator.AnimatorListener{

    private  static  ArrayList<Stock> stocks;
    private  Context context;
    private Typeface tf;

    BackGroundContainer mBackgroundContainer;
    //public View animatingView;
    private float mDownXR,mDownY,mDownX;
    int mSlop;
    private StockAdapter adapter;
    private View animatingView;
    public int animatingRow=-1;
    private HashMap<String,Integer> itemIDs;
    ListView listview;
    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private final static String TAG=StockAdapter.class.getSimpleName();
    private VelocityTracker velocityTracker;



    public static class ViewHolder {
        public Button button;
        public ImageButton imageButton;
        public ImageButton undoButton;
        public int position;
        public TextView lastPrice;
        public TextView rightSwipeView;
        public int uniqueID;
    }


    public StockAdapter(Context context,int textViewResourceId,ArrayList<Stock> stocks, ListView listView)
    {
        super(context,textViewResourceId,stocks);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
            (Context.LAYOUT_INFLATER_SERVICE);
        View view=(View) inflater.inflate(R.layout.fragment_watch_list,null);
        mBackgroundContainer=(BackGroundContainer)view.findViewById(R.id.backgroundContainer);
        adapter=this;
        this.context=context;
        this.stocks=stocks;
        tf=Typeface.createFromAsset(context.getAssets(), "DroidSerif-Regular.ttf");
        //helper=new CommonFuncs();
        animatingRow=-1;
        listview=listView;
        for (int i = 0; i < stocks.size(); ++i) {
            mIdMap.put(stocks.get(i).getStockText(), i);
        }
    }

    public void refreshItemIDs()
    {
        for (int i = 0; i < stocks.size(); ++i) {
            mIdMap.put(stocks.get(i).getStockText(), i);
        }
    }

    @Override
    public long getItemId(int position) {
        Stock item = getItem(position);
        return mIdMap.get(item.getStockText());
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

   /* @Override
    public boolean hasStableIds() {
        return true;
    }*/

    @Override
    public  View getView(int position,View convertView,ViewGroup parent)
    {
        View rowView=convertView;
        // reuse views
        if (rowView == null) {
            Log.i(TAG,"Creating a new View");
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.stocklist_row, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.button = (Button) rowView.findViewById(R.id.stockButton);
            viewHolder.imageButton = (ImageButton) rowView
                    .findViewById(R.id.imageButton);
            viewHolder.undoButton=(ImageButton) rowView.findViewById(R.id.undoButton);
            viewHolder.position=position;
            viewHolder.lastPrice=(TextView) rowView.findViewById(R.id.lastPrice);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String stockCode=stocks.get(position).getStockCode();
        String stockName=stocks.get(position).getStockName();
        String buttonText= stockCode+" - "+stockName;
        Spannable sb = new SpannableString( buttonText );
        sb.setSpan(new StyleSpan(tf.BOLD), 0, stockCode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
        sb.setSpan(new StyleSpan(tf.NORMAL), stockCode.length()+3, buttonText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//resize size

        holder.button.setText(sb);
        final int pos=position;

        // set the imagebtton to visible nad undo button to gone
        //done to reset the changes that would have been don eif a row ws swi[ped
        holder.imageButton.setVisibility(View.VISIBLE);
        holder.undoButton.setVisibility(View.GONE);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to notification setting activity
                Intent intent=new Intent(context,IndividualSetting.class);
                intent.putExtra("stock",stocks.get(pos));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              context.startActivity(intent);
;
            }
        });
        holder.undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"undoButton OnClickListener");
                //Cancel the animation running on animatingView
                animatingView.animate().cancel();
                //Reset the alpha to 1 on the animatingView
                animatingView.setAlpha(1);
                //Make animating Row=-1
                animatingRow=-1;
                //make undoButton Gone and imageButton visible
                view.setVisibility(View.GONE);
                View parentView=(View) view.getParent();
                ImageButton imgButton=(ImageButton) parentView.findViewById(R.id.imageButton);
                imgButton.setVisibility(View.VISIBLE);

            }
        });
       // holder.button.setClickable(true);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Button OnClickListener");
                /*Navigate to GetStockQuote Activity with the Stock object passed as IntentExtra
                 */
                Intent intent=new Intent(context,GetStockQuote.class);
                intent.putExtra("stock",stocks.get(pos));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
        holder.button.setOnTouchListener(stockButtonOnTouch);
        rowView.setOnTouchListener(stockRowOnTouch);

        new GetQuote(context,holder.lastPrice,stockCode).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
       
        return rowView;
    }

    private View.OnTouchListener stockButtonOnTouch=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.i(TAG,"Button OnTouchListener");
            return true;
        }
    };

 private View.OnTouchListener stockRowOnTouch=new View.OnTouchListener() {
     @Override
     public boolean onTouch(View view, MotionEvent motionEvent) {
         int action = motionEvent.getActionMasked();
         ViewConfiguration vc=ViewConfiguration.get(view.getContext());
         mSlop= vc.getScaledTouchSlop();
        Log.i(TAG,"StockRowView OnTouchListener");

           if(velocityTracker==null)
               velocityTracker=VelocityTracker.obtain();

         Button button=(Button)((ViewHolder)view.getTag()).button;
         ImageButton undoButton=(ImageButton) ((ViewHolder) view.getTag()).undoButton;
         ImageButton imageButton=(ImageButton) ((ViewHolder) view.getTag()).imageButton;
         int itemWidth=view.getWidth();
         switch (action) {
             case MotionEvent.ACTION_DOWN:
                 Log.i(TAG,"Down");
                 //Track Velocity
                 velocityTracker.addMovement(motionEvent);
                 //If a previously swiped row is there (whose animation is running); then cancel the animation
                 // and delete that row
                 ViewHolder va=(ViewHolder) view.getTag();
                 //Log.i(TAG,"view positiong - "+va.position);
                 //Check where there is an animation going on in another Row

                 if(animatingRow!=-1 && animatingRow!=va.position)
                 {
                         Log.i(TAG,"animateRemoveStock");
                         animateRemoveStock();
                         return false;
                 }
                 else if(animatingRow!=-1 && animatingRow==va.position)
                 {
                     
                   button.setPressed(false);//We are going to return false so no touch events will be received for this gesture
                   //return false;
                  }


                 mDownXR=motionEvent.getRawX();
                 mDownX=motionEvent.getX();
                 mDownY=motionEvent.getY();
                 if(mDownXR<button.getRight() && mDownXR>button.getLeft() && animatingRow!=va.position)
                     button.setPressed(true);
                 else if(undoButton.getVisibility()==View.VISIBLE && mDownXR<undoButton.getRight()&& mDownXR>undoButton.getLeft()) {
                    undoButton.setPressed(true);
                 }
                 else if(imageButton.getVisibility()==View.VISIBLE && mDownXR<imageButton.getRight()  && mDownXR>imageButton.getLeft() ) {
                     imageButton.setPressed(true);
                     int imageButton_up_color=imageButton.getDrawingCacheBackgroundColor();
                     Log.i(TAG,Integer.toString(imageButton_up_color));
//                     imageButton.setBackgroundColor(0xff000000 + Integer.parseInt("D0C6C6",16));
                     Log.i(TAG, "onTouch.ACTION_DOWN imageButton"+mDownX);
                 }
                 break;
             case MotionEvent.ACTION_MOVE:
                 if(animatingRow==((ViewHolder) view.getTag()).position)
                     return true;
                 //Track Velocity
                 velocityTracker.addMovement(motionEvent);

                 float deltaX=motionEvent.getX()-mDownX;
                 float deltaY=motionEvent.getY()-mDownY;
                 if(Math.abs(deltaY)<Math.abs(deltaX)/2)     //To Prevent accidental swiping
                 {
                     view.setTranslationX(view.getX() + deltaX - mSlop);
                     float fadeValue=1-((view.getX()+deltaX-mSlop)/view.getWidth());//fadeValue=1 -> Transparent
                     view.animate().alpha(fadeValue);

                     /*Set the transaltion if the swipe is right Swipe
                      for the rightSwipeView
                      rightSwipeView should appear as the rest of the view disappears seamlessly
                      */
                     mBackgroundContainer.showBackground(view.getTop(), view.getHeight());
                 }
                 break;
             case MotionEvent.ACTION_CANCEL:
                 //If while swiping the touch has passed on to other view then reset the stockrow to its original position
                 Log.i(TAG, "onTouch.ACTION_CANCEl");
                 /*If while swiping the touch has passed on to other view
                   If the swipe velocity and distance have crossed the threshold at this point
                   then consider it a removal
                  */

                  /*For Safety just set the background color of the image button to white*/
//                / imageButton.setBackgroundColor(0xff000000 + Integer.parseInt("ffffff",16));

                 float deltaXuc=Math.abs(motionEvent.getRawX()-mDownXR);
                 velocityTracker.computeCurrentVelocity(1);
                 float velocityc=Math.abs(velocityTracker.getXVelocity());
                 if((Math.abs(deltaXuc)<itemWidth/6 || velocityc<0.1)&&animatingRow!=((ViewHolder) view.getTag()).position)
                 {
                     Log.i(TAG,Float.toString(velocityc));
                     view.animate().translationX(0).alpha(1).setDuration(500).setListener(null);
                 }
                 else
                 {
                     Log.i(TAG,"animation start");
                      /*The duration in the animate chain sequesnce should be a function of the
                         Velocity
                          */

                     long duration=(long)(deltaXuc/velocityc);
                     Log.i(TAG,Long.toString(duration));

                     //delete the row at the end of the animation
                     animatingRow=((ViewHolder) view.getTag()).position;
                     animatingView=view;

                     animatingView.animate().translationX(itemWidth).alpha(1).setDuration(500).setListener(adapter);

                 }
                    velocityTracker.clear();

                 break;
             case MotionEvent.ACTION_UP:

                 /*For Safety just set the background color of the image button to white*/
//                 imageButton.setBackgroundColor(0xff000000 + Integer.parseInt("ffffff",16));

                 Log.i(TAG,"Up");
                 float deltaXu=Math.abs(motionEvent.getRawX()-mDownXR);
                 velocityTracker.computeCurrentVelocity(1);
                 float velocity=Math.abs(velocityTracker.getXVelocity());
                 //sLog.i(TAG, "onTouch.ACTION_UP deltaXu---"+deltaXu);

                 /*Decide if the UP action can be justified as a click of the StockButton.
                  */
                 if(button.isPressed() && motionEvent.getX()<button.getRight() && motionEvent.getX() > button.getLeft())
                 {
                     button.setPressed(false);
                     if(deltaXu<=mSlop)
                     {

                         view.animate().translationX(0).alpha(1).setDuration(500).setListener(null);
                         button.performClick();
                         return false;
                     }

                 }
                 /*Decide if the UP action can be ratified as a click of the ImageButton*/
                 if(imageButton.getVisibility()==View.VISIBLE && imageButton.isPressed() && motionEvent.getX() <imageButton.getRight() && motionEvent.getX()>imageButton.getLeft() )
                 {
                     Log.i(TAG, "onTouch.ACTION_UP imageButton"+mDownX);
                     Log.i(TAG, "deltaXu"+deltaXu);
                     Log.i(TAG,"slop"+mSlop);
                     imageButton.setPressed(false);
                     if(deltaXu<=2*mSlop)
                     {
                         view.animate().translationX(0).alpha(1).setDuration(500).setListener(null);
                         imageButton.performClick();
                         Log.i(TAG,"Image Button clicked");

                         return false;
                     }
                 }

                 /*Decide if the UP action can be ratified as a click of the undoButton (if it is visible)
                  */
                 if(undoButton.getVisibility()==View.VISIBLE && undoButton.isPressed())
                 {
                     undoButton.setPressed(false);
                     //see if the upclick is performed on the undo button
                     if( motionEvent.getX()<undoButton.getRight() && motionEvent.getX()>undoButton.getLeft())
                     {
                         undoButton.performClick();
                         Log.i(TAG,"Undo Button clicked");
                     }
                 }

                /*In case all the above tests fail, then return if this is the animating row
                 because going further would only assign more animations to it
                  */
                 if(animatingRow==((ViewHolder) view.getTag()).position)
                     return true;

                 /*ANIMATE REMOVAL ONLY IF THE DISTANCE SWIPED AND SWIPE VELOCITY IS GREATER THAN
                 A THRESHOLD VALUE
                  */

                 if(Math.abs(deltaXu)<itemWidth/6 || velocity<0.1)
                 {
                     Log.i(TAG,Float.toString(velocity));
                     view.animate().translationX(0).alpha(1).setDuration(500).setListener(null);
                 }
                 else
                 {
                     Log.i(TAG,"animation start");
                      /*The duration in the animate chain sequesnce should be a function of the
                         Velocity
                          */

                     long duration=(long)(deltaXu/velocity);
                     Log.i(TAG,Long.toString(duration));

                     //delete the row at the end of the animation
                     animatingRow=((ViewHolder) view.getTag()).position;
                     animatingView=view;
                     animatingView.animate().translationX(itemWidth).alpha(1).setDuration(500).setListener(adapter);

                 }

                //CLEAR THE VELOCITY TRACKER AFTER EVERY UP ACTION
                velocityTracker.clear();

         }

         return true;
     }
 };
    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

        ViewHolder holder=(ViewHolder) animatingView.getTag();
        ImageButton button=holder.undoButton;
        ImageButton imgButton=holder.imageButton;
        Log.i(TAG,"End Animation");

        imgButton.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
       final  int position=holder.position;
        //animate the view back with an animation
        animatingView.setTranslationX(0);
        animatingView.setAlpha(0.8f);
        animatingView.animate().alpha(0.4f).setDuration(3000).setListener(null).withEndAction(new Runnable() {
            @Override
            public void run() {
                //After the animation duration delete the stock
                animatingView.setAlpha(1);
              animateRemoveStock();


            }
        });
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    private void animateRemoveStock() //takes the position at of the stock to be removed from animatinvView's getTag()
    {
        Log.i(TAG, "Removal animation");
        //Removing any pending animations for this view
        animatingView.animate().cancel();
        ViewHolder holder=(ViewHolder) animatingView.getTag();
        int position=holder.position;
        /*Remove the stock from stocklist.xml, service xml and pinned stock xml */
        new CommonFuncs().removeStock(context,stocks.get(position).getStockCode());
        adapter.remove(stocks.get(position));//Calls notifydatasetchanged() internally
        animatingRow=-1;

        //**************Dev Bytes Code******************
     /*   int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != animatingView) {
                int position = firstVisiblePosition + i;
                long itemId = this.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }
        // Delete the item from the adapter
        int position = listview.getPositionForView(animatingView);
        this.remove(this.getItem(position));*/

       /* final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = this.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);


                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        mListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    mListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });*/
        //********************************************
    }
    private class GetQuote extends AsyncTask<Void,Void, StockQuote>
    {
        /*  private TextView currentPrice,dayHighLow,yearHighLow;
          public GetQuote(TextView currentPrice,TextView dayHighLow,TextView yearHighLow,Progressbar)
          {
              this.currentPrice=currentPrice;
              this.dayHighLow=dayHighLow;
              this.yearHighLow=yearHighLow;
          }*/
        private final String NSEURL="http://www.nseindia.com/live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=";
        Context context;
        TextView lastPrice;
        String stockCode;
        private GetQuote(Context context,TextView lastPrice,String stockCode)
        {
            this.context=context;
            this.lastPrice=lastPrice;
            this.stockCode=stockCode;
        }
        @Override
        protected StockQuote doInBackground(Void... voids) {
            StockQuote stockQuote;
            //start the progress bar
            publishProgress();
            try{
                //fetch details from NSE site
                Connection connection= Jsoup.connect(NSEURL + stockCode);
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
                String pChange=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("pChange");
                String sharesTraded=(String) stockDetails.getJSONArray("data").getJSONObject(0).get("quantityTraded");
                String lastUpdatedTime=(String) stockDetails.get("lastUpdateTime");

                stockQuote=new StockQuote(stockCode,lastPrice,dayHigh,dayLow,yearHigh,yearLow,openPrice,previousClose,pChange,sharesTraded,lastUpdatedTime);
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
            //progressBar.setVisibility(View.GONE);
            if(stockQuote==null)
                lastPrice.setText("N/A");
            else
            {
              lastPrice.setText(stockQuote.getLastPrice());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //progressBar.setVisibility(View.VISIBLE);
        }
    }

}


