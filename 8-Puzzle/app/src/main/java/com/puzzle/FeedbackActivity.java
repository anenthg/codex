package com.puzzle;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.droid8puzzle.R;


import serialize.Feedback;

public class FeedbackActivity extends Activity {
	Handler handler1;
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		final EditText feedback=(EditText) findViewById(R.id.feedback);
		final EditText email=(EditText) findViewById(R.id.email);
		final ProgressBar pb=(ProgressBar) findViewById(R.id.pb);
		Locale currentLocale = Locale.getDefault();
		final String country=currentLocale.getDisplayCountry();
		Button send=(Button) findViewById(R.id.send_feedback);
		send.setOnClickListener(new View.OnClickListener()
        {

			@Override
			public void onClick(View v1) {
				//VALIDATE IF THE FEEDBACK IS NOT EMPTY
				String text=feedback.getText().toString();
				if(text.equals(""))
				{
					//TOAST AN ERROR MESSAGE
					
				      Toast.makeText(getApplicationContext(),"You forgot to give the feedback!", Toast.LENGTH_LONG).show(); 	
				}
				else
				{
				//CHECK IF THERE IS AN EMAIL ID PRESENT
					final serialize.Feedback se;
					if(email.getText().toString().equals(""))
					{
						//USE A JUNK VALUE FOR EMAIL
						se=new serialize.Feedback("junk",text,TimeZone.getDefault().getDisplayName());
					}
					else
					{
						se=new serialize.Feedback(email.getText().toString(),text,TimeZone.getDefault().getDisplayName());
					}
					pb.setVisibility(0);
					Thread runn=new Thread(new Runnable(){
						public void run()
						{
					try{
						
					URL url = new URL("http://117.218.100.45:8080/feedback/givefeedback");
					   HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					   connection.setDoOutput(true);
					   connection.setConnectTimeout(10000);
					   connection.setReadTimeout(10000);
					   
					   ObjectOutputStream out=new ObjectOutputStream(connection.getOutputStream());
						
						out.writeObject(se);
						out.flush();	
						int respcode=connection.getResponseCode();
			 	        
						   if(respcode==200)
						   {
							   Message msg=handler1.obtainMessage();
							    msg.what=1;
							 	    handler1.sendMessage(msg);			  
							   
							    out.close();
						   }
						   else if(respcode==401)
						   {
							   //FOR FUTURE USE
						   }
						   }
			catch(Exception e)
			{
				Message msg=handler1.obtainMessage();
			    msg.what=0;
			    msg.obj=e.toString();
			 	    handler1.sendMessage(msg);	
			///Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			}
						}
					});
				runn.start();
				
				}
			}
			});

		handler1=new Handler()
		  {
		   public void handleMessage(Message msg)
		   {
			   
		   if(msg.what==1)
		     {
		      pb.setVisibility(8);
		      Toast.makeText(getApplicationContext(),"Feedback Sent Successfuly!", Toast.LENGTH_LONG).show(); 	
		     }
		   else if(msg.what==0)
		   {
			   pb.setVisibility(8);
			      Toast.makeText(getApplicationContext(),(String)msg.obj, Toast.LENGTH_LONG).show(); 	 
		   }
		     
		    }
		   };
	
}
}
