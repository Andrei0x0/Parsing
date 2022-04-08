package com.example.parsing;

import java.util.ArrayList;

public interface Item {
    public String getTitle();
    public int getIconResource();
    public ArrayList<Item> getChilds();
}