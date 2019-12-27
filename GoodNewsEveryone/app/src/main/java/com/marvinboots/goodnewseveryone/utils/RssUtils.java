package com.marvinboots.goodnewseveryone.utils;

import com.marvinboots.goodnewseveryone.model.NewsItem;
import com.prof.rssparser.Article;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RssUtils {

    public static NewsItem articleToNewsItem(Article article) {
        String newsDescription = article.getDescription();
        Document html = Jsoup.parse(newsDescription);
        newsDescription = html.body().text();
        NewsItem newsItem = new NewsItem(
                article.getTitle(),
                newsDescription,
                article.getPubDate().toString(),
                article.getImage(), "");
        return newsItem;
    }

}
