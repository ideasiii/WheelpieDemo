package org.iii.wheelpiedemo.menu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.iii.wheelpiedemo.R;

public class CustomAdapter extends BaseAdapter {

    private LayoutInflater myInflater;
    private String[] titles;
    private int[] imageResourceIds;


    public CustomAdapter(Context context, String[] text1, int[] imageIds) {
        myInflater = LayoutInflater.from(context);
        titles = text1;
        imageResourceIds = imageIds;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return titles.length;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        row = myInflater.inflate(R.layout.fragment_other_dialog_list, parent, false);
        TextView title;
        ImageView i1;
        i1 = (ImageView) row.findViewById(R.id.menu_iv1);
        title = (TextView) row.findViewById(R.id.menu_tv1);
        title.setText(titles[position]);
        i1.setImageResource(imageResourceIds[position]);
        return (row);
    }
}