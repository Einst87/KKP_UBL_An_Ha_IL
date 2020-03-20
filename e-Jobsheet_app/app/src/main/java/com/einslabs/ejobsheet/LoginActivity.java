package com.einslabs.ejobsheet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import static com.firebase.ui.auth.AuthUI.TAG;

public class LoginActivity extends Activity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    private static final int PERMISSIONS_REQUEST = 1;
    private TextView mEmail;
    private TextView mPass;
    private Button mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        mEmail = (TextView) findViewById(R.id.input_email);
        mPass = (TextView)  findViewById(R.id.input_password);
        mLogin = (Button) findViewById(R.id.btn_login);

//        Intent i = getIntent();
//        if (!i.getStringExtra("msg").equals("")){
//            Toast.makeText(this, i.getStringExtra("msg"), Toast.LENGTH_LONG).show();
//        }

        final SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLogin", false)) {
            mEmail.setText(sharedPreferences.getString("email", ""));
            mLogin.setText(sharedPreferences.getString("password", ""));
            startTrackerService();
            finish();
        }

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Nyalakan GPS terlebih dahulu", Toast.LENGTH_LONG).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            //startTrackerService();
            mLogin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (!validateEmail() | !validatePassword()) {
                        return;
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(), mPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Log.d(TAG, "firebase auth success");
                                    //requestLocationUpdates();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLogin", true);
                                    editor.putString("email", mEmail.getText().toString());
                                    editor.putString("password", mPass.getText().toString());
                                    editor.commit();
                                    startTrackerService();
                                    //Intent i = new Intent(, ScanActivity.class);
                                    //startActivity(i);
                                } else {
                                    //Log.d(TAG, "firebase auth failed");
                                    showToast();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLogin", false);
                                    editor.commit();
                                    return;
                                }
                            }
                        });
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    private void startTrackerService() {
        startService(new Intent(this, TrackerService.class));
        startActivity(new Intent(this, ScanActivity.class));
        finish();
    }

    private void showToast(){
        Toast.makeText(getApplicationContext(), "Email atau password salah", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
    }

    private boolean validateEmail() {
        String emailInput = mEmail.getText().toString().trim();

        if (emailInput.isEmpty()) {
            mEmail.setError("Email tidak boleh kosong");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            mEmail.setError("Email tidak valid");
            return false;
        } else {
            mEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = mPass.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            mPass.setError("Password tidak boleh kosong");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            mPass.setError("Password min. 6 karakter atau lebih");
            return false;
        } else {
            mPass.setError(null);
            return true;
        }
    }
}