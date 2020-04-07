package com.einslabs.ejobsheet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private AlertDialog.Builder builder;
    private ImageView mProfileImg;
    private TextView mScan;
    private TextView mlogout;
    private TextView txtNm;
    private TextView txtJabatan;
    private TextView txtTkNm;
    private TextView txtMobile;
    private TextView txtTkEmail;
    private TextView txtNik;
    private TextView txtMulaiKrj;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProfileImg = (ImageView) findViewById(R.id.profile_img_view);

        mAuth = FirebaseAuth.getInstance();
        final SharedPreferences sharedPreferences = getSharedPreferences("teknisi", MODE_PRIVATE);
        if (!sharedPreferences.getString("foto_teknisi", "").equals("")){
            Glide.with(this).load(sharedPreferences.getString("foto_teknisi", "")).override(150, 150).into(mProfileImg);
        }else{
            Log.d(TAG, "foto_teknisi NULL");
            Glide.with(this).load(R.drawable.default_avatar).override(150, 150).into(mProfileImg);
        }

        mScan = (TextView) findViewById(R.id.btnScan);
        mlogout = (TextView) findViewById(R.id.btnLogout);
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

        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ScanActivity.class));
                finish();
            }
        });

        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //konfirmasi ke user
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Logout")
                        .setMessage("Anda yakin ingin keluar dari aplikasi?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // hapus smua data sharedpreference login dan service tracker
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear().commit();
                                SharedPreferences ShLogin = getSharedPreferences("login", MODE_PRIVATE);
                                SharedPreferences.Editor ShEdit = ShLogin.edit();
                                ShEdit.clear().commit();
                                ShEdit.putBoolean("isLogin", false);
                                ShEdit.commit();
                                stopService(new Intent(ProfileActivity.this, TrackerService.class));
                                if (mAuth.getCurrentUser() != null) {
                                    mAuth.signOut();
                                }
                                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
