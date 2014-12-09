package com.puzzle;

import java.util.HashMap;


import com.google.analytics.tracking.android.EasyTracker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.droid8puzzle.R;

public class history extends Activity {

	float[] x;
	float[] y;
	float[] x1;
	float[] y1;
	bitmap[] bm;
	boolean first_time;
	HashMap<String,Bitmap> bitmap_store;
	int[] goal1;
	int[] goal2;
	float screen_width,screen_height,bitmapwidth,bitmapheight;
	OurView v;
	Paint paint;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 WindowManager w = getWindowManager();
		 Display d = w.getDefaultDisplay(); 
	        int sw = d.getWidth(); 
	        int sh = d.getHeight();
	        screen_width=sw;
	        screen_height=sh;
	        bitmapwidth=screen_width/9;
	        bitmapheight=bitmapwidth;
	        paint=new Paint();
	        paint.setColor(Color.WHITE);
	        paint.setTextSize(18);
	        //paint.setUnderlineText(true);
	        v=new OurView(getApplicationContext());
	        Bitmap zero=BitmapFactory.decodeResource(getResources(),R.drawable.zero);
	        Bitmap one=BitmapFactory.decodeResource(getResources(),R.drawable.one);
	        Bitmap two=BitmapFactory.decodeResource(getResources(),R.drawable.two);
	        Bitmap three=BitmapFactory.decodeResource(getResources(),R.drawable.three);
	        Bitmap four=BitmapFactory.decodeResource(getResources(),R.drawable.four);
	        Bitmap five=BitmapFactory.decodeResource(getResources(),R.drawable.five);
	        Bitmap six=BitmapFactory.decodeResource(getResources(),R.drawable.six);
	        Bitmap seven=BitmapFactory.decodeResource(getResources(),R.drawable.seven);
	        Bitmap eight=BitmapFactory.decodeResource(getResources(),R.drawable.eight);
	       
