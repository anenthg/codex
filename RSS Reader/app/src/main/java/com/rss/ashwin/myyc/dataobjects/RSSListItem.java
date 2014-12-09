package com.rss.ashwin.myyc.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ashwin on 7/12/14.
 */
public class RSSListItem implements Serializable{
    private String title, author, category, link, description,string_pubDate;
    private Date date_pubDate;

    public RSSListItem(String title, String author, String category, String link, String description, String string_pubDate) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.link = link;
        this.description = description;
        this.string_pubDate = string_pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getString_pubDate() {
        return string_pubDate;
    }

    public Date getDate_pubDate() {
        return date_pubDate;
    }
}
