package com.gmail.webos21.radio.db;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.webos21.android.ild.ImageLoader;
import com.gmail.webos21.radio.ChEditActivity;
import com.gmail.webos21.radio.Consts;
import com.gmail.webos21.radio.R;
import com.gmail.webos21.radio.RadioApp;

public class ChRowHolder extends RecyclerView.ViewHolder {

    public ImageView iconImageView;
    public TextView titleTextView;
    public TextView descTextView;
    public ImageView playButton;

    private ImageLoader imgLoader;

    private ChRow item;
    private int position;

    public ChRowHolder(View view) {
        super(view);

        iconImageView = (ImageView) view.findViewById(R.id.iv_favicon);
        titleTextView = (TextView) view.findViewById(R.id.tv_title);
        descTextView = (TextView) view.findViewById(R.id.tv_url);
        playButton = (ImageView) view.findViewById(R.id.iv_control);
    }

    public void setChannel(ChRow item, int position, boolean bShowIcon) {
        this.item = item;
        this.position = position;

        if (imgLoader == null) {
            imgLoader = new ImageLoader(itemView.getContext(), R.drawable.ic_gt);
        }

        // 아이템 내 각 위젯에 데이터 반영
        if (bShowIcon && (item.getLogoUrl() != null) && (item.getLogoUrl().length() > 0)) {
            imgLoader.DisplayImage(item.getLogoUrl(), iconImageView);
        } else {
            iconImageView.setImageDrawable(itemView.getContext().getResources().getDrawable(R.drawable.ic_gt));
        }
        titleTextView.setText("[" + item.getChFreq() + "] " + item.getChName());
        titleTextView.setTag(item);
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    ChRow ref = (ChRow) v.getTag();
                    Intent i = new Intent(v.getContext(), ChEditActivity.class);
                    i.putExtra(Consts.EXTRA_ARG_ID, ref.getId());
                    v.getContext().startActivity(i);
                }
            }
        });
        descTextView.setText(item.getPlayUrl());
        playButton.setTag(this);
        playButton.setOnClickListener(new View.OnClickListener() {
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
    }
}
