package com.example.khidalgo_countbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1;
    static final int RESULT_DELETE = 1;
    private static final String FILENAME = "file.sav";
    private ListView oldCounterList;

    private ArrayList<Counter> counterList;
    private ArrayAdapter<Counter> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oldCounterList = (ListView) findViewById(R.id.oldCounterList);
        Button clearButton = (Button) findViewById(R.id.Clear);
        Button addButton = (Button) findViewById(R.id.Add);

        addButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //This dialog was designed by using the android documentation as well as code from
                //https://stackoverflow.com/questions/4279787/how-can-i-pass-values-between-a-dialog-and-an-activity
                //https://stackoverflow.com/questions/30799369/disable-closing-alertdialog-if-edittext-is-empty
                //2017-10-02
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
                View diaView = inflater.inflate(R.layout.dialog, null);
                final EditText name_field = (EditText) diaView.findViewById(R.id.editName);
                final EditText value_field = (EditText) diaView.findViewById(R.id.editValue);
                final EditText comment_field = (EditText) diaView.findViewById(R.id.editComment);
                builder.setView(diaView)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Boolean wantToCloseDialog = ((name_field.getText().toString().trim().isEmpty())|
                                        (value_field.getText().toString().trim().isEmpty()));
                                // if EditText is empty disable closing on possitive button
                                if (!wantToCloseDialog){
                                    Counter newCounter = new Counter(name_field.getText().toString(),
                                            Integer.parseInt(value_field.getText().toString()),
                                            comment_field.getText().toString());
                                    counterList.add(newCounter);
                                    adapter.notifyDataSetChanged();
                                    saveInFile();
                                }
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

        clearButton.setOnClickListener(new View.OnClickListener() {

            /**
             * On clear button click delete all tweets and update screen
             *
             * @param v current view
             */
            public void onClick(View v) {
                setResult(RESULT_OK);
                counterList.clear();

                adapter.notifyDataSetChanged();

                saveInFile();
                //finish();

            }
        });

        oldCounterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Counter openCounter = (Counter) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_NAME", openCounter.getName());
                extras.putString("EXTRA_COMMENT", openCounter.getComment());
                extras.putLong("EXTRA_DATE", openCounter.getDate().getTime());
                extras.putInt("EXTRA_VAL", openCounter.getValue());
                extras.putInt("EXTRA_INIT_VAL", openCounter.getValue());
                extras.putInt("EXTRA_INDEX", position);
                intent.putExtras(extras);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFromFile();
        adapter = new ArrayAdapter<Counter>(this,
                R.layout.list_item, counterList);
        oldCounterList.setAdapter(adapter);
    }

    private void loadFromFile() {
        ArrayList<String> tweets = new ArrayList<String>();
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();

            //Taken from https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
            //2017-10-02
            Type listType = new TypeToken<ArrayList<Counter>>(){}.getType();
            counterList = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            counterList = new ArrayList<Counter>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    private void saveInFile() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            gson.toJson(counterList, out);
            out.flush();

            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }
}
