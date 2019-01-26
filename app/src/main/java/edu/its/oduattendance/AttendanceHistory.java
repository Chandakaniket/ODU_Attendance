package edu.its.oduattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AttendanceHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);


        Intent it=getIntent();
        String midas=it.getStringExtra("midasid");

        //Toast.makeText(this, ""+midas, Toast.LENGTH_SHORT).show();

        WebView webview = new WebView(this);
        setContentView(webview);
        String url = "https://itsapps.odu.edu/apps/attendance/index.php";
        String shared_key="764ef2190dd502097ef9da2b97441a72";
        String postData = null;
        try {
            postData = "username=" + URLEncoder.encode(midas, "UTF-8") + "&sharedKey=764ef2190dd502097ef9da2b97441a72";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webview.postUrl(url,postData.getBytes());
    }
}
