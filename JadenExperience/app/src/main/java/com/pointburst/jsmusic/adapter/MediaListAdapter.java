package com.pointburst.jsmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pointburst.jsmusic.R;
import com.pointburst.jsmusic.listener.JSMediaPlayerListener;
import com.pointburst.jsmusic.model.Media;

import java.util.ArrayList;

/**
 * Created by FARHAN on 12/30/2014.
 */
public class MediaListAdapter extends BaseAdapter{

    private ArrayList<Media> mMediaArrayList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;


    public MediaListAdapter(Context context,ArrayList<Media> arrayList) {
        mMediaArrayList = arrayList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mMediaArrayList==null?0:mMediaArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Media media = mMediaArrayList.get(position);
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.playlist_item, parent, false);
            holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
            holder.tvTime = (TextView)convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTitle.setText(media.getTitle());
        holder.tvTime.setText(media.getDuration());

        return convertView;
    }

    public class ViewHolder{
        public TextView tvTitle;
        public TextView tvTime;
    }
}
