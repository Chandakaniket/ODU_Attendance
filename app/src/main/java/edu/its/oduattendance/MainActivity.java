package edu.its.oduattendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    Button login_button;
    public static String midas_id,password;
    EditText midasId,Password;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        sharedPreferences=getPreferences(Context.MODE_PRIVATE);
        midasId = findViewById(R.id.midasId);
        Password = findViewById(R.id.Password);


        Intent it=getIntent();
        int status=it.getIntExtra("logout",0);
        if(status==1){

            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }

        if(!sharedPreferences.getString("midasId","0").equals("0"))
        {
            Intent in = new Intent(getApplicationContext(), CheckinActivity.class);
            in.putExtra("midasid",sharedPreferences.getString("midasId","0"));
            startActivity(in);
            finish();

        }
        System.out.println(sharedPreferences.getString("rememberMe","0"));

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                midas_id = midasId.getText().toString();
                password = Password.getText().toString();
                getLoginDetails(midas_id, password);
            }
        });


        midas_id=sharedPreferences.getString("midasId","0");

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
                ActivityCompat.finishAffinity(MainActivity.this);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.show();
    }


    public void getLoginDetails(String midas_id, String password) {

        if(isNetworkAvailable())
            new RequestService().execute(midas_id, password);
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Network Unavailable");
            builder.setMessage("Your device is currently unable to establish a network connection.\nTurn on cellular data or use Wi-Fi to access data.");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            builder.show();
        }


    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    } //Check Availability of Network

    class RequestService extends AsyncTask<String, Integer, String> {
        public ProgressDialog dialog=new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Connecting...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return LoginServiceCaller.oduCall("https://itsapps.odu.edu/auth/", params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String result) {
            //     System.out.println(" MY requests, Response from odu: " + result);
            new RequestShibbolethService().execute(result);
            dialog.cancel();
        }
    }


    class RequestShibbolethService extends AsyncTask<String, Integer, String> {
        public ProgressDialog dialog=new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Authenticating...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return LoginServiceCaller.shibbolethCall("https://shibboleth.odu.edu/idp/profile/SAML2/SOAP/ECP", params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            System.out.println(" MY requests, Response from shibboleth: " + result);
            if (result.equals("200")) {
                System.out.println("success login"); //Shib Auth done!!


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("midasId",midas_id);
                editor.putString("rememberMe","true");
                editor.commit();            //Storing midas_id for future login's


                Intent in = new Intent(getApplicationContext(), CheckinActivity.class);
                in.putExtra("midasid",midas_id);
                startActivity(in);
                finish();

            } else if (result.equals("401")) {
                builder.setTitle("Alert");
                builder.setMessage("Invalid Username/Password");
                System.out.println("invalid user/password");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {} });
                builder.show();
            } else {
                builder.setTitle("Alert");
                builder.setMessage("error try again");
                System.out.println("error try again");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {} });
                builder.show();
            }

            dialog.cancel();
        }
    }
}









