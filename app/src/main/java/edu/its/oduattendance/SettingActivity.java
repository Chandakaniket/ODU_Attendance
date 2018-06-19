package edu.its.oduattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button signout=(Button)findViewById(R.id.sign_out);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lgit=new Intent(SettingActivity.this,MainActivity.class);
                lgit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                lgit.putExtra("logout",1);
                finish();
                startActivity(lgit);
            }
        });
    }
}
