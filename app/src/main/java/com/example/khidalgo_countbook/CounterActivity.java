package com.example.khidalgo_countbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;

public class CounterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String counterName = extras.getString("EXTRA_NAME");
        String counterComment = extras.getString("EXTRA_COMMENT");
        Date counterDate = new Date();
        counterDate.setTime(extras.getLong("EXTRA_DATE"));
        Integer counterVal = extras.getInt("EXTRA_VAL");
        Integer counterInitVal = extras.getInt("EXTRA_INIT_VAL");
        Integer counterIndex = extras.getInt("EXTRA_INDEX");
    }
}
