package com.einslabs.ejobsheet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JobsheetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobsheet_view);

        SharedPreferences sharedPreferences = getSharedPreferences("kontrak", MODE_PRIVATE);
        sharedPreferences.getString("kode_pelanggan","");
    }

    private void getData (String keyN, String keyV, String path) {
        MediaType JSON = MediaType.parse("application/x-www-form-urlencoded");
        String requestBody = keyN + "=" + keyV;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, requestBody);

        //start request ke server
        Request request = new Request.Builder()
                .url(getString(R.string.api_url) + "/" + path)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        //get response async
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new AlertDialog.Builder(JobsheetActivity.this)
                        .setTitle("Kesalahan")
                        .setMessage("Tidak dapat menemukan data SN tersebut atau tidak ada koneksi internet")
                        .create().show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response.body().string());
                JsonObject obj = element.getAsJsonObject();
                if (response.code() == 200) {
//                    SharedPreferences sharedPreferences = getSharedPreferences("kontrak", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("no_kontrak", obj.get("no_kontrak").getAsString());
//                    editor.putString("jns_kontrak", obj.get("jns_kontrak").getAsString());
//                    editor.putString("kode_pelanggan", obj.get("kode_pelanggan").getAsString());
//                    editor.putString("sn_mesin", obj.get("sn_mesin").getAsString());
//                    editor.commit();
                    //startActivity(new Intent(JobsheetActivity.this, JobsheetActivity.class));
                    Set<Entry<String, JsonElement>> entries = obj.entrySet();
                    for(Map.Entry<String, JsonElement> entry: entries) {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }
                    finish();
                }else {
                    String status = obj.get("status").getAsString();
                    String msg = obj.get("message").getAsString();
                    new AlertDialog.Builder(JobsheetActivity.this)
                            .setTitle(status)
                            .setMessage(msg)
                            .create().show();
                }
            }
        });
    }
}
