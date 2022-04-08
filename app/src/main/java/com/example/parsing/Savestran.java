package com.example.parsing;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;

public class  Savestran implements Runnable {
    SharedPreferences spers;
    String s;
    String url;
    String iturl;
    String name;
    String glava;
    ArrayList<String> abzac;
    public Savestran(SharedPreferences  Pref, String is,String iurl,String iiturl,String iname,String iglava,ArrayList<String> iabzac) {
       spers=Pref;
       s=is;
       url=iurl;
       iturl=iiturl;
       name=iname;
       glava=iglava;
       abzac=iabzac;
    }

    @Override
    public void run() {
        try {

                SharedPreferences.Editor ed = spers.edit();
                ed= setArrayS(ed, abzac, s+glava.replace("\n","").replace(" ","")+"arrayabzac");
                ed.putString(s+"url", url);
                ed.putString(s+"iturl" , iturl);
                ed.putString(s+"name" , name);
                ed.putString(s+"glava" , glava);
                ed.commit();
            }
            catch(Exception e)
            {
                String s=e.toString();
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

return ed;
    }
}
