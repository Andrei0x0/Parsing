package com.example.parsing;

import android.content.SharedPreferences;
import java.util.ArrayList;

public class  Savepars implements Runnable {
    SharedPreferences sper;
    String s;
    ArrayList<Parsingstran.Stranica> stra;
    ArrayList<String> htm;
    String gethtm;

    public Savepars(SharedPreferences  Pref, String s, ArrayList<Parsingstran.Stranica> str, ArrayList<String> ht,String getht) {
        this.sper=Pref;
        this.s=s;
        this.stra=str;
        htm=ht;
        gethtm=getht;

    }

    @Override
    public void run() {
     try
     {
         int j;
        SharedPreferences.Editor ed = sper.edit();
         if((j=sper.getInt(s+"sizestran",-1))==-1)
             j=0;
         for(int i=j;i<stra.size();i++)
         {
             stra.get(i).SaveData(sper,s+"stran"+i).join();
         }
        ed= setArrayS(ed,htm,s+"arrayhtml");
        ed.putString(s+"getHTML",gethtm);
        ed.putInt(s+"sizestran",stra.size());
        ed.commit();
    }
            catch(Exception e)
    {
        String s=e.toString();
    }
    }
    private SharedPreferences.Editor setArrayS(SharedPreferences.Editor ed, ArrayList<String> array,String s)
    {
        int j;
        if(array.size()>0) {
            if((j=sper.getInt(s+"size",-1))==-1)
                j=0;
            ed.putInt(s+"size", array.size());
            for (int i = j; i < array.size(); i++) {
                ed.putString(s + i, array.get(i));
            }
        }
        return ed;
    }
}