package com.example.picturemanagement.app.domain;

import java.io.File;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class pictureData {

    private String url;
    private long time;
    public pictureData(String url)
    {
        this.url = url;
        time = new File(this.url).lastModified();
    }

    public String getUrl()
    {
        return url;
    }

    public long getTime()
    {
        return time;
    }

}
