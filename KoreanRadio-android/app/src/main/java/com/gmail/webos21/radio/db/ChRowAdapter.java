package com.gmail.webos21.radio.db;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.webos21.android.ild.ImageLoader;
import com.gmail.webos21.radio.R;
import com.gmail.webos21.radio.RadioApp;

import java.util.List;

public class ChRowAdapter extends BaseAdapter {

    private List<ChRow> chRows;
    private ChDbInterface chDb;

    private ImageLoader imgLoader;

    private boolean bShowIcon;

    public ChRowAdapter(Context context, boolean showIcon) {
        ChDbManager dbMan = ChDbManager.getInstance();
        dbMan.init(context);

        chDb = dbMan.getPbDbInterface();
        chRows = chDb.findRows();

        this.bShowIcon = showIcon;
    }

    @Override
    public int getCount() {
        return chRows.size();
    }

    @Override
    public Object getItem(int position) {
        return chRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return chRows.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChRowHolder holder = null;
        View rootView = null;

        int pos = position;
        Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(R.layout.ch_row, parent, false);

            holder = new ChRowHolder();
            holder.iconImageView = (ImageView) rootView.findViewById(R.id.iv_favicon);
            holder.titleTextView = (TextView) rootView.findViewById(R.id.tv_title);
            holder.descTextView = (TextView) rootView.findViewById(R.id.tv_url);
            holder.playButton = (ImageView) rootView.findViewById(R.id.iv_control);

            rootView.setTag(holder);
        } else {
            rootView = convertView;
            holder = (ChRowHolder) rootView.getTag();
        }

        if (imgLoader == null) {
            imgLoader = new ImageLoader(context, R.drawable.ic_gt);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ChRow chData = chRows.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        if (bShowIcon && (chData.getLogoUrl() != null) && (chData.getLogoUrl().length() > 0)) {
            imgLoader.DisplayImage(chData.getLogoUrl(), holder.iconImageView);
        } else {
            holder.iconImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_gt));
        }
        holder.titleTextView.setText("[" + chData.getChFreq() + "] " + chData.getChName());
        holder.descTextView.setText(chData.getPlayUrl());
        holder.playButton.setTag(holder);

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    ChRowHolder ref = (ChRowHolder) v.getTag();
                    RadioApp app = (RadioApp) v.getContext().getApplicationContext();
                    if (app != null) {
                        if (app.isPlaying()) {
                            ref.playButton.setImageResource(android.R.drawable.ic_media_play);
                            app.stopRadio();
                        } else {
                            ref.playButton.setImageResource(android.R.drawable.ic_media_pause);
                            String url = ref.descTextView.getText().toString();
                            app.playRadio(url);
                        }
                    }
                }
            }
        });

        return rootView;
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    public void searchItems(String w) {
        chRows.clear();
        chRows = chDb.findRows(w);
        notifyDataSetChanged();
    }

    public void searchAll() {
        chRows.clear();
        chRows = chDb.findRows();
        notifyDataSetChanged();
    }

    public void setShowIcon(boolean bShowIcon) {
        this.bShowIcon = bShowIcon;
        notifyDataSetChanged();
    }
}