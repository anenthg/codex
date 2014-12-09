package com.rss.ashwin.myyc.reusables;

/**
 * Created by ashwin on 7/12/14.
 */


        import android.os.AsyncTask;
        import android.os.Handler;
        import android.os.Message;


        import com.rss.ashwin.myyc.dataobjects.RSSListItem;

        import org.jsoup.Connection;
        import org.jsoup.HttpStatusException;
        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;
        import org.jsoup.nodes.Element;
        import org.jsoup.parser.Parser;
        import org.jsoup.select.Elements;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

public class GetRSSList extends AsyncTask<Void,Void, List<RSSListItem>>
{
    private final String TAG=GetRSSList.class.getSimpleName();
    private static final String RSSLINK="http://www.surfnetkids.com/reader/feed/";
    private Handler handler;

    public GetRSSList(Handler handler)
    {
        this.handler=handler;
    }


    @Override
    protected List<RSSListItem> doInBackground(Void...voids)  {
        //Set the progressBar
        publishProgress();
        Connection connection= Jsoup.connect(RSSLINK).ignoreContentType(true).parser(Parser.xmlParser());
        Document doc;
        try{
            doc=connection.get();
        }
        catch(HttpStatusException e1)
        {
            return null;
        }
        catch(IOException e)
        {
            return null;
        }
        List<RSSListItem> rssList=new ArrayList<RSSListItem>();
        Elements items=doc.getElementsByTag("item");
        for(Element item:items)
        {
            String title=Jsoup.parse(item.getElementsByTag("title").text()).text();
            String author=Jsoup.parse(item.getElementsByTag("author").text()).text();
            String category=Jsoup.parse(item.getElementsByTag("category").text()).text();
            String link=Jsoup.parse(item.getElementsByTag("link").text()).text();
            String description=Jsoup.parse(item.getElementsByTag("description").text()).text();
            String string_pubDate=Jsoup.parse(item.getElementsByTag("pubDate").text()).text();

            RSSListItem listItem=new RSSListItem(title,author,category,link,description,string_pubDate);
            rssList.add(listItem);
        }
        return rssList;

    }

    @Override
    protected void onPostExecute(List<RSSListItem> listItems) {
        Message message=Message.obtain();
        if(listItems==null)
        {
            message.obj=null;
            message.what=0; //Zero indicates that an exception occured
        }
        else
        {
            message.obj=listItems;
            message.what=2; //two indicates that the rss list was fetched successfully
        }

        handler.dispatchMessage(message);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Message message=Message.obtain();
        message.what=1; //One indicates that the progressbar has to be set to Visible
        handler.dispatchMessage(message);
    }
}
