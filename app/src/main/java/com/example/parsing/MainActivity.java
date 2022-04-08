package com.example.parsing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

import android.widget.ListView;

public class MainActivity extends Activity {


    ArrayList<String> listbook=new ArrayList<String>();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Permmision();
        stopService(new Intent(this, Servis.class));
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id._dynanic);
        SharedPreferences shp=getSharedPreferences ("listbook",MODE_PRIVATE);
        listbook=getArrayS(shp,"listbook");
        setAdapter();
    }

    public  void setAdapter() {
        if(listbook.contains("Новый парсинг")) {
            listbook.remove("Новый парсинг");
            listbook.add("Новый парсинг");
        }
        else
        {
            listbook.add("Новый парсинг");
        }
        ArrayList<String> list=new ArrayList<String>();
        list.add("Новый парсинг");
        final Context context=this;
        Deletelist dellist=new Deletelist(this,listbook,new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getContentDescription() == null&&listbook.size()!=1)return;
               final int i = Integer.parseInt(view.getContentDescription().toString());
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                               SharedPreferences Pref=getSharedPreferences(listbook.get(i),MODE_PRIVATE);
                               Pref.edit().clear().commit();

                                listbook.remove(i);
                                SharedPreferences SP=getSharedPreferences("listbook",MODE_PRIVATE);
                                SharedPreferences.Editor edl=SP.edit();
                                edl= setArrayS(edl,listbook,"listbook");
                                edl.commit();

                                setAdapter();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Вы уверены что хотите удалить:"+listbook.get(i)).setPositiveButton("Да", dialogClickListener)
                        .setNegativeButton("Нет", dialogClickListener).show();

            }

        },list);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text=listbook.get(position);
                Intent intent;
                if(text.equals("Новый парсинг"))
                {
                    intent=new Intent(MainActivity.this,newPars.class);
                }
                else
                {
                    intent=new Intent(MainActivity.this,Parsing.class);
                }
                intent.putExtra("Text",text);
                startActivity(intent);
            }
        });


        lv.setAdapter(dellist);
    }
    private ArrayList<String> getArrayS(SharedPreferences ShPef, String s)
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
    private void Permmision() {
        final int PERMISSION_REQUEST_CODE = 1;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

}
