package com.booxtown.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.booxtown.model.Filter;

import java.util.List;

import com.booxtown.R;

/**
 * Created by thuyetpham94 on 28/08/2016.
 */
public class AdapterFilter  extends BaseAdapter {

    String [] result;
    Context context;
    List<Filter> list;
    Integer selected_position = -1;
    private static LayoutInflater inflater=null;
    public AdapterFilter(Context context, List<Filter> list) {
        // TODO Auto-generated constructor stub
        this.list=list;
        this.context=context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        CheckBox ck;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        final Filter filter = list.get(position);
        View rowView;
        rowView = inflater.inflate(R.layout.custom_listview_fitersort, null);
        holder.tv=(TextView) rowView.findViewById(R.id.txt_listview_filter);
         holder.ck = (CheckBox) rowView.findViewById(R.id.checkBox_listview_filter) ;
        holder.tv.setText(filter.getTitle());
        holder.ck.setChecked(filter.isCheck);
        //
        holder.ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    for(int i=0; i<list.size(); i++){
                        list.get(i).setCheck(false);
                    }
                    list.get(position).setCheck(true);
                    holder.ck.setChecked(true);
                }
                else{
                    list.get(position).setCheck(false);
                    holder.ck.setChecked(false);
                }
                notifyDataSetChanged();
            }
        });
        return rowView;
    }
}