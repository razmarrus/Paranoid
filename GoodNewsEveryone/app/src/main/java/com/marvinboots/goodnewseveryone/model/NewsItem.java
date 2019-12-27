package com.marvinboots.goodnewseveryone.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "newsItems")
public class NewsItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "title")
    private String newsItemTitle;
    @ColumnInfo(name = "description")
    private String newsItemDescription;
    @ColumnInfo(name = "date")
    private String newsItemPubDate;
    @ColumnInfo(name = "image")
    private String newsItemImage;
    @ColumnInfo(name = "origLink")
    private String newsOrigLink;

    public NewsItem(String newsItemTitle,String newsItemDescription,
                    String newsItemPubDate,String newsOrigLink, String newsItemImage) {
        this.newsItemTitle = newsItemTitle;
        this.newsItemDescription = newsItemDescription;
        this.newsItemPubDate = newsItemPubDate;
        this.newsItemImage = newsItemImage;
        this.newsOrigLink = newsOrigLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", newsItemPubDate=" + newsItemPubDate +
                '}';
    }

    public String getNewsItemTitle() {
        return newsItemTitle;
    }

    public void setNewsItemTitle(String newsItemTitle) {
        this.newsItemTitle = newsItemTitle;
    }


    public String getNewsItemDescription() {
        return newsItemDescription;
    }

    public void setNewsItemDescription(String newsItemDescription) {
        this.newsItemDescription = newsItemDescription;
    }

    public String getNewsItemPubDate() {
        return newsItemPubDate;
    }

    public void setNewsItemPubDate(String newsItemPubDate) {
        this.newsItemPubDate = newsItemPubDate;
    }

    public String getNewsItemImage() {
        return newsItemImage;
    }

    public void setNewsItemImage(String newsItemImage) {
        this.newsItemImage = newsItemImage;
    }


    public void setOrigLink(String newsOrigLink) {
        this.newsOrigLink = newsOrigLink;
    }


    public String getNewsOrigLink() {
        return newsOrigLink;
    }
}
