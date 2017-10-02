package com.example.khidalgo_countbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class CounterActivity extends AppCompatActivity {

    private String counterName;
    private String counterComment;
    private Date counterDate = new Date();
    private Integer counterVal;
    private Integer counterInitVal;
    private Integer counterIndex;
    private TextView nameField;
    private TextView commentField;
    private TextView dateField;
    private TextView valField;
    private TextView initValField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        counterName = extras.getString("EXTRA_NAME");
        counterComment = extras.getString("EXTRA_COMMENT");
        counterDate.setTime(extras.getLong("EXTRA_DATE"));
        counterVal = extras.getInt("EXTRA_VAL");
        counterInitVal = extras.getInt("EXTRA_INIT_VAL");
        counterIndex = extras.getInt("EXTRA_INDEX");

        nameField = (TextView) findViewById(R.id.countName);
        commentField = (TextView) findViewById(R.id.countComment);
        dateField = (TextView) findViewById(R.id.countDate);
        valField = (TextView) findViewById(R.id.countVal);
        initValField = (TextView) findViewById(R.id.countInitVal);

        updateDisplay();

        Button decButton = (Button) findViewById(R.id.decrement);
        Button incButton = (Button) findViewById(R.id.increment);
        Button resetButton = (Button) findViewById(R.id.reset);
        Button deleteButton = (Button) findViewById(R.id.delete);
        Button editButton = (Button) findViewById(R.id.edit);

        decButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_OK);
                if (counterVal > 0){
                    counterVal -= 1;
                    counterDate = new Date();
                    updateDisplay();
                }
            }
        });

        incButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_OK);
                counterVal += 1;
                counterDate = new Date();
                updateDisplay();

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_OK);
                counterVal = counterInitVal;
                counterDate = new Date();
                updateDisplay();

            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d("tag", "pressed delete");
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.putExtra("INDEX", counterIndex);
                setResult(MainActivity.RESULT_DELETE, intent);
                finish();
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CounterActivity.this);
                LayoutInflater inflater = (CounterActivity.this).getLayoutInflater();
                View diaView = inflater.inflate(R.layout.dialog_counter, null);
                final EditText name_field = (EditText) diaView.findViewById(R.id.editName);
                final EditText value_field = (EditText) diaView.findViewById(R.id.editValue);
                final EditText init_value_field = (EditText) diaView.findViewById(R.id.editInitValue);
                final EditText comment_field = (EditText) diaView.findViewById(R.id.editComment);
                builder.setView(diaView)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Boolean emptyName = name_field.getText().toString().trim().isEmpty();
                                Boolean emptyVal = value_field.getText().toString().trim().isEmpty();
                                Boolean emptyInitVal = init_value_field.getText().toString().trim().isEmpty();
                                Boolean emptyComment = comment_field.getText().toString().trim().isEmpty();

                                if(!emptyName){
                                    counterName = name_field.getText().toString();
                                }
                                if(!emptyVal){
                                    counterVal = Integer.parseInt(value_field.getText().toString());
                                    counterDate = new Date();
                                }
                                if(!emptyInitVal){
                                    counterInitVal = Integer.parseInt(init_value_field.getText().toString());
                                }
                                if(!emptyComment){
                                    counterComment = comment_field.getText().toString();
                                }
                                updateDisplay();
                            }
                        })
                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    private void updateDisplay(){
        nameField.setText(counterName);
        commentField.setText(counterComment);
        dateField.setText("Last change: " + counterDate.toString());
        valField.setText(counterVal.toString());
        initValField.setText("Initial value: " + counterInitVal.toString());
    }

    //Modified from https://stackoverflow.com/questions/2679250/setresult-does-not-work-when-back-button-pressed
    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString("COUNTER_NAME", counterName);
        bundle.putString("COUNTER_COMMENT", counterComment);
        bundle.putLong("COUNTER_DATE", counterDate.getTime());
        bundle.putInt("COUNTER_VAL", counterVal);
        bundle.putInt("COUNTER_INITVAL", counterInitVal);
        bundle.putInt("COUNTER_INDEX", counterIndex);

        Intent mIntent = new Intent(CounterActivity.this, MainActivity.class);
        mIntent.putExtras(bundle);
        setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }
}
