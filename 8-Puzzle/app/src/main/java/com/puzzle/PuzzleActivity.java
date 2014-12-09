package com.puzzle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;


import com.droid8puzzle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.puzzle.solution.find_solution;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
public class PuzzleActivity extends Activity implements OnTouchListener, OnItemClickListener {
  boolean touch_disabled=false;
  long appEnterTime,appExitTime;//USED IN onStart() and onStop()
  MyHandler handler;
  boolean hintMovesOn;
 static int boost_choice=1;
 //STORE THE CURRENT AND THE PREVIOUS STATES FOR UNDO BUTTON
 int[] now_state, before_state;
    boolean ReduceMove_flag=false;
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
	public int[] initial_state,current_state,goal_state;
	bitmap[] bm;
	boolean first_time;   //Is true when the activity is created for first time
	  int solution_moves;                    //changed to false in OurView.putBitmap()
	  Button shuffle;
	Paint paint;
	Context context;
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
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		boost_choice=position+1;
	}
	private void showBoostDialog()
	{
		try
		{
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.boost_dialog);
			dialog.setTitle("Boost Moves");
			
			final String[] no_moves={"1","2","3"};
			 ListView lv=(ListView) dialog.findViewById(R.id.listView1);
			
			lv.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, no_moves));
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			//Default Selection - 1 Move or the previous choice of the user
			lv.setItemChecked(boost_choice-1, true);
			
			
			lv.setOnItemClickListener(this);
			
			Button okay=(Button) dialog.findViewById(R.id.button1);
			okay.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final EightPuzzle current=new EightPuzzle(current_state,2,0);
					int[] win = {1, 2,
				            3, 4, 5,
				            6, 7, 8,0};
					final EightPuzzle goal1=new EightPuzzle(win,2,0);
					find_boost find=new find_boost(current,goal1);
					dialog.dismiss();
					
					 find.start();
				}
			});
			
			Button cancel=(Button) dialog.findViewById(R.id.button2);
			cancel.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					
				}
			});
			dialog.show();
		}
		catch(Exception e)
		{
			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
		}
		
	}
	private void showDialog()
	{
		try
		   {
		   
          if(moves!=solution_moves)
          {
        	  Builder bd=new Builder(context);
		   bd.setTitle("Good but..");
		   bd.setMessage("You have solved this puzzle in "+Integer.toString(moves)+" moves. But it can be solved in even lesser number of moves.\n     Wanna see?");
		   bd.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent i=new Intent(PuzzleActivity.this,solution.class);
				i.putExtra("initial_state", initial_state);
				startActivity(i);
			}
		});
		   bd.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				shuffle.performClick();
			}
		});
		   bd.show();
          }
          else
          {
        	  //Toast.makeText(getApplicationContext(), "Excellent! completed in minimum moves", Toast.LENGTH_LONG).show();
        	  Builder bd=new Builder(context);
   		   bd.setTitle("Excellent!");
   		   bd.setMessage("You have solved the puzzle in "+Integer.toString(moves)+" moves. Can't get better than that!");
   		   bd.setPositiveButton(R.string.okay,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				shuffle.performClick();
			}
		});
   		   bd.show();
          }
		   
		   }
		   catch(Exception e)
		   {
		    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();	 
		   }
	}
	private void update_moves()
	{
		TextView tv=(TextView) findViewById(R.id.moves);
		tv.setText("Moves:"+Integer.toString(++moves));
		//The moves should be reduceable when a move has been made 
		ReduceMove_flag=true;
	}
	private void reduce_moves()
	{
		if(moves!=0&&ReduceMove_flag==true){
		TextView tv=(TextView) findViewById(R.id.moves);
		tv.setText("Moves:"+Integer.toString(--moves));
		}
	}
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        now_state=new int[9];
        before_state=new int[9];
        context=this;
        WindowManager w = getWindowManager(); 
        
        handler=new MyHandler();
        
        hintMovesOn=false;
        
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
        bitmapwidth=screen_width/4.5f;
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
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(18);
        paint.setUnderlineText(true);
       
        //Replace Title Bar with Custom one
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        try
        {
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        }catch(Exception e)
        {
        	 
        	Log.v("onCreate", e.toString());
        }
        try
        {
        RelativeLayout rl1=(RelativeLayout) findViewById(R.id.rl1);
        ViewGroup.LayoutParams params =  rl1.getLayoutParams();
        params.height = (int)(screen_height*0.10);
        rl1.setLayoutParams(params);
        }catch(Exception e)
        {
        	Log.v("onCreate", e.toString());
        }
        
        final RelativeLayout rl2=(RelativeLayout) findViewById(R.id.rl2);
       // Button temp=(Button) findViewById(R.id.two);
        try
        {
        rl2.addView(v);
        ViewGroup.LayoutParams params =  rl2.getLayoutParams();
        params.height = (int)(screen_height*0.70);
        rl2.setLayoutParams(params);
        //temp.setVisibility(8);
        }catch(Exception e)
        {
        	Log.v("onCreate", e.toString());
        }
        
        //Generate new puzzle and initialize states
          initial_state=generate_correct_puzzle();
          
          //INITIALISE AND ASSIGN PUZZLE STATES TO NOW_STATE AND BEFORE_STATE
          now_state=new int[9];
          before_state=new int[9];
          for(int i=0;i<9;i++)
	        {
	        	now_state[i]=initial_state[i];   //copy initial state to now_state
	        	before_state[i]=initial_state[i];
	        }
         
          
          current_state=new int[9];
        int i=0;
        for(i=0;i<9;i++)
        {
        	current_state[i]=initial_state[i];   //copy initial state to current state
        }
        int[] temp_goal_state={1,2,3,4,5,6,7,8,0};
        goal_state=temp_goal_state;
        v.putBitmap(initial_state);
        
        //Start the thread find_solution now
        solution_moves=-1;
        EightPuzzle start = new EightPuzzle(initial_state, 2, 0);
        int[] win = {1, 2,
            3, 4, 5,
            6, 7, 8,0};
        EightPuzzle goal = new EightPuzzle(win, 2, 0);
        find_solution find=new find_solution(start,goal);
        find.start();
        //initialize the number of moves made by the user
        moves=0;
        
        shuffle=(Button)findViewById(R.id.shuffle);
        shuffle.setOnClickListener(new View.OnClickListener() {
        	
        	
			
			public void onClick(View v1) {
				// TODO Auto-generated method stub
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				Log.v("BEFORE EASY TRACKER SHUFFLE BUTTON CLICK", "--------------");
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());
				  Log.v("AFTER EASY TRACKER SHUFFLE BUTTON CLICK", "--------------");

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "button_press",  // Event action (required)
				                   "Shuffle",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				//Generate new puzzle and initialize states
				solvable=false;  
		          initial_state=generate_correct_puzzle();
		          
		          //INITIALISE AND ASSIGN PUZZLE STATES TO NOW_STATE AND BEFORE_STATE
		          now_state=new int[9];
		          before_state=new int[9];
		          for(int i=0;i<9;i++)
			        {
			        	now_state[i]=initial_state[i];   //copy initial state to now_state
			        	before_state[i]=initial_state[i];
			        }
		          
		          current_state=new int[9];
		        int i=0;
		        for(i=0;i<9;i++)
		        {
		        	current_state[i]=initial_state[i];   //copy initial state to current state
		        }
		        int[] temp_goal_state={1,2,3,4,5,6,7,8,0};
		        goal_state=temp_goal_state;
		        v.putBitmap(initial_state);
				//initialize moves to 0
		        moves=-1;
		        update_moves();
		        //Start the thread find_solution now
		        solution_moves=-1;
		        EightPuzzle start = new EightPuzzle(initial_state, 2, 0);
		        int[] win = {1, 2,
		            3, 4, 5,
		            6, 7, 8,0};
		        EightPuzzle goal = new EightPuzzle(win, 2, 0);
		        find_solution find=new find_solution(start,goal);
		        find.start();
			}
		});
        
        Button solution=(Button)findViewById(R.id.solution);
        solution.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v1) {
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "button_press",  // Event action (required)
				                   "Solution",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				Intent i=new Intent(PuzzleActivity.this,solution.class);
				i.putExtra("initial_state", initial_state);
				startActivity(i);
			}
		});
        TextView tv=(TextView) findViewById(R.id.what);
        tv.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "link_press",  // Event action (required)
				                   "What is 8 Puzzle?",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				Intent i=new Intent(PuzzleActivity.this,history.class);
				startActivity(i);
				
			}
		});
        Button my_input=(Button)findViewById(R.id.ab);
        my_input.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "button_press",  // Event action (required)
				                   "User Defined Puzzle",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				Intent i=new Intent(PuzzleActivity.this,UserInput.class);
				startActivity(i);
				
			}
		});
      
        Button boost=(Button) findViewById(R.id.boost);
        boost.setOnClickListener(new View.OnClickListener()
        {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "button_press",  // Event action (required)
				                   "Boost",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				if(hintMovesOn==false)
				showBoostDialog();
				
		       // Toast.makeText(getApplicationContext(), "in onclick", Toast.LENGTH_LONG).show();
		        Log.d("in onclick listener for boost", "");
			}
        	
        });
        
        //UNDO BUTTON
        Button undo=(Button) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener()
        {

			@Override
			public void onClick(View v1) {
				// TODO Auto-generated method stub
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "button_press",  // Event action (required)
				                   "Undo",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				//Toast.makeText(context, Arrays.toString(before_state), Toast.LENGTH_LONG).show();
				v.putBitmap(before_state);
				reduce_moves();
				Log.v("Thread", Thread.currentThread().getName());
				//MAKE BOTH THE CURRENT AND PREVIOUS STATE EQUAL
				for(int i=0;i<9;i++)
				{
					before_state[i]=now_state[i];
				}
				//Disable the reducing of moves once und button is clicked until a move has been made
				ReduceMove_flag=false;
			}
        	
        });
        //FEEDBACK BUTTON
        
        Button feedback=(Button) findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener()
        {

			@Override
			public void onClick(View v) {
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "button_press",  // Event action (required)
				                   "Feedback",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				//create the send intent  
				Intent intent =   
				 new Intent(PuzzleActivity.this, FeedbackActivity.class);  
			
				startActivity(intent);  
			}
        	
        });
        
        //SHARE BUTTON
        Button share=(Button) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener()
        {

			@Override
			public void onClick(View v) {
				
				//-----ANALYTICS CODE-------
				// May return null if a EasyTracker has not yet been initialized with a
				  // property ID.
				  EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

				  // MapBuilder.createEvent().build() returns a Map of event fields and values
				  // that are set and sent with the hit.
				  easyTracker.send(MapBuilder.createEvent("ui_action",     // Event category (required)
				                   "button_press",  // Event action (required)
				                   "Share",   // Event label
				                   null)            // Event value
				      .build()
				  );
				//-------------
				
				//create the send intent  
				Intent shareIntent =   
				 new Intent(android.content.Intent.ACTION_SEND);  
				  
				//set the type  
				shareIntent.setType("text/plain");  
				  
				//add a subject  
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,   
				 "Take a look at 8-Puzzle");  
				  
				//build the body of the message to be shared  
				String shareMessage = "https://play.google.com/store/apps/details?id=com.droid8puzzle&hl=en";  
				  
				//add the message  
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,   
				 shareMessage);  
				  
				//start the chooser for sharing  
				startActivity(Intent.createChooser(shareIntent,   
				 "Share 8-Puzzle Via"));  
			}
        	
        });
      }
  
    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
            case 1://solution found
            	try{
            	update_moves();
            	if(IsGoalState())
            	{
            		showDialog();
            	}
            	}
            	catch(Exception e)
            	{
            		Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            	}
            	   break;
            case 2: 
            	Toast.makeText(getApplicationContext(), (String) message.obj, Toast.LENGTH_LONG).show();
            	break;
           default :      
                 break;
            }//end switch
        }
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
	private  int[] generate_correct_puzzle() {
		int[] a = {0,1,2,3,4,5,6,7,8};
	    int i=0;
	    while(solvable==false)
	    {
	    	Log.v("generate_correct_puzzle()", "while loop started");
	    Random random=new Random();
	    Random random1=new Random();
	    int oc=random.nextInt(10)+10;
	    for(i=0;i<oc;i++)
	    {
	    	int pos1=random.nextInt(9);
	    	int pos2=random1.nextInt(9);
	    	//Log.v("generate_correct_puzzle()", "pos1="+Integer.toString(pos1));
	    	//Log.v("generate_correct_puzzle()", "pos2="+Integer.toString(pos2));
	    	//exchange positions
	    	int temp=a[pos1];
	    	a[pos1]=a[pos2];
	    	a[pos2]=temp;
	    }
	    v.putBitmap(a);
	    Log.v("generate_correct_puzzle()", "putButmap() called");
		//check if the puzzle is solvable by checking for inverses
	    //check for inverses - if odd inverse not solvable else solvable
	     int count_inversion=0;
	     for(i=0;i<9;i++)
	     {
	    	 int j=0;
	    	 if(a[i]!=0)
	    	 {
	    	 for(j=i+1;j<9;j++)
	    	 {
	    		 if(a[j]!=0 && a[j]>a[i])  //inversion!
	    		 {
	    			 count_inversion++;
	    		 }
	    	 }
	    	 }
	     }
	     if(count_inversion%2==0)
	     {
	    	 //solvable
	    	 solvable=true;
	     }
	     Log.v("generate_correct_puzzle while", "not solvable");
	   }
	    Log.v("generate_correct_puzzle", "puzzle solvable");
	    for(i=0;i<9;i++)
	    Log.v("initial state", Integer.toString(a[i]));
	    return a;
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
	
	
	
	
    public void disable_touch(boolean b) {
        touch_disabled=b;
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
	               solution_moves=solution.size();
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
	public class find_boost extends Thread {
		EightPuzzle start;
		EightPuzzle goal;
		Stack<EightPuzzle> solution;
		find_boost(EightPuzzle initial_state,EightPuzzle goal_state)
		{
		  this.start=initial_state;
		  this.goal=goal_state;
		}
		void make_moves3(int[] move1, int[] move2, int[] move3)
		{
			try{
				Message msg=handler.obtainMessage();
				msg.what=1;
			disable_touch(true);
			Thread.sleep(500);
			v.putBitmap(move1);
			handler.sendMessage(msg);
			Log.v("1st msg sent", "going to sleep");
			Thread.sleep(1000);
			Message msg1=handler.obtainMessage();
			msg1.what=1;
			v.putBitmap(move2);
			handler.sendMessage(msg1);
			Log.v("2nd msg sent", "going to sleep");
			Thread.sleep(1000);
			Message msg2=handler.obtainMessage();
			msg2.what=1;
			v.putBitmap(move3);
			handler.sendMessage(msg2);
			Log.v("3rd msg sent", "");
			
			disable_touch(false);
			}
			catch(InterruptedException e)
			{
				Log.v("InterruptedException", e.toString());
				Message msg=handler.obtainMessage();
				msg.obj=e.toString();
				msg.what=2;
				handler.sendMessage(msg);
			}
			catch(Exception e)
			{
				Log.v("Exception", e.toString());
				Message msg=handler.obtainMessage();
				msg.what=2;
				msg.obj=e.toString();
				
				handler.sendMessage(msg);
			}
		}
		void make_moves2(int[] move1, int[] move2)
		{
			try{
				Message msg=handler.obtainMessage();
				msg.what=1;
			disable_touch(true);
			Thread.sleep(500);
			v.putBitmap(move1);
			handler.sendMessage(msg);
			Log.v("1st msg sent", "going to sleep");
			Thread.sleep(1000);
			Message msg1=handler.obtainMessage();
			msg1.what=1;
			v.putBitmap(move2);
			handler.sendMessage(msg1);
			Log.v("2nd msg sent", "going to sleep");
			
			
			disable_touch(false);
			}
			catch(InterruptedException e)
			{
				Log.v("InterruptedException", e.toString());
				Message msg=handler.obtainMessage();
				msg.obj=e.toString();
				msg.what=2;
				handler.sendMessage(msg);
			}
			catch(Exception e)
			{
				Log.v("Exception", e.toString());
				Message msg=handler.obtainMessage();
				msg.what=2;
				msg.obj=e.toString();
				
				handler.sendMessage(msg);
			}
		}
		void make_moves1(int[] move1)
		{
			try{
				Message msg=handler.obtainMessage();
				msg.what=1;
			disable_touch(true);
			Thread.sleep(500);
			v.putBitmap(move1);
			handler.sendMessage(msg);
			Log.v("1st msg sent", "going to sleep");
			Thread.sleep(1000);
			disable_touch(false);
			}
			catch(InterruptedException e)
			{
				Log.v("InterruptedException", e.toString());
				Message msg=handler.obtainMessage();
				msg.obj=e.toString();
				msg.what=2;
				handler.sendMessage(msg);
			}
			catch(Exception e)
			{
				Log.v("Exception", e.toString());
				Message msg=handler.obtainMessage();
				msg.what=2;
				msg.obj=e.toString();
				
				handler.sendMessage(msg);
			}
		}
		public void run()
		{
			System.out.println("Started");
	        if (start.inversions() % 2 == 1) {
	            System.out.println("Unsolvable");
	           
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
	                int[] move1,move2,move3;
	                try{
	                hintMovesOn=true;
	                if(!solution.isEmpty())
	                move1=solution.pop().puzzle;
	                else
	                	break;
	                if(!solution.isEmpty()&&boost_choice!=1)
	                move2=solution.pop().puzzle;
	                else
	                {
	                	make_moves1(move1);
	                	Thread.sleep(500);
	 	               hintMovesOn=false;

	                break;
	                }
	                if(!solution.isEmpty()&&boost_choice!=2)
	                move3=solution.pop().puzzle;
	                else
	                {
	                	make_moves2(move1,move2);
	                	Thread.sleep(500);
	 	               hintMovesOn=false;

	                     break;
	                }
	               Log.d("found solution", "");
	               //Toast.makeText(getApplicationContext(), "got moves", Toast.LENGTH_LONG).show();
	               make_moves3(move1,move2,move3);
	               Thread.sleep(500);
	               hintMovesOn=false;
	                }
	                catch(InterruptedException e)
	                {
	                	Log.v("Interrupted Exception", e.toString());
	                }
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
				if(!holder.getSurface().isValid() || putBitmap_locked==true)  //holder is invalid or if
				{                                                             //the putBitmap() fn is currently executing
					//Log.v("run method", "holder is not vvalid");
					continue;
				}
				//Lock so that putBitmap waits while thread is executing
				
				run_locked=true;
				Canvas c=holder.lockCanvas();
				if(hintMovesOn)
				c.drawRGB(30, 30, 30);
				else
					c.drawRGB(0, 0, 0);
				
				//c.drawARGB(255, 255, 255, 255);
				//if(bitmap_name.length!=0)   //Enter only if the bitmap_name is not empty
				int k=0;
				int i=0;
				//c.drawARGB(255, 150, 150, 10);
				for(k=0;k<9;k++)
				{
					int j=0;
					i=current_state_copy[k];
					int prev_pos=bm[i].get_pos_prev();
					int prev_state=current_state_copy[prev_pos];
					if((bm[i].get_this_pos()!=prev_pos) && bm[i].get_pos_prev()!=-1 )
					{
						float stepx=(bm[i].get_x()-bm[prev_state].get_x())/50;
						float stepy=(bm[i].get_y()-bm[prev_state].get_y())/50;
						//
					 /*for(j=0;j<50;j++)
					 {
						 if(j==0)
						 {
							 //holder.unlockCanvasAndPost(c);
						 }
						 else
						 {
							 c=holder.lockCanvas();
						 }
						 try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				       c.drawBitmap(bitmap_store.get(bm[i].get_name()), bm[prev_state].get_x()+stepx, bm[prev_state].get_y()+stepy, null);
				       //c.drawBitmap(bitmap_store.get(bm[prev_state].get_name()), bm[i].get_x()-stepx, bm[i].get_y()-stepy, null);
				       stepx+=stepx;
				       stepy+=stepy;
				       holder.unlockCanvasAndPost(c);
						
					 }*/
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
				//c.drawText("Moves:"+Integer.toString(moves), (float) 0.30*screen_width, (float) 0.90*screen_height, paint);
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
		public void putBitmap(int[] tstate)   //state of the puzzle
		{
			Log.v("Threadbit", Thread.currentThread().getName());
			int[] state=new int[9];
			for(int i=0;i<9;i++)
			{
				state[i]=tstate[i];
			}
			//CHANGE THE STATE OF NOW_STATE AND BEFORE_STATE
			
			for(int i=0;i<9;i++)
			{
				before_state[i]=now_state[i];
				now_state[i]=state[i];
			}
			
			while(run_locked==true)
			{
				
			}
			putBitmap_locked=true;  //lock so that the thread cannot draw till this fn is executing
			int i=0;
			for(i=0;i<9;i++)
			{
				//copy the current state to this.current_state_copy
				current_state_copy[i]=state[i];
				if(current_state!=null)
				current_state[i]=state[i]; //CURRENT_STATE IS A GLOBAL; AVAILABLE TO THE WHOLE ACTIVITY
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
		
	Log.v("onTouch()", "Enter");
	if(touch_disabled)
	{
		return true;
	}
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN :
			float x1=event.getX()-bitmapwidth/2;
			float y1=event.getY()-bitmapheight/2;
			
			Log.v("onTouch()", "ACTION_DOWN");
			if((x1>x[0]-bitmapwidth/2 && x1<x[0]+bitmapwidth/2) && (y1>y[0]-bitmapheight/2 && y1<y[0]+bitmapheight/2))
			{
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
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			else if((x1>x[1]-bitmapwidth/2 && x1<x[1]+bitmapwidth/2) && (y1>y[1]-bitmapheight/2 && y1<y[1]+bitmapheight/2))
			{
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
							showDialog();
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
							showDialog();
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
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
							showDialog();
						}
					}
				}
				
			}
			else if((x1>x[2]-bitmapwidth/2 && x1<x[2]+bitmapwidth/2) && (y1>y[2]-bitmapheight/2 && y1<y[2]+bitmapheight/2))
			{
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
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			else if((x1>x[3]-bitmapwidth/2 && x1<x[3]+bitmapwidth/2) && (y1>y[3]-bitmapheight/2 && y1<y[3]+bitmapheight/2))
			{
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
							showDialog();
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
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			else if((x1>x[4]-bitmapwidth/2 && x1<x[4]+bitmapwidth/2) && (y1>y[4]-bitmapheight/2 && y1<y[4]+bitmapheight/2))
			{
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
							showDialog();
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
							showDialog();
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
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			else if((x1>x[5]-bitmapwidth/2 && x1<x[5]+bitmapwidth/2) && (y1>y[5]-bitmapheight/2 && y1<y[5]+bitmapheight/2))
			{
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
							showDialog();
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
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			else if((x1>x[6]-bitmapwidth/2 && x1<x[6]+bitmapwidth/2) && (y1>y[6]-bitmapheight/2 && y1<y[6]+bitmapheight/2))
			{
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
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			else if((x1>x[7]-bitmapwidth/2 && x1<x[7]+bitmapwidth/2) && (y1>y[7]-bitmapheight/2 && y1<y[7]+bitmapheight/2))
			{
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
							//
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
							showDialog();
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
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			else if((x1>x[8]-bitmapwidth/2 && x1<x[8]+bitmapwidth/2) && (y1>y[8]-bitmapheight/2 && y1<y[8]+bitmapheight/2))
			{
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
							//Toast.makeText(getApplicationContext(), "Goal State Reached!", Toast.LENGTH_LONG).show();
							showDialog();
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
							showDialog();
						}
					}
				}
			}
			break;
		
		case MotionEvent.ACTION_UP :
			break;
		default :
			break;
		}
		return true;
	}
	/*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
		 MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.menu, menu);
		    return true;
    }*/
	 //FOR GOOGLE ANALYTICS
	 @Override
	  public void onStart() {
	    super.onStart();
	    appEnterTime=System.currentTimeMillis();
	    Log.v("BEFORE EASY TRACKER START", "--------------");
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	    Log.v("AFTER EASY TRACKER START", "--------------");
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    appExitTime=System.currentTimeMillis();
	    float appTimeMin=(appExitTime-appEnterTime)/(1000*60);
	    Log.v(Long.toString(appExitTime),Long.toString(appEnterTime));
	   Log.v("appTime",Float.toString(appTimeMin));
	    //SEND THE TIME SPENT IN THE APP
	    Tracker easyTracker = EasyTracker.getInstance(this);

	    easyTracker.send(MapBuilder
	        .createTiming("AppTime in Mins",    // Timing category (required)
	                      (long)appTimeMin,       // Timing interval in milliseconds (required)
	                      "AppTime",  // Timing name
	                      null)           // Timing label
	        .build()
	    );
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
	
}