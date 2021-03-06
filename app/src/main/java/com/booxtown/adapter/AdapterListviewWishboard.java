package com.booxtown.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.booxtown.model.Wishboard;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.booxtown.R;
import com.booxtown.activity.RespondActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by thuyetpham94 on 30/08/2016.
 */
public class AdapterListviewWishboard extends RecyclerView.Adapter<AdapterListviewWishboard.HolderWisboard> {

    //    String [] title;
//    String [] name;
//    String [] date;
    List<Wishboard> list;
    Context context;
    private static LayoutInflater inflater = null;

    public AdapterListviewWishboard(Context context, List<Wishboard> list) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public HolderWisboard onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.custom_listview_wishboard, parent, false);
        return new HolderWisboard(itemView);
    }

    @Override
    public void onBindViewHolder(HolderWisboard holder, final int position) {
        /*if (list.get(position).getTitle().trim().length() == 0) {
            if (list.get(position).getAuthor().length() == 0) {
                holder.title.setText(list.get(position).getComment());
                holder.name.setText("");
            } else {
                holder.title.setText(list.get(position).getComment());
                holder.name.setText("by " + list.get(position).getAuthor());
            }
        } else {
            String title = list.get(position).getTitle().substring(0,1).toUpperCase()+ list.get(position).getTitle().substring(1,list.get(position).getTitle().length());
            holder.title.setText(title);
            holder.name.setText("by " + list.get(position).getAuthor());
        }*/

        holder.title.setText(list.get(position).getComment());
        try {

            SharedPreferences pref = context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
            String timeZone = pref.getString("timezone", null);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date dt= dateFormat.parse(list.get(position).getCreate_date());
            String intMonth = (String) android.text.format.DateFormat.format("MM", dt);
            String year = (String) android.text.format.DateFormat.format("yyyy", dt);
            String day = (String) android.text.format.DateFormat.format("dd", dt);
            String date_post= day+"-"+intMonth+"-"+year;
            //String[] dates = list.get(position).getCreate_date().substring(0, 10).split("-");
            //String resultDate = dates[2] + "-" + dates[1] + "-" + dates[0].substring(2, dates[0].length());
            holder.date.setText(date_post);
        } catch (Exception exx) {
            holder.date.setText(list.get(position).getCreate_date().substring(0, 10));
        }

//        Bitmap btn1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.btn_wishbroad_message);
//        holder.imgv_listview_respond.setImageBitmap(btn1);

        if (position % 2 == 0) {
            holder.bg_liner.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_user_empty));
        } else {
            holder.bg_liner.setBackgroundColor(ContextCompat.getColor(context, R.color.dot_light_screen1));
        }

        holder.layout_wishboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RespondActivity.class);
                intent.putExtra("wishboard", list.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class HolderWisboard extends RecyclerView.ViewHolder{
        TextView title;
        TextView name;
        TextView date;
        RelativeLayout bg_liner;
        TextView txt_userName;
        LinearLayout layout_wishboard;
        public HolderWisboard(View itemView) {
            super(itemView);
            bg_liner = (RelativeLayout)itemView.findViewById(R.id.relativeLayout_wishboard);
            title=(TextView) itemView.findViewById(R.id.txt_title_lisview_wishboard);
            //name = (TextView)itemView.findViewById(R.id.txt_name_custom_listview_wishboard);
            date = (TextView)itemView.findViewById(R.id.txt_date_customlistview_wishboard);
            layout_wishboard=(LinearLayout) itemView.findViewById(R.id.layout_wishboard);
        }
    }
}