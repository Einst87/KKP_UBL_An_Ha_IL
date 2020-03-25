package com.einslabs.ejobsheet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private Button mScan;
    private TextView txtNm;
    private TextView txtJabatan;
    public String apiUrl = getString(R.string.api_url);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mScan = (Button) findViewById(R.id.btnScan);
//        txtNm = (TextView) findViewById(R.id.txtNm);
//        txtJabatan = (TextView) findViewById(R.id.txtJabatan);

        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        getTeknisiData(email);

        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ScanActivity.class));
                finish();
            }
        });
    }

    private  void getTeknisiData (String email) {
        MediaType urlEn =  MediaType.parse("application/x-www-form-urlencoded");
        String requestBody = "email=" + email;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(urlEn, requestBody);

        //start request ke server
        Request request = new Request.Builder()
                .url(apiUrl + "/teknisi")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getMsg("Tidak dapat menemukan data teknisi atau tidak ada koneksi internet");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response.body().toString());
                JsonObject obj = element.getAsJsonObject();
                txtNm.setText(obj.get("nama").getAsString());
                txtJabatan.setText(obj.get("jabatan").getAsString());
                Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
                SharedPreferences sharedPreferences = getSharedPreferences("teknisi", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    editor.putString(entry.getKey(), entry.getValue().getAsString());
                }
                editor.apply();
            }
        });
    }

    private void getMsg (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
