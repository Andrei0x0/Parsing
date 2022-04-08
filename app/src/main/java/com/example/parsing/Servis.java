package com.example.parsing;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.support.annotation.Nullable;

public class Servis extends Service {
    Context activ=this;
    Element eglava, eabzac, elink, ebook;
    SharedPreferences Pref ;
    Structura struc;
    TegList tegList=new TegList();
    WebView webView;
    String url,tbook,namebook;
    Parsingstran pars=new Parsingstran();
    Resources res;
    PendingIntent contentIntent;
    NotificationManager notificationManager;
    Thread threadsave=new Thread();
    int tak=0,tik=0;
    Runnable Rb;
    String procces="Страница:0%";
    final Handler handler = new Handler();
    private LayoutInflater mLayoutInflater;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Служба создана",
                Toast.LENGTH_SHORT).show();
        mLayoutInflater = LayoutInflater.from(this.getApplicationContext());
        View convertView = mLayoutInflater.inflate(R.layout.webview, null);
        webView=convertView.findViewById(R.id.WV);

    }
    @Override
    public int onStartCommand( Intent intent, int flags, int startId) {
       final int NOTIFY_ID=startId;
        Toast.makeText(this, "Служба запущена", Toast.LENGTH_SHORT).show();
        Intent notificationIntent = new Intent(activ, MainActivity.class);
        contentIntent = PendingIntent.getActivity(activ, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        res = activ.getResources();
        final Notification.Builder builder =new Notification.Builder(activ);
        startForeground(NOTIFY_ID,builder.build());
        if (namebook == null) {
            builder.setContentIntent(contentIntent);
            // обязательные настройки
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
            builder.setContentTitle("Загрузка");
            //.setContentText(res.getString(R.string.notifytext))
            builder.setContentText(""); // Текст уведомления
            // необязательные настройки
            builder.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground)); // большая
            // картинка
            builder.setWhen(System.currentTimeMillis());
            builder.setAutoCancel(false); // автоматически закрыть уведомление после нажатия
            notificationManager = (NotificationManager) activ.getSystemService(Context.NOTIFICATION_SERVICE);
            // Альтернативный вариант
            // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFY_ID, builder.build());
            int book, glava, abzac, link;
            namebook = intent.getStringExtra("Text");
            Pref = getSharedPreferences(namebook, MODE_PRIVATE);
            pars.GetData(Pref, namebook);
            String html = pars.html.get(0);
            generateSomeHierarchy(html);
            url = pars.stran.get(0).iturl;
            book = tegList.getindex(Pref.getString("book", null));
            glava = tegList.getindex(Pref.getString("glava", null));
            abzac = tegList.getindex(Pref.getString("abzac", null));
            link = tegList.getindex(Pref.getString("link", null));
            eglava = tegList.teg.get(glava);
            eabzac = tegList.teg.get(abzac);
            elink = tegList.teg.get(link);
            ebook = tegList.teg.get(book);
            tbook = tegList.Text.get(book);
            pars.parsHTML(pars.getmaxstran().iturl, webView);
        }
        if ((flags & START_FLAG_RETRY) != 0)
        {
            Toast.makeText(this, "Служба уже запущена", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Rb=new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        if (pars.proc > 0) {
                            builder.setContentTitle("Страница:" + pars.proc + "%");
                        }
                        tak++;
                        if ((pars.proc >= 90 || tak > (100 - pars.proc))) {
                            tak = 0;
                            pars.ParsePage(struc, eglava, eabzac, elink, ebook);
                            if (pars.stran.size() > 0) {
                                if (pars.getmaxstran().url != null) {
                                    pars.parsHTML(pars.getmaxstran().url, webView);
                                } else {
                                    pars.proc = 0;
                                    builder.setContentTitle("Автосохранение");
                                    threadsave.join();
                                    threadsave = pars.SaveData(Pref, tbook);
                                    threadsave.join();
                                    stopForeground (true);
                                    stopSelf();
                                }
                                if (!threadsave.isAlive()) {
                                    tik++;
                                    if(tik>=2)
                                    {
                                        tik=0;
                                        builder.setContentTitle("сохранение");
                                        threadsave = pars.SaveData(Pref, tbook);
                                        threadsave.join();
                                    }
                                }
                            }
                        }
                        builder.setContentText(pars.getmaxstran().glava);
                        notificationManager = (NotificationManager) activ.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(NOTIFY_ID, builder.build());
                        handler.postDelayed(this, 1000);
                    } catch (Exception e) {
                         handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.postDelayed(Rb, 1000);
        }
        return Service.START_REDELIVER_INTENT;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Служба остановлена", Toast.LENGTH_SHORT).show();

        if(notificationManager!=null)
             notificationManager.cancelAll();
             try {
                 if(threadsave!=null)
                 threadsave.join();
             }catch (Exception e){}
        stopForeground (true);
             handler.removeCallbacks(Rb);
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
}
