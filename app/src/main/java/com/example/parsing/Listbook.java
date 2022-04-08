package com.example.parsing;

import java.net.URL;

public class Listbook {
    URL url;
    String title;
    public Listbook(String titlesite,URL urlsite)
    {
       url=urlsite;
       title=titlesite;
    }
    public  void setUrl(URL urlsite)
    {
        url=urlsite;
    }
    public void setTitle(String titlesite)
    {
        title=titlesite;
    }
    public  URL getUrl()
    {
        return url;
    }
    public String getTitle()
    {
        return title;
    }
}
