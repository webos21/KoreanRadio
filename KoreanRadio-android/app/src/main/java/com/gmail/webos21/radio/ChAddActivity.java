package com.gmail.webos21.radio;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.webos21.radio.db.ChDbInterface;
import com.gmail.webos21.radio.db.ChDbManager;
import com.gmail.webos21.radio.db.ChRow;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ChAddActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lblTitle;

    private ViewGroup panelId;
    private ViewGroup panelFixday;

    private TextView tvId;
    private EditText edFreq;
    private EditText edName;
    private EditText edPurl;
    private EditText edLurl;
    private TextView tvRegDate;
    private EditText edMemo;

    private Button btnSave;

    private DatePickerListener dpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_edit);

        lblTitle = (TextView) findViewById(R.id.lbl_title);
        lblTitle.setText(getResources().getString(R.string.che_title_add));

        panelId = (ViewGroup) findViewById(R.id.panel_id);
        panelId.setVisibility(View.GONE);

        panelFixday = (ViewGroup) findViewById(R.id.panel_fixday);
        panelFixday.setVisibility(View.GONE);

        tvId = (TextView) findViewById(R.id.tv_id);
        edFreq = (EditText) findViewById(R.id.ed_freq);
        edName = (EditText) findViewById(R.id.ed_name);
        edPurl = (EditText) findViewById(R.id.ed_purl);
        edLurl = (EditText) findViewById(R.id.ed_lurl);
        tvRegDate = (TextView) findViewById(R.id.tv_regdate);
        tvRegDate.setOnClickListener(this);
        edMemo = (EditText) findViewById(R.id.ed_memo);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setText(getResources().getString(R.string.che_add));
        btnSave.setOnClickListener(this);

        dpl = new DatePickerListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_regdate:
                showDatePicker();
                break;
            case R.id.btn_save:
                saveData();
                break;
            default:
                break;
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, dpl, year, month, day);
        dpd.show();
    }

    private void errorToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void saveData() {
        String ch_freq = edFreq.getText().toString();
        String ch_name = edName.getText().toString();
        String play_url = edPurl.getText().toString();
        String logo_url = edLurl.getText().toString();
        String regDate = tvRegDate.getText().toString();
        String memo = edMemo.getText().toString();

        if (ch_freq == null || ch_freq.length() < 2) {
            edFreq.requestFocus();
            errorToast(getResources().getString(R.string.err_ch_freq));
            return;
        }
        if (ch_name == null || ch_name.length() < 2) {
            edName.requestFocus();
            errorToast(getResources().getString(R.string.err_ch_name));
            return;
        }
        if (play_url == null || play_url.length() < 5) {
            edPurl.requestFocus();
            errorToast(getResources().getString(R.string.err_ch_purl));
            return;
        }
        if (logo_url == null || logo_url.length() < 5) {
            edLurl.requestFocus();
            errorToast(getResources().getString(R.string.err_ch_lurl));
            return;
        }
        if (regDate == null || regDate.length() < 2) {
            tvRegDate.requestFocus();
            errorToast(getResources().getString(R.string.err_pb_regdate));
            return;
        }

        Date rd = null;
        try {
            rd = Consts.SDF_DATE.parse(regDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ChRow pbr = new ChRow(null, ch_freq, ch_name, play_url, logo_url, rd.getTime(), System.currentTimeMillis(), memo);
        ChDbInterface pdi = ChDbManager.getInstance().getPbDbInterface();
        pdi.updateRow(pbr);

        Intent i = new Intent();
        setResult(Activity.RESULT_OK, i);

        finish();
    }

    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String strDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            tvRegDate.setText(strDate);
        }
    }

}
