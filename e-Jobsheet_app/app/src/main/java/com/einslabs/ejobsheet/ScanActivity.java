package com.einslabs.ejobsheet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent ( this, ProfileActivity.class);
        startActivity(i);
        super.onBackPressed();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Log.v("TAG", rawResult.getText()); // Prints scan results
//        Log.v("TAG", rawResult.getBarcodeFormat().toString());
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
//        Intent i = new Intent(this, JobsheetActivity.class);
//        SharedPreferences sharedPreferences = getSharedPreferences("Task", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("SN", rawResult.getText());
//        editor.apply();
        getKontrakDataBySN(rawResult.getText());

        mScannerView.resumeCameraPreview(this);
    }

    private void getKontrakDataBySN (String SN) {
        MediaType JSON = MediaType.parse("application/x-www-form-urlencoded");
        String requestBody = "SN=" + SN;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, requestBody);

        //start request ke server
        Request request = new Request.Builder()
                .url(getString(R.string.api_url) + "/kontrak")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        //get response async
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new AlertDialog.Builder(ScanActivity.this)
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
                    SharedPreferences sharedPreferences = getSharedPreferences("kontrak", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("no_kontrak", obj.get("no_kontrak").getAsString());
                    editor.putString("jns_kontrak", obj.get("jns_kontrak").getAsString());
                    editor.putString("kode_pelanggan", obj.get("kode_pelanggan").getAsString());
                    editor.putString("sn_mesin", obj.get("sn_mesin").getAsString());
                    editor.commit();
                    startActivity(new Intent(ScanActivity.this, JobsheetActivity.class));
                    finish();
                }else {
                    String status = obj.get("status").getAsString();
                    String msg = obj.get("message").getAsString();
                    new AlertDialog.Builder(ScanActivity.this)
                            .setTitle(status)
                            .setMessage(msg)
                            .create().show();
                }
            }
        });
    }
}
