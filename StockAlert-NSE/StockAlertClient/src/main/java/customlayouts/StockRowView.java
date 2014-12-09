package customlayouts;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.droid.stockalertclient.R;

import globaldata.GlobalObject;
import helper.StockAdapter;

/**
 * Created by ashwin on 9/8/14.
 */
public class StockRowView extends LinearLayout
{
    private final int durationMilliseconds = 1000;
    private final int displacementPixels = 200;
    private static final String TAG = StockRowView.class.getSimpleName();
    private boolean isInOriginalPosition = true;
    private boolean isSliding = false;
    private float mDownXR,mDownY,mDownX;
    int mSlop;
    private Context context;
    private ListView listView; //The list vie from which the item has to be removed if the swipe is successful

    public StockRowView(Context context)
    {
        super(context);
        Log.i(TAG,"constructor with 1 argument");
    }
    public StockRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewConfiguration vc=ViewConfiguration.get(context);
        mSlop=vc.getScaledTouchSlop();

        this.context=context;
        Log.i(TAG,"constructor with 2 arguments");
    }

  /*  @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        Log.i(TAG,"dispatch touch event");
        onInterceptTouchEvent(ev);

        return false;
    }*/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {

        Log.i(TAG,"StockRowView onInterceptTouchEvent");

        return true;
//        int action = motionEvent.getActionMasked();
//       //new Exception().printStackTrace();
//
//        int itemWidth=this.getWidth();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                //If a previously swiped row is there (whose animation is running); then cancel the animation
//                // and delete that row
//
//                GlobalObject globalObject=(GlobalObject) context.getApplicationContext();
//                if(globalObject.getStockAdapter().animatingRow!=-1 && globalObject.getStockAdapter().animatingView!=null)
//                {
//                    Log.i(TAG,"INSIDE IF OF ACTION_DOWN");
//                    globalObject.getStockAdapter().animatingView.animate().cancel();
//                    globalObject.getStockList().remove(globalObject.getStockAdapter().animatingRow);
//                    globalObject.getStockAdapter().notifyDataSetChanged();
//                    globalObject.getStockAdapter().animatingRow=-1;
//                }
//
//                mDownXR=motionEvent.getRawX();
//                mDownX=motionEvent.getX();
//                mDownY=motionEvent.getY();
//                Log.i(TAG, "onInterceptTouchEvent.ACTION_DOWN---mDownX---"+mDownX);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float deltaX=motionEvent.getX()-mDownX;
//                float deltaY=motionEvent.getY()-mDownY;
//                if(Math.abs(deltaY)<Math.abs(deltaX)/2)     //To Prevent accidental swiping
//                this.setTranslationX(getX() + deltaX-mSlop);
//                Log.i(TAG, "onInterceptTouchEvent.ACTION_MOVE getX()---"+getX());
//                Log.i(TAG, "onInterceptTouchEvent.ACTION_MOVE deltaX---"+deltaX);
//                Log.i(TAG, "onInterceptTouchEvent.ACTION_MOVE megetX()---"+motionEvent.getX());
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.i(TAG, "onInterceptTouchEvent.ACTION_CANCEl");
//                //If while swiping the touch has passed on to other view then reset the stockrow to its original position
//                this.animate().translationX(0).alpha(1).setDuration(500).setListener(null);
//                        break;
//            case MotionEvent.ACTION_UP:
//                float deltaXu=motionEvent.getRawX()-mDownXR;
//                Log.i(TAG, "onInterceptTouchEvent.ACTION_UP deltaXu---"+deltaXu);
//                if(Math.abs(deltaXu)<itemWidth/3)
//                    this.animate().translationX(0).alpha(1).setDuration(500).setListener(null);
//                else
//                {
//                    Log.i(TAG,"animation start");
//                    final StockRowView th=this;
//                    //delete the row at the end of the animation
//                    Button button=(Button) this.findViewById(R.id.undoButton);
//                    ImageButton imgbutton=(ImageButton)this.findViewById(R.id.imageButton);
//                    button.setVisibility(VISIBLE);
//                    imgbutton.setVisibility(GONE);
//                    this.animate().translationX(itemWidth).alpha(1).setDuration(500).setListener(this);
//                }
//
//                //return true;
//
//        }
//        return super.onInterceptTouchEvent(motionEvent);
    }

}
