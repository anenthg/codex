package com.rss.ashwin.myyc.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rss.ashwin.myyc.IndividualNews;
import com.rss.ashwin.myyc.R;
import com.rss.ashwin.myyc.dataobjects.RSSListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashwin on 7/12/14.
 */
public class RSSListItemAdapter extends ArrayAdapter<RSSListItem> {
   private Context context;
    private List<RSSListItem> items;
    private Typeface tf;

   public RSSListItemAdapter(Context context, int resource,ArrayList<RSSListItem> items) {
        super(context, resource, items);
        this.context=context;
       this.items=items;
       tf=Typeface.createFromAsset(context.getAssets(), "fonts/National Cartoon.ttf");
    }

    public static class ViewHolder {
        public TextView title,description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //To enable reusing the views, I am checking if convertView is null
        //inflating  is costly operation and by checking if convertView is null each time
        //we can avoid calls to inflating method
        if(convertView==null)
        {
            //inflate listItem and create new view
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_rss, null);

            //Assign the views of the listitem to static class view holder
            //this way calls to findViewByID can also be avoided
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title=(TextView)convertView.findViewById(R.id.title);
            viewHolder.description=(TextView)convertView.findViewById(R.id.description);

            //Set ViewHolder object as tag for convertView
            convertView.setTag(viewHolder);
        }
        ViewHolder holder=(ViewHolder) convertView.getTag();
        String title=items.get(position).getTitle();
        String description=items.get(position).getDescription();
        holder.title.setText(title);
        if(description.length()>140)
        {
            description=description.substring(0,139)+"...";
        }
        holder.description.setText(description);

        //Set the font type

       // holder.title.setTypeface(tf);

        final int pos=position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,IndividualNews.class);
                intent.putExtra("item",items.get(pos));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
