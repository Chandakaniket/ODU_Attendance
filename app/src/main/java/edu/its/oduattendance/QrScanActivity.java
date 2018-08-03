/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.its.oduattendance;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.its.mobilevisionbarcodescanner.BarcodeRetriever;
import edu.its.oduattendance.database.DBManager;

import static edu.its.oduattendance.R.id.barcode;
import static edu.its.oduattendance.R.id.image;


public class QrScanActivity extends AppCompatActivity implements BarcodeRetriever {


    private static final String TAG = "BarcodeMain";
    String midas_id;
    private DBManager dbManager;


    private SimpleCursorAdapter adapter;

    BarcodeCapture barcodeCapture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        dbManager = new DBManager(this);
        dbManager.open();

        Intent res=getIntent();
        midas_id=res.getStringExtra("midasid");



        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(barcode);
        barcodeCapture.setRetrieval(this);

        findViewById(R.id.qr_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                barcodeCapture.setShowDrawRect(true)
                        .setSupportMultipleScan(true)
                        .shouldAutoFocus(true)
                        .setBarcodeFormat(Barcode.QR_CODE)
                        .setCameraFacing(CameraSource.CAMERA_FACING_BACK)
                        .setShouldShowText(true);
            }
        });
    }


    @Override
    public void onRetrieved(final Barcode barcode) {
        Log.d(TAG, "Barcode read: " + barcode.displayValue);

        barcodeCapture.stopScanning();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HHmmssMMddyyyy");
        long epoch = System.currentTimeMillis();


        sendDataToServer(epoch,barcode.displayValue,midas_id);  //Sending Midas ID and scanned QR to server..
    }



    private String formatDataAsJSON(long formattedDate, String displayValue, String midas_id){
        final JSONObject jsonObject=new JSONObject();
        final JSONObject identifier=new JSONObject();
        try{
            jsonObject.put("timestamp",formattedDate);
            jsonObject.put("token",displayValue);
            identifier.put("type","QR");
            identifier.put("identifier",midas_id);
            jsonObject.put("identifier",identifier);

            return jsonObject.toString();
        } catch (JSONException e) {
            Log.d("Error","Can't format JSON");
        }
        return jsonObject.toString();
    }


    private void sendDataToServer(long formattedDate, String displayValue, String midas_id) {

        final String json=formatDataAsJSON(formattedDate, displayValue, midas_id);
        System.out.println("Json created is:"+json);

        new sendRequest().execute(json);
    }

    @Override
    public void onRetrievedMultiple(final Barcode closetToClick, final List<BarcodeGraphic> barcodeGraphics) {
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            Barcode barcode = sparseArray.valueAt(i);
            Log.e("value", barcode.displayValue);
        }

    }

    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }


    public void qrscan(View view) {
        finish();
        startActivity(getIntent());
    }

    public void nfcscan(View view) {
    }           //For NFC scanning

    private class sendRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return getServerResponse(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            System.out.println(" Response after Scanning QR : " + result);

            String att_status="";
            String message = "";
            try {

                JSONObject json = new JSONObject(result);

                String response_code = json.getString("response");
                message = json.getString("message");   // Fetching message from json

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
                String date = df.format(c.getTime());         //Current time and date
                String time=tf.format(c.getTime());
                System.out.println("Response--" + response_code + " message--" + message + " date--" + date+" time--" + time);


                //Response codes PRESENT_INT = 1 , ABSENT_INT = 2 , TARDY_INT = 3 and UNKNOWN_ATTENDANCE_TYPE_INT = 99;
                if (response_code.contains("99")) {
                    att_status="INVALID";
                    dbManager.insert(midas_id,"INVALID", message, date,time,R.drawable.invalid);
                    dbManager.close();
                }

                else if (response_code.contains("1")) {
                    att_status="PRESENT";
                    dbManager.insert(midas_id,"PRESENT", message, date,time,R.drawable.present);
                    dbManager.close();
                }

                else if (response_code.contains("2")) {
                    att_status="ABSENT";
                    dbManager.insert(midas_id,"ABSENT", message, date,time,R.drawable.absent);
                    dbManager.close();
                }

                else if (response_code.contains("3")) {
                    att_status="TARDY";
                    dbManager.insert(midas_id,"TARDY", message, date,time,R.drawable.tardy);
                    dbManager.close();
                }

                else
                {
                    att_status=response_code;
                    dbManager.insert(midas_id,"INVALID QR", message, date,time,R.drawable.invalid);
                    dbManager.close();
                }

            } catch (JSONException e) {
                Toast.makeText(QrScanActivity.this,"This is not a valid QR",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


            Intent it = new Intent(QrScanActivity.this, AttendanceReport.class);
            it.putExtra("att_status",att_status);
            it.putExtra("result", message);
            it.putExtra("midasid", midas_id);

            startActivity(it);
            finish();


        }
    }

    private String getServerResponse(String json) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer("");
        int responseCode=0;
        try {

           // URL url = new URL("https://esb.pprd.odu.edu:8443/attendance/0.0.1/attendance/submitAttendance");    //API Call to pre-prod ESB
            URL url = new URL("https://esb.odu.edu:8443/attendance/1.0.0/attendance/submitAttendance");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestProperty("Content-type","application/json");
           // connection.setRequestProperty("authorization","Bearer 7aa944938e65422940567beb3b86e8e1");  //Pre-prod auth auth

            connection.setRequestProperty("authorization","Bearer 3cc5774636de84d79dfae878a4687a");


            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            System.out.println("response message" + connection.getResponseMessage());
            responseCode = connection.getResponseCode();



            System.out.println("response code" + responseCode);

            if(responseCode==200) {
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            }
            System.out.print("Response header msg"+buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();

    }
}




