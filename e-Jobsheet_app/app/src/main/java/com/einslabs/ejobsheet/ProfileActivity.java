package com.einslabs.ejobsheet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private AlertDialog.Builder builder;
    private ImageView mProfileImg;
    private Button mScan;
    private TextView txtNm;
    private TextView txtJabatan;
    private TextView txtTkNm;
    private TextView txtMobile;
    private TextView txtTkEmail;
    private TextView txtNik;
    private TextView txtMulaiKrj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProfileImg = (ImageView) findViewById(R.id.profile_img_view);

        SharedPreferences sharedPreferences = getSharedPreferences("teknisi", MODE_PRIVATE);
        if (!sharedPreferences.getString("foto_teknisi", "").equals("")){
            Glide.with(this).load("http://goo.gl/gEgYUd").override(150, 150).into(mProfileImg);
        }else{
            Log.d(TAG, "foto_teknisi NULL");
        }

//        mScan = (Button) findViewById(R.id.btnScan);
        txtNm = (TextView) findViewById(R.id.txtNm);
        txtJabatan = (TextView) findViewById(R.id.txtJabatan);
        txtTkNm = (TextView) findViewById(R.id.txtTkNm);
        txtMobile = (TextView) findViewById(R.id.txtMobile);
        txtTkEmail = (TextView) findViewById(R.id.txtTkEmail);
        txtNik = (TextView) findViewById(R.id.txtNik);
        txtMulaiKrj = (TextView) findViewById(R.id.txtMulaiKrj);

        txtNm.setText(sharedPreferences.getString("nama", ""));
        txtJabatan.setText(sharedPreferences.getString("jabatan", ""));
        txtTkNm.setText(sharedPreferences.getString("nama", ""));
        txtMobile.setText(sharedPreferences.getString("no_hp", ""));
        txtTkEmail.setText(sharedPreferences.getString("email", ""));
        txtNik.setText(sharedPreferences.getString("nik_teknisi", ""));
        txtMulaiKrj.setText(sharedPreferences.getString("tgl_mulai_krj", ""));

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

    public void showDialog(final String status, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                builder.setMessage(msg).setTitle(status).create().show();
            }
        });
    }

    private void startTrackerService() {
//        SharedPreferences shTeknisi = getSharedPreferences("teknisi", MODE_PRIVATE);
//        SharedPreferences shLogin = getSharedPreferences("login", MODE_PRIVATE);
//        String email = shLogin.getString("email", "");
//        String pass = shLogin.getString("password", "");
//        String nik = shTeknisi.getString("nik_teknisi","");
//        Intent intent = new Intent(this, TrackerService.class);
//        intent.putExtra("email", email);
//        intent.putExtra("password", pass);
//        intent.putExtra("nik", nik);
        showDialog("Berhasil", "berhasil run fungsi");
        //startService(intent);
        startService(new Intent(this, TrackerService.class));
    }
}