	        Bitmap zero_scaled=Bitmap.createScaledBitmap(zero, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap one_scaled=Bitmap.createScaledBitmap(one, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap two_scaled=Bitmap.createScaledBitmap(two, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap three_scaled=Bitmap.createScaledBitmap(three, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap four_scaled=Bitmap.createScaledBitmap(four, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap five_scaled=Bitmap.createScaledBitmap(five, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap six_scaled=Bitmap.createScaledBitmap(six, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap seven_scaled=Bitmap.createScaledBitmap(seven, (int)bitmapwidth, (int)bitmapheight, true);
	        Bitmap eight_scaled=Bitmap.createScaledBitmap(eight, (int)bitmapwidth, (int)bitmapheight, true);
	        
	        //put the bitmaps in the hashmap
	        bitmap_store=new HashMap<String,Bitmap>();
	        bitmap_store.put("zero_scaled", zero_scaled);
	        bitmap_store.put("one_scaled", one_scaled);
	        bitmap_store.put("two_scaled", two_scaled);
	        bitmap_store.put("three_scaled", three_scaled);
	        bitmap_store.put("four_scaled", four_scaled);
	        bitmap_store.put("five_scaled", five_scaled);
	        bitmap_store.put("six_scaled", six_scaled);
	        bitmap_store.put("seven_scaled", seven_scaled);
	        bitmap_store.put("eight_scaled", eight_scaled);
	        int[] temp1={1,2,3,4,5,6,7,8,0};
	        int[] temp2={1,2,3,8,0,4,7,6,5};
	        goal1=temp1;
	        goal2=temp2;
	        initialize_cords(bitmapwidth, bitmapheight);
	        setContentView(R.layout.history);
	        RelativeLayout rl2=(RelativeLayout) findViewById(R.id.rl2);
	        ViewGroup.LayoutParams params =  rl2.getLayoutParams();
	        params.height = (int)(screen_height*0.40);
	        rl2.setLayoutParams(params);
	        // Button temp=(Button) findViewById(R.id.two);
	         try
	         {
	         rl2.addView(v);
	         //temp.setVisibility(8);
	         }catch(Exception e)
	         {
	         	Log.v("onCreate", e.toString());
	         }
	         TextView tv=(TextView) findViewById(R.id.about);
	         tv.setText(Html.fromHtml(getString(R.string.desc)));
		
	}
	
	private void initialize_cords(float bitmapwidth, float bitmapheight) {
		// TODO Auto-generated method stub
		x=new float[9];
		y=new float[9];
		x1=new float[9];
		y1=new float[9];
		x[0]=(0.08f*screen_width)+(bitmapwidth/2)-(bitmapwidth/2);
		y[0]=(bitmapheight)-(bitmapheight/2);
		x[1]=(0.12f*screen_width)+(3*bitmapwidth/2)-(bitmapwidth/2);
		y[1]=(bitmapheight)-(bitmapheight/2);
		x[2]=(0.16f*screen_width)+(5*bitmapwidth/2)-(bitmapwidth/2);
		y[2]=(bitmapheight)-(bitmapheight/2);
		
		x[3]=(0.08f*screen_width)+(bitmapwidth/2)-(bitmapwidth/2);
		y[3]=(0.06f*screen_height)+(2*bitmapheight)-(bitmapheight/2);
		x[4]=(0.12f*screen_width)+(3*bitmapwidth/2)-(bitmapwidth/2);
		y[4]=(0.06f*screen_height)+(2*bitmapheight)-(bitmapheight/2);
		x[5]=(0.16f*screen_width)+(5*bitmapwidth/2)-(bitmapwidth/2);
		y[5]=(0.06f*screen_height)+(2*bitmapheight)-(bitmapheight/2);
		
		x[6]=(0.08f*screen_width)+(bitmapwidth/2)-(bitmapwidth/2);
		y[6]=(0.12f*screen_height)+(3*bitmapheight)-(bitmapheight/2);
		x[7]=(0.12f*screen_width)+(3*bitmapwidth/2)-(bitmapwidth/2);
		y[7]=(0.12f*screen_height)+(3*bitmapheight)-(bitmapheight/2);
		x[8]=(0.16f*screen_width)+(5*bitmapwidth/2)-(bitmapwidth/2);
		y[8]=(0.12f*screen_height)+(3*bitmapheight)-(bitmapheight/2);
		
		x1[0]=x[0]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[0]=(bitmapheight)-(bitmapheight/2);
		x1[1]=x[1]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[1]=(bitmapheight)-(bitmapheight/2);
		x1[2]=x[2]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[2]=(bitmapheight)-(bitmapheight/2);
		
		x1[3]=x[3]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[3]=(0.06f*screen_height)+(2*bitmapheight)-(bitmapheight/2);
		x1[4]=x[4]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[4]=(0.06f*screen_height)+(2*bitmapheight)-(bitmapheight/2);
		x1[5]=x[5]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[5]=(0.06f*screen_height)+(2*bitmapheight)-(bitmapheight/2);
		
		x1[6]=x[3]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[6]=(0.12f*screen_height)+(3*bitmapheight)-(bitmapheight/2);
		x1[7]=x[7]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[7]=(0.12f*screen_height)+(3*bitmapheight)-(bitmapheight/2);
		x1[8]=x[8]+(0.16f*screen_width)+2*(0.04f*screen_width)+3*bitmapwidth;
		y1[8]=(0.12f*screen_height)+(3*bitmapheight)-(bitmapheight/2);
	    
	}
	
	public class OurView extends SurfaceView implements Runnable
	{
		Thread t=null;
		SurfaceHolder holder;
		boolean ok;
		int[] current_state_copy;
		boolean putBitmap_locked;
		boolean run_locked;
		public OurView(Context context)
		{
			super(context);
			holder=getHolder();
			current_state_copy=new int[9];
			putBitmap_locked=true;
			run_locked=false;
		}
		public void run()
		{
			Log.v("run()", "enter");
			while(ok==true)
			{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Draw
				if(!holder.getSurface().isValid())  
				{ 
					continue;
				}
				
				Canvas c=holder.lockCanvas();
				int i=0;
				for(i=0;i<9;i++)
				{
					 c.drawBitmap(bitmap_store.get(get_bitmap_name(goal1[i])), x[i], y[i], null);
					// c.drawBitmap(bitmap_store.get(get_bitmap_name(goal2[i])), x1[i], y1[i], null);
			    }
				c.drawText("You have to", x[2]+1.5f*bitmapwidth, 2*bitmapheight, paint);
				c.drawText("reach this", x[2]+1.8f*bitmapwidth, 2.5f*bitmapheight, paint);
				c.drawText("configuration", x[2]+1.5f*bitmapwidth, 3*bitmapheight, paint);
				holder.unlockCanvasAndPost(c);
		  }
		}
		public void pause()
		{
			ok=false;
			Log.v("pause()", "ok=false");
			while(true)
			{
				try{
					Log.v("pause()", "before join");
					t.join();
					Log.v("pause()", "after join");
				}
				catch(InterruptedException e)
				{
					Log.v("pause()",e.toString());
				}
				Log.v("pause()", "breaking from while loop");
				break;
			}
           t=null;
           Log.v("pause()", "thread stopped and nulled");
		}
		public void resume()
		{
			ok=true;
			if(t==null)
			{
			 t=new Thread(this);
	           t.start();
	           Log.v("resume()", "new thread started");
			}
			else
			{
				Log.v("resume()", "new thread not started as t!=null");
			}
		}
		
		private String get_bitmap_name(int n)
		{
			if(n==0)
				return "zero_scaled";
			else if(n==1)
				return "one_scaled";
			else if(n==2)
				return "two_scaled";
			else if(n==3)
				return "three_scaled";
			else if(n==4)
				return "four_scaled";
			else if(n==5)
				return "five_scaled";
			else if(n==6)
				return "six_scaled";
			else if(n==7)
				return "seven_scaled";
			else
				return "eight_scaled";
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v("onPause()", "super.onPause()");
		v.pause();
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v("onResume()", "super.onResume()");
		v.resume();
      
	}
	
	 //FOR GOOGLE ANALYTICS
	 @Override
	  public void onStart() {
	    super.onStart();
	    // The rest of your onStart() code.
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	     // The rest of your onStop() code.
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
}
