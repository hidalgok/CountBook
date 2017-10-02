/**
 * MainActivity
 *
 * Version 1.0
 *
 * October 2, 2017
 */
package com.example.khidalgo_countbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * The main activity of this app
 * Consists of a list with clickable objects as well as two buttons
 *
 * @author Ken Hidalgo
 * @see Counter
 * @see CounterActivity
 */
public class MainActivity extends AppCompatActivity {

    public static final int PICK_CONTACT_REQUEST = 1;
    private static final String FILENAME = "file.sav";
    private ListView oldCounterList;
    private TextView titleCounter;
    private ArrayList<Counter> counterList = new ArrayList<Counter>();
    private ArrayAdapter<Counter> adapter;

    /* Called before anything else */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oldCounterList = (ListView) findViewById(R.id.oldCounterList);
        Button clearButton = (Button) findViewById(R.id.Clear);
        Button addButton = (Button) findViewById(R.id.Add);
        titleCounter = (TextView) findViewById(R.id.title);
        titleCounter.setText("Total Counters: " + counterList.size());

        /**
         * Clicking the add button will bring up a new dialog window
         * A user can enter in the parameters of a new counter
         * Name and initial value must be provided
         *
         * Modified from: https://stackoverflow.com/questions/4850493/open-a-dialog-when-i-click-a-button
         * and the Android Documentation
         * 2017-10-02
         */
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
                                //If OK is clicked, check to make sure no empty fields before saving
                                Boolean emptyFields = ((name_field.getText().toString().trim().isEmpty())|
                                        (value_field.getText().toString().trim().isEmpty()));
                                if (!emptyFields){
                                    Counter newCounter = new Counter(name_field.getText().toString(),
                                            Integer.parseInt(value_field.getText().toString()),
                                            comment_field.getText().toString());
                                    counterList.add(newCounter);
                                    titleCounter.setText("Total Counters: " + counterList.size());
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
                        //close dialog on cancel, no changes
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //Clicking the clear button will delete all counter objects
        clearButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_OK);
                counterList.clear();

                adapter.notifyDataSetChanged();
                titleCounter.setText("Total Counters: " + counterList.size());
                saveInFile();
                //finish();

            }
        });

        //On item click start up a new activity to show the chosen counter
        oldCounterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Counter openCounter = (Counter) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_NAME", openCounter.getName());
                extras.putString("EXTRA_COMMENT", openCounter.getComment());
                extras.putLong("EXTRA_DATE", openCounter.getDate().getTime());
                extras.putInt("EXTRA_VAL", openCounter.getValue());
                extras.putInt("EXTRA_INIT_VAL", openCounter.getInitVal());
                extras.putInt("EXTRA_INDEX", position);
                intent.putExtras(extras);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });


    }

    /**
     * On start up of app, load any previously saved info and create adapter
     */
    @Override
    protected void onStart() {
        super.onStart();
        loadFromFile();
        adapter = new ArrayAdapter<Counter>(this,
                R.layout.list_item, counterList);
        oldCounterList.setAdapter(adapter);
    }

    /**
     * Loads up any previously saved counter lists
     * Taken from CMPUT301 lab files (lonelyTwitter Project) and modified for this project
     */
    private void loadFromFile() {
        //ArrayList<String> counters = new ArrayList<String>();
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();

            //Taken from https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
            //2017-10-02
            Type listType = new TypeToken<ArrayList<Counter>>(){}.getType();
            counterList = gson.fromJson(in, listType);
            titleCounter.setText("Total Counters: " + counterList.size());

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            counterList = new ArrayList<Counter>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * Saves current counterlist to a file so it can be read later
     * Taken from CMPUT301 lab files (lonelyTwitter) and modified for this project
     */
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

    /**
     * Upon finish of CounterActivity, update counterlist to reflect any changes made
     *
     * @param requestCode code to counteractivity
     * @param resultCode code specifying result
     * @param data data package
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST){
            if (resultCode == RESULT_OK){ //user clicked back, update counter values
                Bundle bundle = data.getExtras();
                Counter editcounter = adapter.getItem(bundle.getInt("COUNTER_INDEX"));
                editcounter.setName(bundle.getString("COUNTER_NAME"));
                editcounter.setComment(bundle.getString("COUNTER_COMMENT"));
                editcounter.setDate(bundle.getLong("COUNTER_DATE"));
                editcounter.setVal(bundle.getInt("COUNTER_VAL"));
                editcounter.setInitVal(bundle.getInt("COUNTER_INITVAL"));
                adapter.notifyDataSetChanged();
                saveInFile();

            }
            else if (resultCode == RESULT_FIRST_USER){ //user clicked delete, delete counter from list
                int index = data.getIntExtra("INDEX", 1);
                counterList.remove(index);
                adapter.notifyDataSetChanged();
                titleCounter.setText("Total Counters: " + counterList.size());
                saveInFile();

            }
        }

    }
}
