package com.einslabs.ejobsheet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private FirebaseAuth mAuth;
    private TextView mEmail;
    private TextView mPass;
    private Button mLogin;
    private AlertDialog.Builder builder;
    //public String apiUrl = getString(R.string.api_url);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (TextView) findViewById(R.id.input_email);
        mPass = (TextView) findViewById(R.id.input_password);
        mLogin = (Button) findViewById(R.id.btn_login);
        builder = new AlertDialog.Builder(this);

//        if (sharedPreferences.getBoolean("isLogin", false)) {
//            mEmail.setText(sharedPreferences.getString("email", ""));
//            mLogin.setText(sharedPreferences.getString("password", ""));
//            startTrackerService();
//            finish();
//        }

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            builder.setMessage("nyalakan GPS terlebih dahulu").setTitle("GPS")
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            //startTrackerService();
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateEmail() | !validatePassword()) {
                        return;
                    } else {
                        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.putString("email", mEmail.getText().toString());
                        editor.putString("password", mPass.getText().toString());
                        editor.commit();
                        getTeknisiData(mEmail.getText().toString());
                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        startTrackerService();
//                        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//
//                                } else {
//                                    //Log.d(TAG, "firebase auth failed");
//                                    showDialog("Error", "Email atau password salah");
//                                }
//                            }
//                        });
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
        finish();
    }

    //ambil data teknisi
    private  void getTeknisiData (String email) {
        MediaType urlEn =  MediaType.parse("application/x-www-form-urlencoded");
        String requestBody = "email=" + email;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(urlEn, requestBody);

        //start request ke server
        Request request = new Request.Builder()
                .url("https://field.amidocabang.com/api/teknisi")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showDialog("Gagal", "Tidak dapat terhubung ke server atau tidak ada koneksi internet");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response.body().string());
                JsonObject obj = element.getAsJsonObject();
                if (response.code() == 200) {
                    SharedPreferences sharedPreferences = getSharedPreferences("teknisi", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nik_teknisi", obj.get("nik_teknisi").getAsString());
                    editor.putString("foto_teknisi", obj.get("foto_teknisi").getAsString());
                    editor.putString("nama", obj.get("nama").getAsString());
                    editor.putString("no_hp", obj.get("no_hp").getAsString());
                    editor.putString("email", obj.get("email").getAsString());
                    editor.putString("jabatan", obj.get("jabatan").getAsString());
                    editor.putString("tgl_mulai_krj", obj.get("tgl_mulai_krj").getAsString());
                    editor.putString("wilayah", obj.get("wilayah").getAsString());
                    editor.putString("longitude", obj.get("longitude").getAsString());
                    editor.putString("latitude", obj.get("latitude").getAsString());
                    editor.commit();
                }else{
                    String status = obj.get("status").getAsString();
                    String msg = obj.get("message").getAsString();
                    showDialog(status, msg);
                }
            }
        });
    }

//    private void updateUI (FirebaseUser user) {
//        if (user != null){
//            //mEmail.setText(user.getEmail());
//            //Uri photoUrl = user.getPhotoUrl();
//            startService(new Intent(this, TrackerService.class));
//            startActivity(new Intent(this, ProfileActivity.class));
//            finish();
//        }
//    }

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

    private void showDialog(final String status, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                builder.setMessage(msg).setTitle(status).create().show();
            }
        });
    }

//    @Override
//    public void onStart () {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

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