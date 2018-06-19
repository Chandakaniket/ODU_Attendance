package edu.its.oduattendance;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by achandak on 3/29/2018.
 */

public class AttendanceReport  extends Activity {

    private TextView result_tv,att_res_text;
    private ImageButton imageButton;
    String midas_id,att_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_report);

        result_tv=(TextView)findViewById(R.id.resultTextView);
        att_res_text=(TextView)findViewById(R.id.att_type);
        imageButton=(ImageButton)findViewById(R.id.checkin_button);

        Intent intent=getIntent();

        String qr_token=intent.getStringExtra("result");    //Qr Token
        midas_id=intent.getStringExtra("midasid");           //Midas id
        att_type=intent.getStringExtra("att_status");

        att_res_text.setText(att_type);
        if(qr_token!="")
            result_tv.setText(qr_token);

        if(att_type.contains("ABSENT")){
            att_res_text.setTextColor(Color.RED);
            imageButton.setBackgroundResource(R.drawable.absent);
        }
        else if(att_type.contains("TARDY")){
            att_res_text.setTextColor(Color.WHITE);
            imageButton.setBackgroundResource(R.drawable.late);
        }
        else if(att_type.contains("PRESENT")){
            att_res_text.setTextColor(Color.GREEN);
            imageButton.setBackgroundResource(R.drawable.present);
        }
        else if(att_type.contains("INVALID")){
            att_res_text.setTextColor(Color.WHITE);
            imageButton.setBackgroundResource(R.drawable.invalid);
        }
        else {
            att_res_text.setTextColor(Color.BLACK);
            imageButton.setBackgroundResource(R.drawable.invalid);
            result_tv.setText("Invalid QR");
        }

    }




}
