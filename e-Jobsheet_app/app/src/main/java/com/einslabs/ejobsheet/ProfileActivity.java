package com.einslabs.ejobsheet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private Button mScan;
    private TextView txtNm;
    private TextView txtJabatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mScan = (Button) findViewById(R.id.btnScan);
//        txtNm = (TextView) findViewById(R.id.txtNm);
//        txtJabatan = (TextView) findViewById(R.id.txtJabatan);

//        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
//        String email = sharedPreferences.getString("email", "");

//        mScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ProfileActivity.this, ScanActivity.class));
//                finish();
//            }
//        });
    }

    private void getMsg (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
