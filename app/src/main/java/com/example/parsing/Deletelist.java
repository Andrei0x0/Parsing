package com.example.parsing;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Deletelist extends BaseAdapter {
    ArrayList<String> data = new ArrayList<String>();
    Context context;
    View.OnClickListener onClickListener;
    ArrayList<String> iscluc;
    public Deletelist(Context context, ArrayList<String> arr, View.OnClickListener OCL ) {
        if (arr != null) {
            data = arr;
        }
        this.context =context;
        onClickListener=OCL;
    }
    public Deletelist(Context context, ArrayList<String> arr, View.OnClickListener OCL,ArrayList<String> s) {
        if (arr != null) {
            data = arr;
        }
        this.context =context;
        onClickListener=OCL;
        iscluc=s;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int num) {
        // TODO Auto-generated method stub
        return data.get(num);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int i, View someView, ViewGroup arg2) {
        //Получение объекта inflater из контекста
        LayoutInflater inflater = LayoutInflater.from(context);
        //Если someView (View из ListView) вдруг оказался равен
        //null тогда мы загружаем его с помошью inflater
        if (someView == null) {
            someView = inflater.inflate(R.layout.list_view_delete, arg2, false);
        }
        TextView Header = (TextView) someView.findViewById(R.id.Viewtext);
        Button button=(Button)someView.findViewById(R.id.buttondel);
        if(iscluc!=null)
        if(iscluc.contains(data.get(i)))
            button.setVisibility(View.VISIBLE);
        button.setOnClickListener(onClickListener);
        button.setContentDescription(Integer.toString(i));

        Header.setText(data.get(i));
        return someView;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}
