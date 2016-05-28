package com.example.picturemanagement.app.widget;

/**
 * Created by mycomputer on 2016/5/28.
 */
public class getForListview {

    private String sign;
    private String bitmapUrl;
    private String Url;
    public getForListview(String bitmapUrl,String sign,String Url)
    {
        this.bitmapUrl = bitmapUrl;
        this.sign = sign;
        this.Url = Url;
    }

    public String getBitmapURl()
    {
        return bitmapUrl;
    }

    public String getsign()
    {
        return sign;
    }

    public String getUrl()
    {
        return Url;
    }
}
