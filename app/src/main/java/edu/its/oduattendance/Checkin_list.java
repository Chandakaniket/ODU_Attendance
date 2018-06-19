package edu.its.oduattendance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.List;

import edu.its.oduattendance.database.DBManager;
import edu.its.oduattendance.database.DataBaseHelper;

public class Checkin_list extends AppCompatActivity {


    private DBManager dbManager;

    private ListView listView;

    private SimpleCursorAdapter adapter;

    Spinner dropdown;

    String[] dates={"All","None"};

    String midas;

    final String[] from = new String[] {DataBaseHelper._ID, DataBaseHelper.response_code, DataBaseHelper.message, DataBaseHelper.c_time,DataBaseHelper.c_date};

    final int[] to = new int[] { R.id.id, R.id.status, R.id.title, R.id.desc,R.id.datedesc};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_list_blank);

        Toolbar toolbar= (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Intent it=getIntent();
        midas=it.getStringExtra("midasid");
        System.out.print("Demo==========="+midas);

        dropdown=(Spinner)findViewById(R.id.spinnerDate);

//        dropdown.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        loadSpinnerData();



        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                // Get select item
                int sid=dropdown.getSelectedItemPosition();
                Toast.makeText(getBaseContext(), "Date selected : " + dates[sid],Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });





        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch(midas);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));


  adapter = new SimpleCursorAdapter(this, R.layout.activity_checkin_list, cursor, from, to, 0);
        adapter.notifyDataSetChanged();


    listView.setAdapter(adapter);

    }

    private void loadSpinnerData() {

        DataBaseHelper db = new DataBaseHelper(getApplicationContext());

        List<String> labels=db.getAllLabels(midas);

        dates= labels.toArray(new String[0]);
        ArrayAdapter<String> spadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels);

        dropdown.setAdapter(spadapter);




    }
}
