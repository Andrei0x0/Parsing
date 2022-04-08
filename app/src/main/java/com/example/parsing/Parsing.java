package com.example.parsing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parsing extends AppCompatActivity {
Parsingstran pars=new Parsingstran();
    Element eglava, eabzac, elink, ebook;
    TabHost tabHost;
    TegList tegList=new TegList();
    EditText editText,editText2,editText3,editText4;
    TextView textView,textView2,textView3,textView4,textView5,procces;
    ListView lv;
    Button but3,but5;
    String url,tbook;
    final Handler handler = new Handler();
    Runnable Rb;
    int tak=0;
    Structura struc;
    int book,glava,abzac,link;
    WebView webView;
    SharedPreferences  Pref ;
    boolean parsing=false;
    static Context cont;
    Thread thread=new Thread();

    @Override
    public void onPause()
    {
       try {
           if(parsing==true)
           {
               parsing=false;
               but3.setText("старт");
            //   startService(new Intent(Parsing.this, Servis.class).putExtra("Text",tbook));
           }
       }
       catch (Exception e)
       {
        String s=e.toString();
       }
         super.onPause();
    }
    public void  onDestroy()
    {
        try {
            Thread td=  pars.SaveData(Pref, tegList.Text.get(book));
            td.setPriority(10);
            td.join();
        }
        catch (Exception e)
        {
            String s=e.toString();
        }
        super.onDestroy();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        try {
         //   stopService(new Intent(this, Servis.class));
            Pref=getSharedPreferences(tbook,MODE_PRIVATE);
            pars.GetData(Pref,tbook);
            setAdapter();
        }catch (Exception e){};
    }
    @Override
    public void onBackPressed()
    {
    finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing);
        cont=getApplicationContext();
        editText=findViewById(R.id.editText6);
        editText2=findViewById(R.id.editText5);
        editText3=findViewById(R.id.editText4);
        editText4=findViewById(R.id.editText3);
        textView=findViewById(R.id.textView4);
        textView2=findViewById(R.id.textView3);
        textView3=findViewById(R.id.textView2);
        textView4=findViewById(R.id.textView);
        textView5=findViewById(R.id.textView5);
        procces=findViewById(R.id.textView6);
        webView=findViewById(R.id.webviewpars);
        lv=findViewById(R.id.Listcan);
        but3=findViewById(R.id.button3);
        but5=findViewById(R.id.button5);
        final ProgressBar PB=findViewById(R.id.progressBar);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        Rb=new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    PB.setProgress(pars.proc);
                    if(pars.proc>0)
                        procces.setText("Прогресс загрузки страницы:"+pars.proc+",до добовления"+(100-pars.proc-tak)+" с.");
                    if(pars.stran.size()==0)
                        but3.setEnabled(false);
                    else
                        but3.setEnabled(true);
                    tak++;
                    //||tak>(100-pars.proc))&&(parsing|| pars.stran.size()==0)
                    if (pars.proc >= 100 && (parsing || pars.stran.size()==0))
                    {
                        tak=0;

                            pars.ParsePage(struc, eglava, eabzac, elink, ebook);
                            if (pars.stran.size() > 0) {
                                if (tabHost.getCurrentTab() != 1)
                                    setAdapter();

                                if (pars.getmaxstran().url != null) {
                                    if(pars.getHTML.length()>40) {
                                        Parshtml();
                                    }
                                    else
                                    {
                                        procces.setText("ошибка парсинга");
                                        but3.setText("старт");
                                        parsing=false;
                                    }
                                } else {
                                    pars.proc = 0;
                                    thread = pars.SaveData(Pref,tbook);
                                    thread.join();
                                }
                                if(!thread.isAlive())
                                {
                                    procces.setText("Автосохранение");
                                    thread = pars.SaveData(Pref,tbook);

                                }
                            }
                    }
                handler.postDelayed(this, 1000);
                }
                catch (Exception e)
                {
                    String string=e.toString();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        Intent intent = getIntent();
        boolean flag;
        flag= intent.getBooleanExtra("neznay",false);

        if(flag)
        {
            String html=intent.getStringExtra("html");
            generateSomeHierarchy(html);

            url=intent.getStringExtra("url");
            book= intent.getIntExtra("namebook",0);
            glava= intent.getIntExtra("nameglav",0);
            abzac= intent.getIntExtra("nameabzac",0);
            link= intent.getIntExtra("namelink",0);

            String namebook=tegList.Text.get(book);
            Pref=getSharedPreferences(namebook,MODE_PRIVATE);
            SharedPreferences SP=getSharedPreferences("listbook",MODE_PRIVATE);
            ArrayList<String> listbook=new ArrayList<>();
            listbook=getArrayS(SP,"listbook");
            if(!listbook.contains(namebook))
            {
                listbook.add(namebook);
            }
            SharedPreferences.Editor edl=SP.edit();
            edl= setArrayS(edl,listbook,"listbook");
            edl.commit();
            SharedPreferences.Editor ed=Pref.edit();
            ed.putString("book",tegList.Text.get(book));
            ed.putString("glava",tegList.Text.get(glava));
            ed.putString("abzac",tegList.Text.get(abzac));
            ed.putString("link",tegList.Text.get(link));
            ed.commit();

            editText.setText(tegList.Text.get(book));
            textView.setText(tegList.getteg(book));
            editText2.setText(tegList.Text.get(glava));
            textView2.setText(tegList.getteg(glava));
            editText3.setText(tegList.Text.get(abzac));
            textView3.setText(tegList.getteg(abzac));
            editText4.setText(tegList.Text.get(link));
            textView4.setText(tegList.getteg(link));
            pars.parsHTML(url,webView);
        }
        else
        {
            String namebook=intent.getStringExtra("Text");
            Pref=getSharedPreferences(namebook,MODE_PRIVATE);
            pars.GetData(Pref,namebook);
            pars.Sortstran();
            String html=pars.html.get(0);
            generateSomeHierarchy(html);
            url=pars.stran.get(0).iturl;
            book= tegList.getindex(Pref.getString("book",null));
            glava= tegList.getindex(Pref.getString("glava",null));
            abzac=tegList.getindex(Pref.getString("abzac",null));
            link= tegList.getindex(Pref.getString("link",null));
            editText.setText(tegList.Text.get(book));
            textView.setText(tegList.getteg(book));
            editText2.setText(tegList.Text.get(glava));
            textView2.setText(tegList.getteg(glava));
            editText3.setText(tegList.Text.get(abzac));
            textView3.setText(tegList.getteg(abzac));
            editText4.setText(tegList.Text.get(link));
            textView4.setText(tegList.getteg(link));
            setAdapter();
            textView5.setText("Получено глав:"+pars.stran.size()+",последняя:"+pars.getmaxstran().glava);

        }
        tabHost = (TabHost)findViewById(R.id.tabHost2);

        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Парсинг");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Полученные");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);
        eglava = tegList.teg.get(glava);
        eabzac = tegList.teg.get(abzac);
        elink = tegList.teg.get(link);
        ebook = tegList.teg.get(book);
        tbook=tegList.Text.get(book);
        Pref=getSharedPreferences(tbook,MODE_PRIVATE);
        try {
            stopService(new Intent(this, Servis.class).putExtra("Text", tbook));
        }catch (Exception e){};
        handler.postDelayed( Rb, 10);
    }
    public void Start(View view)
    {
        Button but=(Button)view;
        if(but.getText().equals("старт")) {
            but.setText("стоп");
            parsing = true;
            if ((pars.stran.size() > 0) && (pars.getmaxstran().url != null)) {
                if(pars.getmaxstran().url.length()>10)
                pars.parsHTML(pars.getmaxstran().url, webView);
                else
                pars.parsHTML(pars.getmaxstran().iturl, webView);
            } else {
                if(pars.stran.size() > 0)
                    pars.parsHTML(pars.getmaxstran().iturl, webView);
                    else
                pars.parsHTML(url, webView);
            }

        }
        else
        {
            parsing=false;
            but.setText("старт");
            thread = pars.SaveData(Pref,tbook);
        }
    }
    public  void setAdapter() {

    ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,pars.getarrayname());
        final Deletelist dellist=new Deletelist(this,pars.getarrayname(),new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(view.getContentDescription() == null&&pars.stran.size()!=1)return;
                int i = Integer.parseInt(view.getContentDescription().toString());
                pars.remove(i);
                setAdapter();
            }
        });
    lv.setAdapter(dellist);

        textView5.setText("Получено глав:" + pars.stran.size() + ",последняя:" + pars.getmaxstran().glava);
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
    public  void recurseOverElements(Elements elementList,Structura structura){
        if(elementList.size()>0)
            for(Element element:elementList)
            {
                if (element.tagName().equals("a"))
                {
                    tegList.Text.add(element.attr("href"));
                    tegList.teg.add(element);
                }
                if ((element.text().length() > 0))
                {
                    tegList.Text.add(element.text());
                    tegList.teg.add(element);
                }
                Structura s = new Structura(element);
                structura.addchild(s);
                recurseOverElements(element.children(), s);
            }
    }
    public void writeFile() {
        try {

            Parsingstran PS=new Parsingstran();
            PS=pars;
            String namebook=PS.stran.get(0).name;
            final ArrayList<Parsingstran.Stranica> s=PS.stran;
            namebook=  namebook.trim();
            namebook= namebook.replaceAll("[-\\[\\]^/,'*:.!><~@#$%+=?|\"\\\\()]+", " ");
            namebook=Environment.getExternalStorageDirectory()+"/books/"+namebook+".fb2";
            File file=new File(namebook);
            file.mkdirs();
            if (file.exists ()) file.delete ();
            file.createNewFile();
            FileWriter bw= new FileWriter(file,true);
            bw.write(("<?xml version=\"1.0\" encoding=\"utf-8\"?><FictionBook xmlns=\"http://www.gribuser.ru/xml/fictionbook/2.0\" xmlns:l=\"http://www.w3.org/1999/xlink\"><description><title-info><book-title>"+PS.stran.get(0).name+"</book-title></title-info></description><body>"));
            fileCreate(bw,s,0);
            bw.flush();
            bw.close();
            Context context = getApplicationContext();
            Toast.makeText(context,"Успешно сохранен файл:"+namebook,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

            Showtoast("ошибка:"+e.toString());
        }
    }
    public void fileCreate(FileWriter file, ArrayList<Parsingstran.Stranica> stran, int i)
    {
        try{
            if(stran.size()>i)
            {
                file.write(("<section><title><p>" + stran.get(i).glava + "</p></title>"));
                for (int j = 0; j < stran.get(i).abzac.size(); j++)
                    file.write(("<p>" + stran.get(i).abzac.get(j) + "</p>"));
                file.write("</section>");

                fileCreate(file, stran, i + 1);
            }
            else
            {
                file.write("</body></FictionBook>");
                return;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private SharedPreferences.Editor setArrayS(SharedPreferences.Editor ed, ArrayList<String> array,String s)
    {

        if(array.size()>0) {
            ed.putInt(s+"size", array.size());
            for (int i = 0; i < array.size(); i++) {
                ed.putString(s + i, array.get(i));
            }
        }
        return  ed;
    }
    private ArrayList<String> getArrayS(SharedPreferences ShPef,String s)
    {
        int size=ShPef.getInt(s+"size",0);
        ArrayList<String> array=new ArrayList<>();
        if(size>0) {

            for (int i = 0; i < size; i++) {
                array.add(ShPef.getString(s+i,null));
            }
        }
        return array;
    }
    public void onSave(View view)
    {
        try {
            if (!thread.isAlive()) {
                thread = pars.SaveData(Pref,tbook);
                thread.join();
            }
            else
            {
                thread.join();
            }
        }catch (Exception e){}
    }
    public void onFile(View view)
    {
        writeFile();
    }
    public void Parshtml()
    {
        pars.parsHTML(pars.getmaxstran().url, webView);
    }
    static  public void Showtoast(String s)
    {
        Toast.makeText(cont,s,Toast.LENGTH_SHORT).show();
    }
}
