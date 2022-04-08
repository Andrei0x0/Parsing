package com.example.parsing;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.jsoup.nodes.Element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class RunServerParsing implements Runnable {
    Element eglava, eabzac, elink, ebook;
    SharedPreferences Pref ;
    Structura struc;
    TegList tegList=new TegList();
    WebView webV;
    String url,tbook;
    Parsingstran pars=new Parsingstran();
    Resources res;
    PendingIntent contentIntent;
    NotificationManager notificationManager;
    Thread threadsave=new Thread();

    int tak=0,timer=0;
    Context activ;

    public RunServerParsing(SharedPreferences SP,Context context,String nb,WebView WV)
    {
        activ=context;
        Intent notificationIntent = new Intent(activ, Parsing.class);
        contentIntent = PendingIntent.getActivity(activ, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        res = activ.getResources();
        int book, glava, abzac, link;
        Pref=SP;

        activ=context;
        String namebook=nb;
        pars.GetData(Pref, namebook);
        String html = pars.html.get(0);
        generateSomeHierarchy(html);
        book = tegList.getindex(Pref.getString("book", null));
        glava = tegList.getindex(Pref.getString("glava", null));
        abzac = tegList.getindex(Pref.getString("abzac", null));
        link = tegList.getindex(Pref.getString("link", null));
        eglava = tegList.teg.get(glava);
        eabzac = tegList.teg.get(abzac);
        elink = tegList.teg.get(link);
        ebook = tegList.teg.get(book);
        tbook = tegList.Text.get(book);
        webV=WV;


    }
    @Override
    public void run() {
        String procces="Страница:0%";
        timer++;
        try {
            if(pars.proc==0)
           pars.parsHTML(pars.getmaxstran().iturl,webV);
            Thread.sleep(1000);
            if (pars.proc > 0)
                procces = "Страница:" + pars.proc + "%";
            tak++;
            if ((pars.proc >= 90 || tak > (100 - pars.proc))) {
                tak = 0;
                pars.ParsePage(struc, eglava, eabzac, elink, ebook);
                if (pars.stran.size() > 0) {
                    if (pars.getmaxstran().url != null) {
                        pars.parsHTML(pars.getmaxstran().url, webV);
                    } else {
                        pars.proc = 0;
                        threadsave.join();
                        threadsave = pars.SaveData(Pref, tbook);
                        threadsave.join();
                        timer=-1;
                        return;
                    }
                    if (!threadsave.isAlive()) {
                        // Showtoast("Автосохранение");
                        threadsave = pars.SaveData(Pref, tbook);
                    }
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activ);
            builder.setContentIntent(contentIntent);
            // обязательные настройки
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
            builder.setContentTitle(procces);
            //.setContentText(res.getString(R.string.notifytext))
            builder.setContentText(pars.getmaxstran().glava); // Текст уведомления
            // необязательные настройки
            builder.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground)); // большая
            // картинка
            builder.setWhen(System.currentTimeMillis());
            builder.setAutoCancel(false); // автоматически закрыть уведомление после нажатия
            notificationManager = (NotificationManager) activ.getSystemService(Context.NOTIFICATION_SERVICE);
            // Альтернативный вариант
            // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
            run();
        } catch (Exception e) {
            String string = e.toString();
            run();
        }
    }
    private void generateSomeHierarchy(String HTML) {
        Document doc = Jsoup.parse(HTML);
        Elements elelist = doc.children();
        elelist=elelist.get(0).children();
        struc=new Structura(doc.createElement("html"));
        tegList.clear();
        recurseOverElements(elelist,struc);
        tegList.sort();
    }
    public  void recurseOverElements(Elements elementList,Structura structura) {
        if(elementList.size()>0)
            for(Element element:elementList) {
                if ((element.text().length() > 0)) {
                    tegList.Text.add(element.text());
                    tegList.teg.add(element);
                }
                Structura s = new Structura(element);
                structura.addchild(s);
                recurseOverElements(element.children(), s);
            }
    }
    public  void parsHTML(String url,WebView web) {
        pars.geturl=url;

        class MyJavaScriptInterface {
            @JavascriptInterface
            @SuppressWarnings("unused")
            public  void  processHTML(String gethtml)
            {

                pars.getHTML=gethtml;

            }
        }
        MyJavaScriptInterface myjava=new MyJavaScriptInterface();

        final WebView browser=(WebView)web;
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setSaveFormData(false);
        browser.getSettings().setLoadsImagesAutomatically(false);
        browser.getSettings().setUserAgentString("Chrome/41.0.2228.0 Safari/537.36");
        browser.addJavascriptInterface(myjava, "HTMLOUT");

        browser.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress)
            {
                pars.proc=newProgress;
                browser.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('html')[0].innerHTML);");
            }

        });
        browser.loadUrl(url);

    }
}
