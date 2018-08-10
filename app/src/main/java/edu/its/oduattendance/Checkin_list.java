package edu.its.oduattendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.List;

import edu.its.oduattendance.database.DBManager;
import edu.its.oduattendance.database.DataBaseHelper;

public class Checkin_list extends AppCompatActivity {

    Button btn;
    TextView d_tv;
    int year_x,month_x,day_x;
    static final int dialog_id=0;


    private DBManager dbManager;

    private ListView listView;

    private SimpleCursorAdapter adapter;


    String midas;

    final String[] from = new String[] { DataBaseHelper.response_code, DataBaseHelper.message, DataBaseHelper.c_time,DataBaseHelper.c_date,DataBaseHelper.att_type};


    final int[] to = new int[] { R.id.status, R.id.title, R.id.desc,R.id.datedesc,R.id.pic};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_list_blank);

        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DATE);
        showDialogOnButtonClick();

        Toolbar toolbar= (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Intent it=getIntent();
        midas=it.getStringExtra("midasid");
        // System.out.print("Demo==========="+midas);

        d_tv=(TextView)findViewById(R.id.date_tv);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));



        dbManager = new DBManager(this);


        dbManager.open();
        Cursor cursor = dbManager.fetch(midas);



        adapter = new SimpleCursorAdapter(this, R.layout.activity_checkin_list, cursor, from, to, 0);

        adapter.notifyDataSetChanged();


        listView.setAdapter(adapter);
        dbManager.close();
    }

    public void showDialogOnButtonClick(){

        btn=(Button)findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(dialog_id);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==dialog_id){
            return new DatePickerDialog(this,dpickerListener,year_x,month_x,day_x);
        }


        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            year_x=year;
            month_x=month+1;
            day_x=day;
            //  Toast.makeText(Checkin_list.this,year_x+"/"+month_x+"/"+day_x,Toast.LENGTH_SHORT).show();

            String date_s=month_x+"-"+day_x+"-"+year_x;

            if(day_x<10){
                date_s=month_x+"-0"+day_x+"-"+year_x;       //Adjusting day, as in db month is storing in double digit
            }

            if(month_x<10) {
                date_s="0"+date_s;        //Adjusting month, as in db month is storing in double digit
            }

            d_tv.setText(getString(R.string.date_sel_instruction)+date_s);


            dbManager.open();
            Cursor cursor1 = dbManager.datefetch(midas,date_s);
            adapter = new SimpleCursorAdapter(Checkin_list.this, R.layout.activity_checkin_list, cursor1, from, to, 0);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
            dbManager.close();

        }
    };

}
