package com.puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;

import com.droid8puzzle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.puzzle.PuzzleActivity.OurView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class solution extends Activity implements OnTouchListener {
	Bitmap zero_scaled;
	Bitmap one_scaled;
	Bitmap two_scaled;
	Bitmap three_scaled;
	Bitmap four_scaled;
	Bitmap five_scaled;
	Bitmap six_scaled;
	Bitmap seven_scaled;
	Bitmap eight_scaled;
	boolean solvable;
	float[] x;
	float[] y;
	int moves;
	float screen_width,screen_height;
	float bitmapwidth,bitmapheight;
	OurView v;
	HashMap<String,Bitmap> bitmap_store;
	int[] initial_state,current_state,goal_state;
	bitmap[] bm;
	boolean first_time;   //Is true when the activity is created for first time
	                      //changed to false in OurView.putBitmap()
	ProgressBar pb;
	TextView wait;
	MyHandler handler;
	Stack<EightPuzzle> solution_stack;
	String[] solution_in_words;
	int[] to_be_moved;
	int current_pos;
	ListView ls;
	boolean processing_over;
	Paint paint;
	private boolean IsGoalState() {
		// TODO Auto-generated method stub
		int i=0;
		boolean flag=true;  //true implies goal state not reached
		for(i=0;i<9;i++)
		{
			if(current_state[i]!=goal_state[i])
			{
				//Set the flag to zero
				flag=false;
			}
		}
		
		return flag;
	}
	private void update_moves()
	{
		//TextView tv=(TextView) findViewById(R.id.moves);
		//tv.setText("Moves:"+Integer.toString(++moves));
//		//moves++;
	}
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager w = getWindowManager(); 
        
        v=new OurView(this);   //initialize the ourview
        v.setOnTouchListener(this);  ///set the ontouch listener for v
        //initialize bitmap array
        bm=new bitmap[9];
        Bitmap zero=BitmapFactory.decodeResource(getResources(),R.drawable.zero);
        Bitmap one=BitmapFactory.decodeResource(getResources(),R.drawable.one);
        Bitmap two=BitmapFactory.decodeResource(getResources(),R.drawable.two);
        Bitmap three=BitmapFactory.decodeResource(getResources(),R.drawable.three);
        Bitmap four=BitmapFactory.decodeResource(getResources(),R.drawable.four);
        Bitmap five=BitmapFactory.decodeResource(getResources(),R.drawable.five);
        Bitmap six=BitmapFactory.decodeResource(getResources(),R.drawable.six);
        Bitmap seven=BitmapFactory.decodeResource(getResources(),R.drawable.seven);
        Bitmap eight=BitmapFactory.decodeResource(getResources(),R.drawable.eight);
        Display d = w.getDefaultDisplay(); 
        int sw = d.getWidth(); 
        int sh = d.getHeight();
        screen_width=sw;
        screen_height=sh;
        bitmapwidth=screen_width/6;
        bitmapheight=bitmapwidth;
        
        //initialize the co-ordinates
        initialize_cords(bitmapwidth,bitmapheight);
        
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
        
        first_time=true;
        solvable=false;
        processing_over=false;
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(18);
        paint.setUnderlineText(true);
        try
        {
        setContentView(R.layout.solution);
        }catch(Exception e)
        {
        	Log.v("onCreate", e.toString());
        }
        try
        {
        RelativeLayout rl2=(RelativeLayout) findViewById(R.id.rl2);
        ViewGroup.LayoutParams params =  rl2.getLayoutParams();
        params.height = (int)(screen_height*0.53);
        params.width = (int) (screen_width*80);
        rl2.setLayoutParams(params);
       
        rl2.addView(v);
        }catch(Exception e)
        {
        	Log.v("onCreate", e.toString());
        }
        
        
        initial_state=getIntent().getExtras().getIntArray("initial_state");
          current_state=new int[9];
        int i=0;
        for(i=0;i<9;i++)
        {
        	current_state[i]=initial_state[i];   //copy initial state to current state
        }
        int[] temp_goal_state={0,1,2,3,4,5,6,7,8};
        goal_state=temp_goal_state;
        v.putBitmap(initial_state);
        v.putBitmap(initial_state);
        
        //initialize the number of moves made by the user
        moves=0;
        
        //get the list view
        ls=(ListView) findViewById(R.id.solution_moves);
        
        //initialize the states start the thread to find the solution
        EightPuzzle start = new EightPuzzle(initial_state, 2, 0);
        int[] win = {1, 2,
            3, 4, 5,
            6, 7, 8,0};
        EightPuzzle goal = new EightPuzzle(win, 2, 0);
        find_solution find=new find_solution(start,goal);
            //start the progressbar and text view
        pb=(ProgressBar) findViewById(R.id.pb);
        pb.setVisibility(0);
        wait=(TextView) findViewById(R.id.wait);
        wait.setVisibility(0);
        handler=new MyHandler();
        find.setPriority(Thread.NORM_PRIORITY+2);
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        find.start();
       
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
				try{
				Thread.sleep(10);
				}catch(InterruptedException e)
				{
					Log.v("exception solution.run ", e.toString());
				}
				//Draw
				if(!holder.getSurface().isValid() || putBitmap_locked==true)  //holder is invalid or if
				{                                                             //the putBitmap() fn is currently executing
					//Log.v("run method", "holder is not vvalid");
					continue;
				}
				//Lock so that putBitmap waits while thread is executing
				
				run_locked=true;
				Canvas c=holder.lockCanvas();
				//c.drawARGB(255, 255, 255, 255);
				//if(bitmap_name.length!=0)   //Enter only if the bitmap_name is not empty
				int k=0;
				int i=0;
				for(k=0;k<9;k++)
				{
					int j=0;
					i=current_state_copy[k];
					int prev_pos=bm[i].get_pos_prev();
					int prev_state=current_state_copy[prev_pos];
					if((bm[i].get_this_pos()!=prev_pos) && bm[i].get_pos_prev()!=-1 )
					{
					 // draw exactly at the respective places
					
					 c.drawBitmap(bitmap_store.get(bm[i].get_name()), bm[i].get_x(), bm[i].get_y(), null);
				       //c.drawBitmap(bitmap_store.get(bm[prev_state].get_name()), bm[prev_state].get_x(), bm[prev_state].get_y(), null);
					 //make the prev and current values same
					 bm[i].change( bm[i].get_x(), bm[i].get_y(), bm[i].get_this_pos());
					 //bm[prev_state].change(bm[prev_state].get_x(), bm[prev_state].get_y(), bm[prev_state].get_this_pos());
					 Log.v("run", "inside moving loop");
					 Log.v("previous x made the same as the presenr", "bm[current_state[k]] present x and prev x="+Float.toString(bm[i].get_x())+" "+Float.toString(bm[current_state_copy[bm[i].get_pos_prev()]].get_x()));
					 Log.v("previous y made the same as the presenr", "bm[current_state[k]] present y and prev y="+Float.toString(bm[i].get_y())+" "+Float.toString(bm[current_state_copy[bm[i].get_pos_prev()]].get_y()));
					 Log.v("previous pos made the same as the presenr", "bm[current_state[k]] present pos and prev pos="+Float.toString(bm[i].get_this_pos())+" "+Float.toString(bm[i].get_pos_prev()));
					}
					else
					{
						c.drawBitmap(bitmap_store.get(bm[i].get_name()), bm[i].get_x(), bm[i].get_y(), null);
					}
				
				}
				if(processing_over==true)
				{
					c.drawText("Moves:"+Integer.toString(moves), (float)0.70*screen_width, (float)0.30*screen_height, paint);
				}
				//Unlock 
				run_locked=false;
				holder.unlockCanvasAndPost(c);
				/*try
				{
				 Thread.sleep(50);
				}
				catch(InterruptedException e)
				{
					Log.v("run()", e.toString());
				}*/
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
		public void putBitmap(int[] state)   //state of the puzzle
		{
			while(run_locked==true)
			{
				
			}
			putBitmap_locked=true;  //lock so that the thread cannot draw till this fn is executing
			int i=0;
			for(i=0;i<9;i++)
			{
				//copy the current state to this.current_state_copy
				current_state_copy[i]=state[i];
				Log.v("putBitmap", "current_state["+Integer.toString(i)+"]="+Integer.toString(state[i]));
			}
			if(first_time)
			{
				Log.v("putBitmap", "1st time");
				
			 for(i=0;i<9;i++)
		     {
		 		bm[i]=new bitmap(get_bitmap_name(i),x[i],y[i],i);
			 	Log.v("putBitmap()", "bitmap_name["+Integer.toString(i)+"]="+bm[i].get_name());
			 }
			 first_time=false;   //changed forever until this activity is started again
			}
			else
			{
				Log.v("putBitmap", "bm.change");
				for(i=0;i<9;i++)
				{
					bm[state[i]].change( x[i], y[i], i);
					Log.v("putBitmap", bm[state[i]].get_name());
					Log.v("putBitmap", "x="+Float.toString(bm[state[i]].get_x())+" prev_pos="+Float.toString(bm[state[i]].get_pos_prev())+" current_pos="+Float.toString(bm[state[i]].get_this_pos()));
					Log.v("putBitmap", "y="+Float.toString(bm[state[i]].get_y())+" prevy="+Float.toString(bm[bm[state[i]].get_pos_prev()].get_y()));
				}
			}
			putBitmap_locked=false;   //unlock so that the thread can draw
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
	public boolean onTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
		try
		{
			Thread.sleep(50);
		}
		catch(InterruptedException e)
		{
			Log.v("in onTouch",e.toString());
		}
		 if(processing_over==false)
		 {
			 return false;
		 }
		Log.v("onTouch()", "Enter");
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN :
			float x1=event.getX()-bitmapwidth/2;
			float y1=event.getY()-bitmapheight/2;
			boolean updateList=false;
			Log.v("onTouch()", "ACTION_DOWN");
			if((x1>x[0]-bitmapwidth/2 && x1<x[0]+bitmapwidth/2) && (y1>y[0]-bitmapheight/2 && y1<y[0]+bitmapheight/2) && CheckMove(0))
			{
				updateList=true;
				//If the user clicked on 1st row and 1st column
				if(current_state[0]!=0)  //if not the white block
				{
					if(current_state[1]==0)
					{
						//interchange the positions
						int temp=current_state[0];
						current_state[0]=current_state[1];
						current_state[1]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
						
					 }
					else if(current_state[3]==0)
					{
						//interchange the positions
						int temp=current_state[0];
						current_state[0]=current_state[3];
						current_state[3]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if((x1>x[1]-bitmapwidth/2 && x1<x[1]+bitmapwidth/2) && (y1>y[1]-bitmapheight/2 && y1<y[1]+bitmapheight/2) && CheckMove(1))
			{
				updateList=true;
				//If the user clicked on 1st row and 2nd column
				if(current_state[1]!=0)  //if not the white block
				{
					if(current_state[0]==0)
					{
						//interchange the positions
						int temp=current_state[1];
						current_state[1]=current_state[0];
						current_state[0]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[2]==0)
					{
						//interchange the positions
						int temp=current_state[1];
						current_state[1]=current_state[2];
						current_state[2]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
					else if(current_state[4]==0)
					{
						//interchange the positions
						int temp=current_state[1];
						current_state[1]=current_state[4];
						current_state[4]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
				
			}
			else if((x1>x[2]-bitmapwidth/2 && x1<x[2]+bitmapwidth/2) && (y1>y[2]-bitmapheight/2 && y1<y[2]+bitmapheight/2) && CheckMove(2))
			{
				updateList=true;
				//If the user clicked on 1st row and 3rd column
				if(current_state[2]!=0)  //if not the white block
				{
					if(current_state[1]==0)
					{
						//interchange the positions
						int temp=current_state[1];
						current_state[1]=current_state[2];
						current_state[2]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[5]==0)
					{
						//interchange the positions
						int temp=current_state[2];
						current_state[2]=current_state[5];
						current_state[5]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if((x1>x[3]-bitmapwidth/2 && x1<x[3]+bitmapwidth/2) && (y1>y[3]-bitmapheight/2 && y1<y[3]+bitmapheight/2) && CheckMove(3))
			{
				updateList=true;
				//If the user clicked on 2nd row and 1st column
				if(current_state[3]!=0)  //if not the white block
				{
					if(current_state[0]==0)
					{
						//interchange the positions
						int temp=current_state[3];
						current_state[3]=current_state[0];
						current_state[0]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[4]==0)
					{
						//interchange the positions
						int temp=current_state[4];
						current_state[4]=current_state[3];
						current_state[3]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
					else if(current_state[6]==0)
					{
						//interchange the positions
						int temp=current_state[6];
						current_state[6]=current_state[3];
						current_state[3]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if((x1>x[4]-bitmapwidth/2 && x1<x[4]+bitmapwidth/2) && (y1>y[4]-bitmapheight/2 && y1<y[4]+bitmapheight/2) && CheckMove(4))
			{
				updateList=true;
				//If the user clicked on 2nd row and 2nd column
				if(current_state[4]!=0)  //if not the white block
				{
					if(current_state[1]==0)
					{
						//interchange the positions
						int temp=current_state[4];
						current_state[4]=current_state[1];
						current_state[1]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[3]==0)
					{
						//interchange the positions
						int temp=current_state[4];
						current_state[4]=current_state[3];
						current_state[3]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
					else if(current_state[5]==0)
					{
						//interchange the positions
						int temp=current_state[4];
						current_state[4]=current_state[5];
						current_state[5]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
					else if(current_state[7]==0)
					{
						//interchange the positions
						int temp=current_state[4];
						current_state[4]=current_state[7];
						current_state[7]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if((x1>x[5]-bitmapwidth/2 && x1<x[5]+bitmapwidth/2) && (y1>y[5]-bitmapheight/2 && y1<y[5]+bitmapheight/2) && CheckMove(5))
			{
				updateList=true;
				//If the user clicked on 2nd row and 3rd column
				if(current_state[5]!=0)  //if not the white block
				{
					if(current_state[2]==0)
					{
						//interchange the positions
						int temp=current_state[2];
						current_state[2]=current_state[5];
						current_state[5]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[4]==0)
					{
						//interchange the positions
						int temp=current_state[4];
						current_state[4]=current_state[5];
						current_state[5]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
					else if(current_state[8]==0)
					{
						//interchange the positions
						int temp=current_state[5];
						current_state[5]=current_state[8];
						current_state[8]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if((x1>x[6]-bitmapwidth/2 && x1<x[6]+bitmapwidth/2) && (y1>y[6]-bitmapheight/2 && y1<y[6]+bitmapheight/2)  && CheckMove(6))
			{
				updateList=true;
				//If the user clicked on 3rd row and 1st column
				if(current_state[6]!=0)  //if not the white block
				{
					if(current_state[3]==0)
					{
						//interchange the positions
						int temp=current_state[3];
						current_state[3]=current_state[6];
						current_state[6]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[7]==0)
					{
						//interchange the positions
						int temp=current_state[7];
						current_state[7]=current_state[6];
						current_state[6]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if((x1>x[7]-bitmapwidth/2 && x1<x[7]+bitmapwidth/2) && (y1>y[7]-bitmapheight/2 && y1<y[7]+bitmapheight/2)  && CheckMove(7))
			{
				updateList=true;
				//If the user clicked on 3rd row and 2nd column
				if(current_state[7]!=0)  //if not the white block
				{
					if(current_state[4]==0)
					{
						//interchange the positions
						int temp=current_state[4];
						current_state[4]=current_state[7];
						current_state[7]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[6]==0)
					{
						//interchange the positions
						int temp=current_state[6];
						current_state[6]=current_state[7];
						current_state[7]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
					else if(current_state[8]==0)
					{
						//interchange the positions
						int temp=current_state[8];
						current_state[8]=current_state[7];
						current_state[7]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			else if((x1>x[8]-bitmapwidth/2 && x1<x[8]+bitmapwidth/2) && (y1>y[8]-bitmapheight/2 && y1<y[8]+bitmapheight/2)  && CheckMove(8))
			{
				updateList=true;
				//If the user clicked on 3rd row and 3rd column
				if(current_state[8]!=0)  //if not the white block
				{
					if(current_state[7]==0)
					{
						//interchange the positions
						int temp=current_state[7];
						current_state[7]=current_state[8];
						current_state[8]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							///Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					 }
					else if(current_state[5]==0)
					{
						//interchange the positions
						int temp=current_state[5];
						current_state[5]=current_state[8];
						current_state[8]=temp;
						//Draw the new current_state
						v.putBitmap(current_state);
						update_moves();
						//check if the goal state has been reached
						if(IsGoalState())
						{
							//GOAL STATE REACHED!
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
			if(updateList==true)
			{
				//call changeList
				changeList();
			}
			break;
		
		case MotionEvent.ACTION_UP :
			break;
		default :
			break;
		}
		return true;
	}
	public boolean CheckMove(int pos)
	{
		return (to_be_moved[current_pos]==pos);
	}
	public void changeList()
	{
		if(current_pos==moves-1)
		{
			Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_LONG).show();
		}
		else
		showList(++current_pos);
	}
	//handler for the find_solution thread
	public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
            case 1://solution found
            	solution_stack=(Stack<EightPuzzle>)message.obj;
            	if(solution_stack.isEmpty())
            	{
            		Toast.makeText(getApplicationContext(), "Already in goal state!", Toast.LENGTH_LONG).show();
            	}
            	else
            	{
            	   convert_to_solution_words(solution_stack);
            	   processing_over=true;
            	   showList(current_pos);
            	}
            	   pb.setVisibility(8);  //gone
            	   wait.setVisibility(8);
            	   break;
            case 0://wrong inout
            	pb.setVisibility(8);  //gone
            	Toast.makeText(getApplicationContext(), "Wrong Input!", Toast.LENGTH_LONG).show();
            	break;
           default :      
                 break;
            }//end switch
        }
    }
	public class find_solution extends Thread {
		EightPuzzle start;
		EightPuzzle goal;
		Stack<EightPuzzle> solution;
		find_solution(EightPuzzle initial_state,EightPuzzle goal_state)
		{
		  this.start=initial_state;
		  this.goal=goal_state;
		}
		public void run()
		{
			System.out.println("Started");
	        if (start.inversions() % 2 == 1) {
	            System.out.println("Unsolvable");
	            Message msg=handler.obtainMessage();
	            msg.what=0;  //empty for unsolvable
	            handler.sendMessage(msg);
	            return;
	        }
	        HashMap<Integer,EightPuzzle> closedset = new HashMap<Integer,EightPuzzle>();
	        PriorityQueue<EightPuzzle> openset = new PriorityQueue<EightPuzzle>();

	        openset.add(start);
	        //int i=0;
	        while (!openset.isEmpty()) {
	            EightPuzzle x = openset.poll();
	            if (x.mapEquals(goal)) {
	                Stack<EightPuzzle> solution = reconstruct(x);
	                Message msg=handler.obtainMessage();
	                msg.obj=solution;
	                msg.what=1;  //for the solution
	                handler.sendMessage(msg);
	                break;
	            }
	            closedset.put(x.hashcode(),x);
	            LinkedList<EightPuzzle> neighbor = x.getChildren();
	            while (!neighbor.isEmpty()) {
	                EightPuzzle y = neighbor.removeFirst();
	                if (closedset.containsKey(y.hashcode())) {
	                	if(y.equals(closedset.get(y.hashcode())))
	                	{
	                		//System.out.println("here1");
	                     continue;
	                     
	                	}
	                }
	                openset.add(y);
	            }
	            //System.out.println(i++);
	        }
		}
		 public  Stack<EightPuzzle> reconstruct(EightPuzzle winner) {
		        solution = new Stack<EightPuzzle>();

		        while (winner.getParent() != null) {
		            solution.add(winner);
		            winner = winner.getParent();
		        }

		        return solution;
		    }

	}
	public void convert_to_solution_words(Stack<EightPuzzle> solution)
     {
		EightPuzzle prev_state=new EightPuzzle(initial_state,2,0);
		moves=solution.size();
		solution_in_words=new String[moves];
		to_be_moved=new int[moves];
		current_pos=0;
		int i=0;
		while(!solution.isEmpty())
		{
			EightPuzzle present_state=solution.pop();
			boolean right=false,left=false,up=false,down=false;
			//get row and column number of zero in previous and present state
			 int prev_loc,present_loc;
			 for(prev_loc=0;prev_state.puzzle[prev_loc]!=0;prev_loc++){}
			 for(present_loc=0;present_state.puzzle[present_loc]!=0;present_loc++){}
			 
			 int prev_row_no=prev_loc/3;
			 int prev_col_no=prev_loc%3;
			 int present_row_no=present_loc/3;
			 int present_col_no=present_loc%3;
			 
			 if(prev_row_no!=present_row_no)
			 {
				 if(present_row_no>prev_row_no)
				 {
					 down=true;
				 }
				 else
				 {
					 up=true;
				 }
			 }
			 else if(prev_col_no!=present_col_no)
			 {
				 if(present_col_no>prev_col_no)
				 {
					 right=true;
				 }
				 else
				 {
					 left=true;
				 }
			 }
			if(down==true)
			{
				//zero is down; this implies the other number has moved up
				  //location of the other number  = loc_zero - 3
				int pos=present_loc;
				solution_in_words[i]="move "+Integer.toString(present_state.puzzle[present_loc-3])+" up";
				to_be_moved[i]=pos;
			}
			else if(up==true)
			{
				int pos=present_loc;
				solution_in_words[i]="move "+Integer.toString(present_state.puzzle[present_loc+3])+" down";
				to_be_moved[i]=pos;
			}
			else if(right==true)
			{
				 int pos=present_loc;
				solution_in_words[i]="move "+Integer.toString(present_state.puzzle[present_loc-1])+" left";
				to_be_moved[i]=pos;
			}
			else if(left==true)
			{
				int pos=present_loc;
				solution_in_words[i]="move "+Integer.toString(present_state.puzzle[present_loc+1])+" right";
				to_be_moved[i]=pos;
			}
			i++;
			prev_state=present_state;
		}
	}
    public void showList(int pos_colored)
    {
    	Log.v("initially", "current_pos="+Integer.toString(current_pos)+" sol in words="+solution_in_words[0]+" to be moved="+Integer.toString(to_be_moved[current_pos]));
    	my_adapter adapter=new my_adapter(getApplicationContext(),solution_in_words,pos_colored);
    	ls.setAdapter(adapter);
    	if(pos_colored==0)
    	ls.setSelection(pos_colored);
    	else
    	ls.setSelection(pos_colored-1);
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
