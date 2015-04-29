package caching;

import it.sauronsoftware.feed4j.FeedIOException;
import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.FeedXMLParseException;
import it.sauronsoftware.feed4j.UnsupportedFeedException;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by rushabhmehta91 on 4/29/15.
 */
public class RSSReader {
    public static ArrayList<String> readRSS(String newsURL) {
        ArrayList<String> title = new ArrayList<String>();
        try {
            URL newsRead = new URL(newsURL);
            Feed feed = FeedParser.parse(newsRead);
            int items = feed.getItemCount();
            for (int i = 0; i < items; i++) {
                FeedItem item = feed.getItem(i);
                title.add(item.getTitle());
                System.out.println(item.getTitle());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FeedXMLParseException e) {
            e.printStackTrace();
        } catch (UnsupportedFeedException e) {
            e.printStackTrace();
        } catch (FeedIOException e) {
            e.printStackTrace();
        }
        return title;

    }

    public static void main(String[] args) {
        readRSS("http://www.rit.edu/news/lib/rss/topstories.rss");
    }


}
