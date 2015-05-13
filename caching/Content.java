package caching;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by rushabhmehta91 on 4/6/15.
 */
public class Content implements Serializable{
    public HashMap<String, Integer> listofScoreOnInterfaces;
    private String contentName;
    private int maxNScore;
    private int timeToLive;
    public List<String> trail;
    private long sizeInBytes;
    private Date lastUsed;
    private Object contentCache;

    // constructors start
    public Content(String contentName, List<String> trail, long sizeInBytes, Object contentCache) {
        this.contentName = contentName;
//        this.lastUsed = now;
        this.contentCache = contentCache;
        this.maxNScore = 3;
        this.timeToLive = 999999;
        this.trail = trail;
        this.sizeInBytes = sizeInBytes;
        listofScoreOnInterfaces = new HashMap<String, Integer>();
    }

    public Content(String contentName, int maxNScore, int timeToLive, List<String> trail, long sizeInBytes, Object contentCache) {
        this.contentName = contentName;
        this.maxNScore = maxNScore;
        this.timeToLive = timeToLive;
        this.trail = trail;
        this.sizeInBytes = sizeInBytes;
//        this.lastUsed = lastUsed;
        this.contentCache = contentCache;
        listofScoreOnInterfaces = new HashMap<String, Integer>();
    }
    // constructors ends

    //getter and setters starts

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public int getMaxNScore() {
        return maxNScore;
    }

    public void setMaxNScore(int maxNScore) {
        this.maxNScore = maxNScore;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public HashMap<String, Integer> getListofScoreOnInterfaces() {
        return listofScoreOnInterfaces;
    }

    public void setListofScoreOnInterfaces(HashMap<String, Integer> listofScoreOnInterfaces) {
        this.listofScoreOnInterfaces = listofScoreOnInterfaces;
    }

    public List<String> getTrail() {
        return trail;
    }

    public void setTrail(List<String> trail) {
        this.trail = trail;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public Object getContentCache() {
        return contentCache;
    }

    public void setContentCache(Object contentCache) {
        this.contentCache = contentCache;
    }


    //getter and setters ends

}
