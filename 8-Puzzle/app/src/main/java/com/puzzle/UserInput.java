package com.puzzle;

import java.util.HashMap;

import com.droid8puzzle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.puzzle.history.OurView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class UserInput extends Activity implements OnTouchListener {
    
	OurView v;
	float screen_width,screen_height,bitmapwidth,bitmapheight;
	Paint paint;
	float[] x;
	float[] y;
	HashMap<String,Bitmap> bitmap_store;
	int[] initial_state;
	int[] current_state;
	bitmap[] bm;
	int CURRENT_MOVING;  //stores the currently moving bitmap's start position
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
	        bitmapwidth=screen_width/5;
	        bitmapheight=bitmapwidth;
	        //Initialize coordinates
	       
	        paint=new Paint();
	        paint.setColor(Color.WHITE);
	        paint.setTextSize(18);
	        //paint.setUnderlineText(true);
	        v=new OurView(getApplicationContext());
	        v.setOnTouchListener(this);
	        
	        setContentView(R.layout.userip);
	        RelativeLayout rl2=(RelativeLayout) findViewById(R.id.rl2);
	        rl2.addView(v);
	        ViewGroup.LayoutParams params =  rl2.getLayoutParams();
	        params.height = (int)(screen_height*0.63);
	        rl2.setLayoutParams(params);
	        
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
	        initial_state=temp1;
	        current_state=new int[9];
	        
	        CURRENT_MOVING=0;
	       
			bm=new bitmap[9];
			 initialize_cords(bitmapwidth, bitmapheight);
			 int i=0;
		        for(i=0;i<9;i++)
		        {
		        	current_state[i]=initial_state[i];
		        	bm[current_state[i]]=new bitmap(v.get_bitmap_name(current_state[i]),x[i],y[i],current_state[i]);
		        }
		        Button solve=(Button) findViewById(R.id.solve);
		        solve.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						
						//-----ANALYTICS CODE-------
						// May return null if a EasyTracker has not yet been initialized with a
						  // property ID.
						  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

						  // MapBuilder.createEvent().build() returns a Map of event fields and values
						  // that are set and sent with the hit.
						  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
						                   "button_press",  // Event action (required)
						                   "Solve Button for UserDefined Puzzle",   // Event label
						                   null)            // Event value
						      .build()
						  );
						//-------------
						
						// TODO Auto-generated method stub
						EightPuzzle x=new EightPuzzle(current_state,2,0);
						if(x.inversions()%2==1)
						{
						 Toast.makeText(getApplicationContext(), "Sorry, this configuration is unsolvable.", Toast.LENGTH_LONG).show();	
						}
						else
						{	
					     Intent i=new Intent(UserInput.this,solution.class);
					     i.putExtra("initial_state", current_state);
					     startActivity(i);
						}
					   
					}
				});
		        
	}
	private void initialize_cords(float bitmapwidth, float bitmapheight) {
		// TODO Auto-generated method stub
		
		 x=new float[9];
			y=new float[9];
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
				
				//Draw
				if(!holder.getSurface().isValid())  
				{ 
					continue;
				}
				
				Canvas c=holder.lockCanvas();
				int i=0;
				c.drawARGB(255, 0, 0, 0);
				for(i=0;i<9;i++)
				{
					 c.drawBitmap(bitmap_store.get(bm[i].get_name()), bm[i].get_x(), bm[i].get_y(), null);
					// c.drawBitmap(bitmap_store.get(get_bitmap_name(goal2[i])), x1[i], y1[i], null);
			    }
				
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
		
		public String get_bitmap_name(int n)
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
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		try
		{
			Thread.sleep(50);
		}
		catch(InterruptedException e)
		{
			Log.v("in onTouch",e.toString());
		}
		
		Log.v("onTouch()", "Enter");
		float x1=event.getX()-bitmapwidth/2;
		float y1=event.getY()-bitmapheight/2;
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN :
			
			Log.v("onTouch()", "ACTION_DOWN");
			if((x1>x[0]-bitmapwidth/2 && x1<x[0]+bitmapwidth/2) && (y1>y[0]-bitmapheight/2 && y1<y[0]+bitmapheight/2))
			{
				CURRENT_MOVING=0;
			}
			else if((x1>x[1]-bitmapwidth/2 && x1<x[1]+bitmapwidth/2) && (y1>y[1]-bitmapheight/2 && y1<y[1]+bitmapheight/2))
			{
				CURRENT_MOVING=1;
				
			}
			else if((x1>x[2]-bitmapwidth/2 && x1<x[2]+bitmapwidth/2) && (y1>y[2]-bitmapheight/2 && y1<y[2]+bitmapheight/2))
			{
				CURRENT_MOVING=2;
			}
			else if((x1>x[3]-bitmapwidth/2 && x1<x[3]+bitmapwidth/2) && (y1>y[3]-bitmapheight/2 && y1<y[3]+bitmapheight/2))
			{
				CURRENT_MOVING=3;
			}
			else if((x1>x[4]-bitmapwidth/2 && x1<x[4]+bitmapwidth/2) && (y1>y[4]-bitmapheight/2 && y1<y[4]+bitmapheight/2))
			{
				CURRENT_MOVING=4;
			}
			else if((x1>x[5]-bitmapwidth/2 && x1<x[5]+bitmapwidth/2) && (y1>y[5]-bitmapheight/2 && y1<y[5]+bitmapheight/2))
			{
				CURRENT_MOVING=5;
			}
			else if((x1>x[6]-bitmapwidth/2 && x1<x[6]+bitmapwidth/2) && (y1>y[6]-bitmapheight/2 && y1<y[6]+bitmapheight/2))
			{
				CURRENT_MOVING=6;
			}
			else if((x1>x[7]-bitmapwidth/2 && x1<x[7]+bitmapwidth/2) && (y1>y[7]-bitmapheight/2 && y1<y[7]+bitmapheight/2))
			{
				CURRENT_MOVING=7;
			}
			else if((x1>x[8]-bitmapwidth/2 && x1<x[8]+bitmapwidth/2) && (y1>y[8]-bitmapheight/2 && y1<y[8]+bitmapheight/2))
			{
				CURRENT_MOVING=8;
			}
			else
			{
				CURRENT_MOVING=-1;  //DON'T MOVE ANYTHNG
			}
			
			break;
		case MotionEvent.ACTION_MOVE :
			if(CURRENT_MOVING!=-1)
			{
			 Log.v("onTouch()", "ACTION_MOVE");
			 bm[current_state[CURRENT_MOVING]].set_xy(x1,y1);
			}
			break;
		case MotionEvent.ACTION_UP :
			
			Log.v("onTouch()", "ACTION_UP");
			if((x1>x[0]-bitmapwidth/2 && x1<x[0]+bitmapwidth/2) && (y1>y[0]-bitmapheight/2 && y1<y[0]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				//EXCHANGE POSITIONS
				
			   
				bm[current_state[CURRENT_MOVING]].set_xy(x[0], y[0]);
				bm[current_state[0]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[0];
				current_state[0]=temp;
				//bm[CURRENT_MOVING].set_xy(x[0],y[0]);
				//bm[0].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[1]-bitmapwidth/2 && x1<x[1]+bitmapwidth/2) && (y1>y[1]-bitmapheight/2 && y1<y[1]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
					
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[1], y[1]);
				bm[current_state[1]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[1];
				current_state[1]=temp;
				//bm[CURRENT_MOVING].set_xy(x[1],y[1]);
				//bm[1].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[2]-bitmapwidth/2 && x1<x[2]+bitmapwidth/2) && (y1>y[2]-bitmapheight/2 && y1<y[2]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[2], y[2]);
				bm[current_state[2]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[2];
				current_state[2]=temp;
				//bm[CURRENT_MOVING].set_xy(x[2],y[2]);
				//bm[2].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[3]-bitmapwidth/2 && x1<x[3]+bitmapwidth/2) && (y1>y[3]-bitmapheight/2 && y1<y[3]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[3], y[3]);
				bm[current_state[3]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[3];
				current_state[3]=temp;
				//bm[CURRENT_MOVING].set_xy(x[3],y[3]);
				//bm[3].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[4]-bitmapwidth/2 && x1<x[4]+bitmapwidth/2) && (y1>y[4]-bitmapheight/2 && y1<y[4]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[4], y[4]);
				bm[current_state[4]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[4];
				current_state[4]=temp;
				//bm[CURRENT_MOVING].set_xy(x[4],y[4]);
				//bm[4].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[5]-bitmapwidth/2 && x1<x[5]+bitmapwidth/2) && (y1>y[5]-bitmapheight/2 && y1<y[5]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[5], y[5]);
				bm[current_state[5]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[5];
				current_state[5]=temp;
				//bm[CURRENT_MOVING].set_xy(x[5],y[5]);
				//bm[5].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[6]-bitmapwidth/2 && x1<x[6]+bitmapwidth/2) && (y1>y[6]-bitmapheight/2 && y1<y[6]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[6], y[6]);
				bm[current_state[6]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[6];
				current_state[6]=temp;
				
				//bm[CURRENT_MOVING].set_xy(x[6],y[6]);
				//bm[6].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[7]-bitmapwidth/2 && x1<x[7]+bitmapwidth/2) && (y1>y[7]-bitmapheight/2 && y1<y[7]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[7], y[7]);
				bm[current_state[7]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[7];
				current_state[7]=temp;
				//bm[CURRENT_MOVING].set_xy(x[7],y[7]);
				//bm[7].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else if((x1>x[8]-bitmapwidth/2 && x1<x[8]+bitmapwidth/2) && (y1>y[8]-bitmapheight/2 && y1<y[8]+bitmapheight/2) && CURRENT_MOVING!=-1)
			{
				
				
				bm[current_state[CURRENT_MOVING]].set_xy(x[8], y[8]);
				bm[current_state[8]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
				int temp=current_state[CURRENT_MOVING];
				current_state[CURRENT_MOVING]=current_state[8];
				current_state[8]=temp;
				//bm[CURRENT_MOVING].set_xy(x[8],y[8]);
				//bm[8].set_xy(x[CURRENT_MOVING],y[CURRENT_MOVING]);
			}
			else
			{
				//Set x and y to original values
				if(CURRENT_MOVING!=-1)
				bm[current_state[CURRENT_MOVING]].set_xy(x[CURRENT_MOVING], y[CURRENT_MOVING]);
				
			}
			break;
		default :
			break;
		}
		return true;
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
