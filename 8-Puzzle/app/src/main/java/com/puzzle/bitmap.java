package com.puzzle;

import com.droid8puzzle.R;

public class bitmap {
	private final String name;
	private float x,y;//,x_prev,y_prev;
	private int this_pos;
	private int pos_prev;
	public bitmap(String name,float x,float y,int this_pos)
	{
		this.name=name;
		this.x=x;
		this.y=y;
		this.this_pos=this_pos;
		//this.x_prev=-1;
		//this.y_prev=-1;
		this.pos_prev=-1;
	}
	public float get_x()
	{
		return this.x;
	}
	public float get_y()
	{
		return this.y;
	}
	/*public float get_x_prev()
	{
		return this.x_prev;
	}
	public float get_y_prev()
	{
		return this.y_prev;
	}*/
	public String get_name()
	{
		return this.name;
	}
	public int get_this_pos()
	{
		return this.this_pos;
	}
	public int get_pos_prev()
	{
		return this.pos_prev;
	}
	public void set_xy(float x,float y)
	{
		this.x=x;
		this.y=y;
	}
	public void change(float x,float y,int this_pos)
	{
		//this.x_prev=this.x;
		//this.y_prev=this.y;
		this.x=x;
		this.y=y;
		this.pos_prev=this.this_pos;
		this.this_pos=this_pos;
		
	}
}