package com.example.parsing;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

public class newPars extends Activity {
    Parsingstran pars = new Parsingstran();
    EditText editurl;
    Button prob, startpars;
    AutoCompleteTextView autoText,autoText2,autoText3,autoText4;
    WebView webview;
    TabHost tabHost;
    ArrayList<Item> items=new ArrayList<Item>(); // 1
    ListAdapter adapter;
    ListView mList;
    TegList tegList=new TegList();
    Runnable Rb;
    String stablhtml;
    Structura struc;
    boolean soderg[]=new boolean[4];
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pars);
        Initelizice();
    }
    public void Initelizice() {
        editurl = (EditText) findViewById(R.id.editText);
        prob = (Button) findViewById(R.id.button);
        startpars = (Button) findViewById(R.id.button2);
        webview = (WebView) findViewById(R.id.webview);
        pars.parsHTML("https://www.google.ru", webview);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        mList = (ListView) this.findViewById(R.id.listview);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Браузер");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("HTML");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("HTML");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(1);
        stopService(new Intent(newPars.this, Servis.class));
        generateSomeHierarchy(pars);
        Rb=new Runnable()
        {
            @Override
            public void run()
            {

                if(Arraybool(soderg)) {
                    View view=findViewById(R.id.button2);

                    startpars.setEnabled(true);
                }
                else
                {
                    startpars.setEnabled(false);
                }
                if(stablhtml!=pars.getHTML)
                {

                    stablhtml=pars.getHTML;
                   // autoText4.setText(pars.urllink);
                    if (tegList.Text.contains(autoText4.getText().toString())) {
                        autoText4.setTextColor(Color.GREEN);
                        soderg[2]=true;
                    } else {
                        autoText4.setTextColor(Color.RED);
                        soderg[2]=false;
                    }
                        generateSomeHierarchy(pars);
                            SetAdapter();
                }
                    handler.postDelayed(this, 1000);
            }
        };

        autoText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoText2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        autoText3 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
        autoText4 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView4);
        editurl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (URLUtil.isValidUrl(s.toString())) {
                    editurl.setTextColor(Color.GREEN);
                    soderg[3]=true;
                } else {
                    editurl.setTextColor(Color.RED);
                    soderg[3]=false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        autoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tegList.Text.contains(autoText.getText().toString())) {
                    autoText.setTextColor(Color.GREEN);
                } else {
                    autoText.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        autoText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (tegList.Text.contains(autoText2.getText().toString())) {
                    soderg[0]=true;
                    autoText2.setTextColor(Color.GREEN);
                } else {
                    autoText2.setTextColor(Color.RED);
                    soderg[0]=false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        autoText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (tegList.Text.contains(autoText3.getText().toString())) {
                    autoText3.setTextColor(Color.GREEN);
                    soderg[1]=true;
                } else {
                    autoText3.setTextColor(Color.RED);
                    soderg[1]=false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        autoText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (tegList.Text.contains(autoText4.getText().toString())) {
                    autoText4.setTextColor(Color.GREEN);
                    soderg[2]=true;
                } else {
                    autoText4.setTextColor(Color.RED);
                    soderg[2]=false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editurl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editurl.setHint("");
                else
                    editurl.setHint("Адрес сайта");
            }
        });
        editurl.setHint("Адрес сайта");

        autoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    autoText.setHint("");
                else
                    autoText.setHint("Название книги");
            }
        });
        autoText.setHint("Название книги");
        autoText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    autoText2.setHint("");
                else
                    autoText2.setHint("Название главы книги");
            }
        });
        autoText2.setHint("Название главы книги");
        autoText3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    autoText3.setHint("");
                else
                    autoText3.setHint("Абзац");
            }
        });
        autoText3.setHint("Абзац");
        autoText4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    autoText4.setHint("");
                else
                    autoText4.setHint("Ссылка на следующую страницу");
            }
        });
        autoText4.setHint("Ссылка на следующую страницу");
        handler.postDelayed(Rb, 10);
    }
    public void poiscteg(View view) {
        if (URLUtil.isValidUrl(editurl.getText().toString())) {
            pars.parsHTML(editurl.getText().toString(), webview);

            handler.postDelayed(Rb, 1000);
        }
    }
    private void generateSomeHierarchy(Parsingstran p) {
        String HTML=p.getHTML;
        if(items.size()>0)
            items.clear();
        if(HTML !=null) {
            Document doc = Jsoup.parse(HTML);
            Elements elelist = doc.children();
            elelist=elelist.get(0).children();
            ListItem li = new ListItem("<html>");
            items.add(li);
            struc=new Structura(doc.createElement("html"));
            tegList.clear();
            recurseOverElements(elelist,li,struc);
            tegList.sort();
        }
        else
        {
            ListItem li = new ListItem("Нет данных");
            items.add(li);
        }

        obnovlistview(items,mList);
    }
    public void obnovlistview(ArrayList<Item> items,ListView mList) {
            adapter = new ListAdapter(this, items);
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.clickOnItem(position);
            }

        });
    }
    public  void recurseOverElements(Elements elementList,ListItem item,Structura structura){
   if(elementList.size()>0)
     for(Element element:elementList) {
             String rezult;
             rezult = "<" + element.tag();
             if (element.className().length() > 0)
                 rezult += " class='" + element.className() + "'";
             if (element.id().length() > 0)
                 rezult += " id='" + element.id() + "'";
             rezult += ">";
             ListItem li = new ListItem(rezult);
             if (element.tagName().equals("a")) {
                 tegList.Text.add(element.attr("href"));
                 tegList.teg.add(element);
             }
             if ((element.text().length() > 0)) {
                 tegList.Text.add(element.text());
                 tegList.teg.add(element);

                 ListItem l = new ListItem(element.text());
                 ListItem l2 = new ListItem("Текст");
                 li.addChild(l2);
                 l2.addChild(l);
             }

             Structura s = new Structura(element);
             structura.addchild(s);
             item.addChild(li);
         recurseOverElements(element.children(), li, s);
     }
    }
    public void SetAdapter() {
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tegList.Text);

        autoText.setAdapter(arrayAdapter);
        autoText2.setAdapter(arrayAdapter);
        autoText3.setAdapter(arrayAdapter);
        autoText4.setAdapter(arrayAdapter);
        autoText.setThreshold(1);
        autoText2.setThreshold(1);
        autoText3.setThreshold(1);
        autoText4.setThreshold(1);

    }
    public boolean Arraybool(boolean array[]) {
        for (boolean b:array){
            if(!b)
            {
                return false;
            }

        }
        return true;
    }
    public void startParsing(View view) {
        Intent intent=new Intent(newPars.this,Parsing.class);
        String string;
        intent.putExtra("neznay",true);
        intent.putExtra("url",editurl.getText().toString());
        string=autoText.getText().toString();
        intent.putExtra("namebook",tegList.getindex(string));
        string=autoText2.getText().toString();
        intent.putExtra("nameglav",tegList.getindex(string));
        string=autoText3.getText().toString();
        intent.putExtra("nameabzac",tegList.getindex(string));
        string=autoText4.getText().toString();
        intent.putExtra("namelink",tegList.getindex(string));
        intent.putExtra("html",pars.getHTML);
        startActivity(intent);
    }
}
