package com.example.parsing;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.jsoup.nodes.Element;
import org.jsoup.select.*;

import java.util.ArrayList;

public class TegList {
    ArrayList<String> Text;
    ArrayList<Element> teg;
    public TegList()
    {
        Text=new ArrayList<String>();
        teg=new Elements();
    }
    public String getteg(int index)
    {
        String rezult;
        rezult="<"+teg.get(index).tag();
        if(teg.get(index).className().length()>0)
        rezult+=" class='"+teg.get(index).className()+"'";
        if(teg.get(index).id().length()>0)
        rezult+=" id='"+teg.get(index).id()+"'";
        rezult+=">";
        return rezult;
    }
    public void clear()
    {
        Text.clear();
        teg.clear();
    }
    public void sort()
    {
        if(Text.size()>2)
        for(int i=0;i<Text.size();i++)
        {
            if((i+1)<Text.size())
            if(Text.get(i).equals(Text.get(i+1))&&(teg.get(i).children().contains(teg.get(i+1))))
            {
                teg.remove(i);
                Text.remove(i);
            }
        }
    }
   public int getindex(String s)
   {
       for(int i=0;i<Text.size();i++)
           if(Text.get(i).equals(s))
               return i;
           return 0;
   }
}
