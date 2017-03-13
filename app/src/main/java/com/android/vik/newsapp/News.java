package com.android.vik.newsapp;

public class News {

    private String mTitle;
    private String mThumbNailURL;
    private String mWebUrl;


    public News(String ThumbNailURL, String title, String WebUrl) {

        mTitle = title;
        mThumbNailURL = ThumbNailURL;
        mWebUrl = WebUrl;

    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbNailURL() {
        return mThumbNailURL;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

}
