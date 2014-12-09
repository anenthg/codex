package com.puzzle;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.droid8puzzle.R;

public class my_adapter extends ArrayAdapter<String> 
{
 private final Context context;
 private final String[] values;
 private final int pos;
 public my_adapter(Context context,String[] values,int pos)
 {
  super(context,R.layout.list_item,values);
  this.context=context;
  this.values=values;
  this.pos=pos;
 }
 
 @Override
 public View getView(int position,View convert_view,ViewGroup parent)
 {
  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  View rowView = inflater.inflate(R.layout.list_item, parent, false);
  TextView textView = (TextView) rowView.findViewById(R.id.tv1);
  textView.setText(values[position]);
  for(int i=0;i<values.length;i++)
  {
   if(pos==position)
   {
	 textView.setTextColor(Color.YELLOW);
	 break;
   }
  }
  
  return  rowView;
 }
}
