package com.example.parsing;

import java.util.ArrayList;

public class ListItem implements Item {

    private String title;
    private ArrayList<Item> childs;

    public ListItem (String title) { // 1
        this.title = title;
        childs = new ArrayList<Item>();
    }

    @Override
    public String getTitle() { // 2
        return title;
    }

    @Override
    public ArrayList<Item> getChilds() { // 3
        return childs;
    }

    @Override
    public int getIconResource() { // 4
        if (childs.size() > 0)
            return R.mipmap.ic_launcher;
        return R.mipmap.ic_launcher_round;
    }

    public void addChild (Item item) { // 5
        childs.add(item);
    }



}