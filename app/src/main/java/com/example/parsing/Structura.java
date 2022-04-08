package com.example.parsing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Structura {
    Element Ielement;
    String posledovatelnost;
    ArrayList<Structura> cheldres;
    String str;
    Structura(Element element)
    {
        String rezult;
        rezult=element.tagName();
        if(!element.tagName().equals("body"))
        {
           // if (element.id().length() > 0)
           //     rezult += "[id='" + element.id() + "']";
          //  else
            for (Attribute attribute:element.attributes())
            {

                String attr=attribute.getValue();
                if(attribute.getKey().equals("class"))
                {
                    String[] classes=attr.replaceAll("\\d+","\\\\d+").split(" ");
                    for (String s:classes)
                    {
                        rezult+="["+attribute.getKey()+"~="+s+"]";
                    }
                }
                else
                    if(!attribute.getKey().equals("href"))
                        if(!(attr.indexOf(" ")!=-1))
                rezult+="["+attribute.getKey()+"~="+attr.replaceAll("\\d+","\\\\d+") +"]";
                        else
                        {
                            rezult+="["+attribute.getKey()+"='"+attr +"']";
                        }
            }
           // if (element.className().length() > 0)
           //     rezult += "[class='" + element.className() + "']";
           // if (element.id().length() > 0)
            //  rezult += "[id='" + element.id() + "']";
        }

        posledovatelnost=rezult;
        Ielement=element;
        cheldres=new ArrayList<Structura>();
    }
    public void addchild(Structura structura)
    {
            structura.posledovatelnost = posledovatelnost + " > " + structura.posledovatelnost;
            cheldres.add(structura);
    }
    public ArrayList<String> poisc(Element element)
    {
        return generlstring(cheldres,element,new ArrayList<String>());
    }
    public String poisc(Element element, Document site,final String uri)
    {
        try {
            String rezultall = "";
            site = br2nl(site);
            ArrayList<String> rezult = poisc(element);
            if (rezult.size() > 0) {
                for (String stroci : rezult) {
                    Elements elements = site.select(stroci+">img");
                    if(!elements.isEmpty()) {
                        for (final Element e: elements) {
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        try {
                                        URL url = new URL(uri);
                                        String domain =url.getProtocol()+"://"+url.getHost();
                                        String nameimg="";
                                        URL urlR=new URL(domain+e.attr("src"));
                                        nameimg=urlR.getFile();
                                        Bitmap bm= BitmapFactory.decodeStream(urlR.openConnection() .getInputStream());
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG , 100, baos);
                                        String imgString = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
                                        str="<image l:href=\"#"+nameimg+"\"/><binary content-type=\"image/jpg\" id=\""+nameimg+"\">"+imgString+"</binary>\n";
                                    }catch (Exception ex){ }
                                    }
                                };
                            Thread thread = new Thread(runnable);
                            thread.start();
                            thread.join();
                            if(str!=null)
                                rezultall+=str;
                        }
                    }
                        rezultall += site.select(stroci).text() + "\n";

                }
            } else {
                return null;
            }
            return rezultall;
        }catch (Exception e)
        {
            return null;
        }
    }
    public ArrayList<String> generlstring(ArrayList<Structura> cheld, Element element,ArrayList<String> list)
    {
        if(cheld.size()>0)
        for(Structura s:cheld)
        {
            if(element==s.Ielement)
            {
                list.add(s.posledovatelnost);
            }
            else
            {
                generlstring(s.cheldres,element,list);
            }
        }
        return list;
    }
    public static Document br2nl(Document document ) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        document.html().replaceAll("\\\\n", "\n");
        return document;
    }
}
