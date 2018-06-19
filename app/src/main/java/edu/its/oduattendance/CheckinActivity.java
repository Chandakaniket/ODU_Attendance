package edu.its.oduattendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckinActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences1;
    private String id,first_name,email,display_name;
    TextView welcome_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_checkin);
        welcome_tv=(TextView)findViewById(R.id.welcome_tv);
        ImageButton checkin=(ImageButton)findViewById(R.id.checkin_button);
        ImageButton history=(ImageButton)findViewById(R.id.historyButton);
        ImageButton settings=(ImageButton)findViewById(R.id.settingButton);

        Intent res=getIntent();
        final String midas_id=res.getStringExtra("midasid");

        new RequestCsService().execute(midas_id);


        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scan=new Intent(CheckinActivity.this,QrScanActivity.class);
                scan.putExtra("midasid",midas_id);
                startActivity(scan);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(CheckinActivity.this,Checkin_list.class);
                it.putExtra("midasid",midas_id);
                startActivity(it);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lgit=new Intent(CheckinActivity.this,SettingActivity.class);
                lgit.putExtra("midasid",midas_id);
                startActivity(lgit);
            }
        });


    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Do you want to exit the app? ");
        builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.finishAffinity(CheckinActivity.this);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.show();
    }

//After shibboleth login is done..
    class RequestCsService extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            return LoginServiceCaller.csCall("http://ww2.odu.edu/apps/mobile/directory/modo-api/details.php?",params[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            System.out.println(" MY requests, Response for isStudent  " + result);

            try {

                JSONObject json = new JSONObject(result);
                System.out.println( json.getString("details"));

                if (!json.isNull("details")){

                    System.out.println("if condition passed");
                    System.out.println( json.getJSONArray("details").getJSONObject(0).getString("email"));

                    id=json.getJSONArray("details").getJSONObject(0).getString("id");
                    first_name=json.getJSONArray("details").getJSONObject(0).getString("first_name");
                    display_name=json.getJSONArray("details").getJSONObject(0).getString("displayName");
                    email=json.getJSONArray("details").getJSONObject(0).getString("email");

                    welcome_tv.setText("Welcome, "+display_name+".");

                    sharedPreferences1=getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putString("first_name",first_name);
                    editor.commit();
                }
                else
                {
                    Toast.makeText(CheckinActivity.this,"Error fetching details",Toast.LENGTH_SHORT).show();


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
