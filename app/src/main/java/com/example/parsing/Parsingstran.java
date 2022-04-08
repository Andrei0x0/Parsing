package com.example.parsing;
import android.content.SharedPreferences;

import android.webkit.JavascriptInterface;
import android.webkit.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parsingstran {
    ArrayList<String> html;
    ArrayList<Stranica> stran;
    String getHTML;
    String geturl;
    String urllink;
    int proc;
    public class Stranica  {
        String url;
        String iturl;
        String name;
        String glava;
        ArrayList<String> abzac;
        public Stranica()
        {
            url=null;
            iturl=null;
            name="";
            glava="";
            abzac=new ArrayList<String>();
        }
        public Thread SaveData(SharedPreferences spers,String s)
        {
            Savestran SS=new Savestran(spers,s,url,iturl,name,glava,abzac);
            Thread thread = new Thread(SS, "Поток парсинга страниц");
            thread.start();
            return thread;
        }
        public void GetData(SharedPreferences spers,String s)
        {
            glava=spers.getString(s+"glava",null);
            abzac=getArrayS(spers, s+glava.replace("\n","").replace(" ","")+"arrayabzac");
            url=spers.getString(s+"url",null);
            iturl=spers.getString(s+"iturl",null);
            name=spers.getString(s+"name",null);
        }
                    }

    public Parsingstran() {
        stran=new  ArrayList<Stranica>();
        html=new ArrayList<String>();
        getHTML=null;
        proc=0;
    }

    public  void parsHTML(String url, WebView webviver) {
        geturl=url;
        class MyJavaScriptInterface
        {
            @JavascriptInterface
            @SuppressWarnings("unused")
            public  void  processHTML(String gethtml)
            {
             if(gethtml.length()>40)
                getHTML=gethtml;
            }
        }
        MyJavaScriptInterface myjava=new MyJavaScriptInterface();
        WebView browser = (WebView)webviver;
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setSaveFormData(true);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setUserAgentString("Chrome/41.0.2228.0 Safari/537.36");
        browser.addJavascriptInterface(myjava, "HTMLOUT");
        browser.setWebChromeClient(new WebChromeClient(){
           public void onProgressChanged(WebView view, int newProgress)
           {
               proc=newProgress;
               view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('html')[0].innerHTML);");
           }

        });
        browser.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                urllink=url;
                view.loadUrl(url);
                return true;
            }
        });
          browser.loadUrl(url);
    }

    public void ParsePage(Structura struc,Element glava,Element abzac,Element link,Element book) {
            Document doc = Jsoup.parse(getHTML);
            Stranica s = new Stranica();
            String url=geturl;
            for (String string : struc.poisc(abzac, doc,url).split("\\\\n"))
            {
                if(string.replace("\\n","").trim().length()>2)
                s.abzac.add(string);
            }
            if(s.abzac.size()==0)
            {
                for (String string : struc.poisc(abzac, doc,url).split("\\\\n"))
                {
                    if(string.replace("\\n","").trim().length()>2)
                        s.abzac.add(string);
                }
            }

            s.iturl = url;
            s.glava = struc.poisc(glava, doc,url);
            s.name = struc.poisc(book, doc,url);
            if (struc.poisc(link).size() > 0) {
                List<String> listlink=struc.poisc(link);
                for (String s23:listlink)
                {
                    Elements linking = doc.select(s23);
                     if (linking.size() > 0)
                    {
                        for (Element lin : linking.get(0).getElementsByTag("a"))
                        {
                            s.url = lin.attr("href");
                            break;
                        }
                    }
                }
                if ( s.iturl.length()>2&&s.glava.length()>2&&s.name.length()>2 && s.abzac.size()!=0)
                    if (!getmaxstran().equals(s)&&!stran.contains(s) )
                    {
                        html.add(getHTML);
                        stran.add(s);
                    }
                else
                    {
                        if (s.iturl == getmaxstran().iturl && getmaxhtml().length() < getHTML.length()) {
                            stran.set(getmaxstranI(), s);
                            html.set(getmaxhtmlI(), getHTML);
                        }
                    }
            }
            Sortstran();
    }

    public Stranica getmaxstran()
    {
        if(stran.size()>0)
        return stran.get(stran.size()-1);
        else {
            Stranica s=new Stranica();
            return s;
        }
    }
    public String getmaxhtml()
    {
        if(html.size()>0)
            return html.get(html.size()-1);
        else
            return null;
    }
    public int getmaxstranI()
    {
        if(stran.size()>0)
            return stran.size()-1;
        else
            return 0;
    }
    public int getmaxhtmlI()
    {
        if(html.size()>0)
            return html.size()-1;
        else
            return 0;
    }
    public  ArrayList<String> getarrayname()
    {
        ArrayList<String> strings=new  ArrayList<String>();
        for(int i=0;i<stran.size();i++)
        {
            strings.add(stran.get(i).glava);
        }
        return strings;
    }
    public Thread SaveData(SharedPreferences spers,String s)
    {
        Savepars MR=new Savepars(spers,s,stran,html,getHTML);
        Thread thread = new Thread(MR, "Сохранение парсинга");
        thread.start();
        return thread;
    }
    public void GetData(SharedPreferences spers,String s)
    {
        stran.clear();
        int size;
        html=getArrayS(spers,s+"arrayhtml");
        if(html.size()>0)
        getHTML=html.get(html.size()-1);
        size=spers.getInt(s+"sizestran",0);
        for(int i=stran.size();i<size;i++)
        {
            Stranica sv=new Stranica();
            sv.GetData(spers,s+"stran"+i);
            if(!stran.contains(sv))
            stran.add(i,sv);
        }
    }
    private ArrayList<String> getArrayS(SharedPreferences ShPef,String s)
    {
           int size=ShPef.getInt(s+"size",0);
           ArrayList<String> array=new ArrayList<>();
            if(size>0) {
            for (int i =0; i < size; i++) {
                array.add(ShPef.getString(s+i,null));
            }
        }
    return array;
    }
    public void Sortstran()
    {
        if(stran.size()>1)
        for(int i=1;i<stran.size();i++)
        {
                if(stran.get(i).glava.equals(stran.get(i-1).glava))
                {
                    if(stran.get(i).abzac.size()>stran.get(i-1).abzac.size())
                    {
                        stran.remove(i-1);
                    }
                    else
                    {
                        stran.remove(i);
                    }
                }
        }
    }
    public void remove(int index)
    {
        html.remove(index);
        stran.remove(index);
    }
}
